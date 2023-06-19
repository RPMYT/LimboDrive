package limbo.drive.old.starfield;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WPanelWithInsets;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import limbo.drive.LimboDrive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StarmapGUI extends CottonInventoryScreen<StarmapGUI.Description> {
    static boolean closed = false;

    public StarmapGUI(Description description, PlayerInventory inventory) {
        super(description, inventory);
        closed = false;
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (closed) {
            closed = false;
            this.close();
        }
    }

    public static class Description extends SyncedGuiDescription {
        public Description(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
            super(LimboDrive.Screens.STARMAP, syncId, inventory, getBlockInventory(context, 4), getBlockPropertyDelegate(context));

            Renderer renderer = new Renderer();
            this.setRootPanel(renderer);
            renderer.validate(this);
        }
    }

    public static class Renderer extends WPanelWithInsets {
        private Page currentPage = Page.MENU;
        private Page previousPage = Page.MENU;
        private PageSection currentSection;

        private int gtnh = 0;

        private boolean attemptedUnimplementedAccess = false;

        public static class Textures {
            private static final Identifier STEVE = new Identifier("limbodrive", "textures/gui/steve.png");
            private static final Identifier BUTTON = new Identifier("limbodrive", "textures/gui/button.png");
            private static final Identifier BACKGROUND = new Identifier("limbodrive", "textures/gui/starmap.png");
        }
        public Renderer() {
            this.setInsets(Insets.ROOT_PANEL);
            this.setSize(410, 225);
        }

        @Override
        @Environment(EnvType.CLIENT)
        public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
            ScreenDrawing.texturedRect(context, x, y, width, height, Textures.BACKGROUND, 0xFF_FFFFFF);

            if (this.currentSection == PageSection.Secret.SECRET) {
                MinecraftServer server = MinecraftClient.getInstance().getServer();
                PlayerManager manager = server.getPlayerManager();
                manager.broadcast(Text.translatable("limbodrive.what.the.fuck", MinecraftClient.getInstance().player.getName()), false);
                this.currentSection = null;
            }

            String key;
            for (int i = 1; i <= 5; i++) {
                int top = (y + (i * 36)) - 12;

                key = switch (this.currentPage) {
                    case MENU -> switch (i) {
                        case 1 -> "limbodrive.gui.button.starfield";
                        case 2 -> "limbodrive.gui.button.leaderboard";
                        case 3 -> "limbodrive.gui.button.profile";
                        case 4 -> "limbodrive.gui.button.settings";
                        case 5 -> "limbodrive.gui.button.exit";

                        default -> "limbodrive.gui.button.wtf";
                    };

                    case STARFIELD -> switch (i) {
                        case 1 -> "limbodrive.gui.button.map";
                        case 2 -> "limbodrive.gui.button.combat";
                        case 3 -> "limbodrive.gui.button.fleet";
                        case 4 -> "limbodrive.gui.button.missions";
                        case 5 -> "limbodrive.gui.button.back";

                        default -> "limbodrive.gui.button.wtf";
                    };

                    case SETTINGS, LEADERBOARD -> "limbodrive.gui.button.bigwtf";

                    case PROFILE -> switch (i) {
                        case 1 -> "limbodrive.gui.button.alliance";
                        case 2 -> "limbodrive.gui.button.achievements";
                        case 3 -> "limbodrive.gui.button.statistics";
                        case 4 -> "limbodrive.gui.button.rewards";
                        case 5 -> "limbodrive.gui.button.back";

                        default -> "limbodrive.gui.button.wtf";
                    };
                };

                ScreenDrawing.texturedRect(context, x + 8, top, 96, 32, Textures.BUTTON, 0xFF_FFFFFF);
                ScreenDrawing.drawString(context, Text.translatable(key).asOrderedText(), x + 16, top + 12, 0xFF_FFFFFF);
            }

            if (this.currentPage == Page.PROFILE) {

            }

            if (attemptedUnimplementedAccess) {
                ScreenDrawing.coloredRect(context, x + 8 + 108, y + 13, 261, 200, (gtnh >= 4 ? 0x67746E68 : 0xFF_0040FF));

                ScreenDrawing.drawString(context, "Sorry! " +
                    (gtnh >= 4 ? "GregTech" : "That feature")
                    + " isn't implemented yet!", x + 8 + 134, y + 20, 0xFF_FFFFFF);

                ScreenDrawing.drawString(context,
                    gtnh == 4 ? "Have you heard of our lord and savior GregoriusT" : "(But it will be in the future, so look forward to it!)",
                    x + 8 + 114 - (gtnh == 4 ? 4 : 0), y + 200, 0xFF_FFFFFF);


                ScreenDrawing.coloredRect(context, x + 8 + 124, y + 48, 16, 16, 0xFF_FFFFFF);
                ScreenDrawing.coloredRect(context, x + 8 + 124, y + 160, 16, 16, 0xFF_FFFFFF);
                ScreenDrawing.coloredRect(context, x + 8 + 155, y + 58, 16, 16, 0xFF_FFFFFF);
                ScreenDrawing.coloredRect(context, x + 8 + 155, y + 150, 16, 16, 0xFF_FFFFFF);
                ScreenDrawing.coloredRect(context, x + 8 + 150, y + 64, 16, 96, 0xFF_FFFFFF);
            }

            if (this.currentSection != null) {
                switch (this.currentPage) {
                    case STARFIELD -> {
                        switch ((PageSection.Starfield) this.currentSection) {
                            case MAP -> {
                                ScreenDrawing.coloredRect(context, x + 8 + 102, (y + 9), 256, 16, 0xFF_1F2041);
                                ScreenDrawing.coloredRect(context, x + 8 + 102 + 256, (y + 9), 16, 208, 0xFF_1F2041);
                                ScreenDrawing.coloredRect(context, x + 8 + 102, (y + 9) + 192, 256, 16, 0xFF_1F2041);
                                ScreenDrawing.coloredRect(context, x + 8 + 102, (y + 9), 16, 208, 0xFF_1F2041);

//                                ScreenDrawing.coloredRect(context, x + 8 + 102 + 128, (y + 9), 16, 96, 0xFF_637074);
//                                ScreenDrawing.coloredRect(context, x + 8 + 102 + 128 , (y + 9) + 112, 16, 96, 0xFF_637074);
//                                ScreenDrawing.coloredRect(context, x + 8 + 102 + 144, (y + 9) + 96, 128, 16, 0xFF_637074);
//                                ScreenDrawing.coloredRect(context, x + 8 + 102, (y + 9) + 96, 128, 16, 0xFF_637074);
//
//                                ScreenDrawing.coloredRect(context, x + 8 + 102 + 128, (y + 9) + 96, 16, 16, 0xFF_C74A00);


                            }

                            case COMBAT -> {

                            }

                            case FLEET -> {

                            }

                            case MISSIONS -> {

                            }
                        }
                    }

                    case PROFILE -> {
                        switch ((PageSection.Profile) this.currentSection) {
                            case STATISTICS -> {
                                String name = MinecraftClient.getInstance().player.getName().getString();
                                ScreenDrawing.drawString(context, name, x + 8 + 125 + (name.length() / 2), (y + 20), 0xFF_FFFFFF);
                                ScreenDrawing.coloredRect(context, x + 8 + 125, (y + 32), 58, 94, 0xFF_FFFFFF);
                                ScreenDrawing.texturedRect(context, x + 8 + 132, (y + 34), 45, 90, Textures.STEVE, 0xFF_FFFFFF);
                            }
                        }
                    }
                }
            } else {
                switch (this.currentPage) {
                    case STARFIELD -> this.currentSection = PageSection.Starfield.MAP;
                    case PROFILE -> this.currentSection = PageSection.Profile.STATISTICS;
                }
            }
        }



        @Override
        @Environment(EnvType.CLIENT)
        public InputResult onClick(int x, int y, int button) {
            if ((x >= 67 && x <= 72 && y >= 40 && y <= 43 && gtnh == 0)
                || (x >= 196 && x <= 198 && y >= 22 && y <= 25 && gtnh == 1)
                || (x >= 256 && x <= 260 && y >= 22 && y <= 27 && gtnh == 2)
                || (x >= 185 && x <= 188 && y >= 21 && y <= 28 && gtnh == 3)
                || gtnh >= 4) {

                    this.gtnh++;
                    System.out.print(switch (this.gtnh) {
                        case 1 -> "Greg";
                        case 2 -> "Tech ";
                        case 3 -> "New ";
                        case 4 -> "Horizons!\n";
                        default -> "";
                    });

                    if (this.gtnh >= 4) {
                        this.gtnh = 4;
                    }

                    return InputResult.PROCESSED;
                }

            if (this.gtnh > 0) {
                this.gtnh = 0;
                LimboDrive.LOGGER.fatal("You are not worthy.");
            }

            if (x >= 8 && x <= 96 && y >= 33 && y <= 199) {
                int clicked = (((y - 33) + 32) / 32) - 1;
                this.attemptedUnimplementedAccess = false;

                if (this.currentPage == Page.MENU) {
                    if (clicked == 4) {
                        StarmapGUI.closed = true;
                    } else {
                        this.previousPage = this.currentPage;
                        this.currentPage = Page.values()[clicked];
                    }
                } else {
                    if (clicked == 4) {
                        Page temp = this.currentPage;
                        this.currentPage = this.previousPage;
                        this.previousPage = temp;
                    } else {
                        try {
                            this.currentSection = switch (this.currentPage) {
                                case STARFIELD -> PageSection.Starfield.values()[clicked];
                                case LEADERBOARD -> PageSection.Leaderboard.values()[clicked];
                                case PROFILE -> PageSection.Profile.values()[clicked];
                                case SETTINGS -> PageSection.Settings.values()[clicked];

                                default -> {
                                    LimboDrive.LOGGER.error("Can I get an upwarp burger with that cosmic ray soda?");
                                    yield PageSection.Secret.SECRET;
                                }
                            };
                        } catch (ArrayIndexOutOfBoundsException exception) {
                            this.attemptedUnimplementedAccess = true;
                        }
                    }
                }
            }

            return InputResult.PROCESSED;
        }

        private enum Page {
            STARFIELD,
            LEADERBOARD,
            PROFILE,
            SETTINGS,
            MENU
        }
            
        public interface PageSection {
            enum Starfield implements PageSection {
                MAP,
                COMBAT,
                FLEET,
                MISSIONS
            }

            enum Leaderboard implements PageSection {

            }

            enum Profile implements PageSection {
                ALLIANCE,
                ACHIEVEMENTS,
                STATISTICS,
                REWARDS
            }

            enum Settings implements PageSection {

            }

            enum Secret implements PageSection {
                SECRET
            }
        }
    }
}
