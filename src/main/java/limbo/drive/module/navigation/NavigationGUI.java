package limbo.drive.module.navigation;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.api.graphics.core.gui.BackgroundType;
import limbo.drive.api.graphics.core.gui.BorderType;
import limbo.drive.api.graphics.core.gui.DisplayProperties;
import limbo.drive.api.graphics.core.gui.GuiBase;
import limbo.drive.module.navigation.renderer.MapRenderer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import limbo.drive.module.navigation.renderer.map.sprite.SpriteBuffer;
import limbo.drive.module.navigation.renderer.map.tile.BackgroundLayer;
import limbo.drive.module.navigation.renderer.map.tile.TileBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class NavigationGUI extends GuiBase {
    private enum ViewMode {
        EDITOR_MAP,
        EDITOR_BATTLE,

        PLAYER_MAP,
        PLAYER_BATTLE,
    }

    private static ViewMode MODE = ViewMode.PLAYER_MAP;
    private static RenderingContext CURRENT_CONTEXT;

    private static final int MAP_OFFSET_X = 4;
    private static final int MAP_OFFSET_Y = 2;
    private static final int MAP_SIZE_X = 812;
    private static final int MAP_SIZE_Y = 446;
    private static final int MAP_COLOUR = 0xFF_2A2A2A;

    private static final int EDITOR_TILESET_SIZE_X = 135;
    private static final int EDITOR_TILESET_SIZE_Y = 439;
    private static int EDITOR_TILESET_SELECTED = 0;
    private static int EDITOR_TILE_SELECTED = 0;
    private static int EDITOR_TILE_PAGE = 0;

    private static ArrayList<Pair<Pair<Integer, Integer>, Integer>> EDITOR_TILES = new ArrayList<>();

    public NavigationGUI() {
        super(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.COLOUR,
                new Identifier(
                    "limbodrive",
                    "navigator"
                ),
                820,
                450,
                0xFF_1F2041,
                null,
                null,
                new Identifier(
                    "limbodrive",
                    "textures/gui/starmap.png"
                )
            ),
            null,
            Lists.newArrayList(
                NavigationGUI::handleMouseClicks
            ),
            Lists.newArrayList(
                NavigationGUI::handleKeyboardInput
            ),
            Lists.newArrayList(),
            Lists.newArrayList(
                (context, stage, display, posX, posY, mouseX, mouseY) -> draw(context, posX, posY)
            )
        );
    }

    public static void contextualize(RenderingContext context) {
        CURRENT_CONTEXT = context;
    }

    private static void draw(DrawContext context, int posX, int posY) {
        RenderingContext current = new RenderingContext(
            context,
            posX,
            posY,
            CURRENT_CONTEXT.location,
            CURRENT_CONTEXT.visible,
            CURRENT_CONTEXT.room,
            CURRENT_CONTEXT.texture,
            CURRENT_CONTEXT.width,
            CURRENT_CONTEXT.height
        );
        handleModeSwitch();

        switch (MODE) {
            case EDITOR_MAP -> drawMapEditor(current);
            case PLAYER_MAP -> drawMapViewer(current);
        }

        MapRenderer.render(current, SpriteBuffer.create(0, 0)
            .characterAnchored(current.location.getRight().getLeft(), current.location.getRight().getRight(), current.width, current.height, 0, current.texture)
        );

        handleMovement();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof LimboNavigatorItem) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.put("NavigationContext", CURRENT_CONTEXT.serialize());
            stack.setNbt(nbt);
        }
    }

    private static void drawMapEditor(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context, context.posX + MAP_OFFSET_X, context.posY + MAP_OFFSET_Y, EDITOR_TILESET_SIZE_X, EDITOR_TILESET_SIZE_Y, MAP_COLOUR);

        int currentlyDrawing = EDITOR_TILE_PAGE * 66;
        for (int column = 0; column < 12; column++) {
            if (column == 0) {
                for (int tileset = EDITOR_TILESET_SELECTED; tileset < EDITOR_TILESET_SELECTED + 4; tileset++) {
                    ScreenDrawing.texturedRect(context.context, (context.posX + MAP_OFFSET_X + ((tileset + 1) * 32)) + (2 * tileset) + 2, context.posY + MAP_OFFSET_Y + 1, 32, 32, new Identifier("limbodrive:textures/gui/tiles/" + tileset + "/icon.png"), 0xFF_FFFFFF);
                }
            } else {
                for (int row = 0; row < 6; row++) {
                    ScreenDrawing.texturedRect(context.context, (context.posX + MAP_OFFSET_X + (row * 32)) + (2 * row), context.posY + MAP_OFFSET_Y + (column * 32) + (2 * column), 32, 32, new Identifier("limbodrive:textures/gui/tiles/" + EDITOR_TILESET_SELECTED + "/" + currentlyDrawing + ".png"), 0xFF_FFFFFF);
                    if (EDITOR_TILE_SELECTED == currentlyDrawing) {
                        ScreenDrawing.coloredRect(context.context, (context.posX + MAP_OFFSET_X + (row * 32)) + (2 * row), context.posY + MAP_OFFSET_Y + (column * 32) + (2 * column), 32, 32, 0x6F_FFFF00);
                    }
                    currentlyDrawing++;
                }
            }
        }

        ScreenDrawing.drawStringWithShadow(context.context, "<======== Current Page: " + EDITOR_TILE_PAGE + " ========>", HorizontalAlignment.CENTER, context.posX + MAP_OFFSET_X + 9, context.posY + MAP_OFFSET_Y + 426, 190,  0xFF_FFFFFF);

        RenderingContext ctx = new RenderingContext(
            context.context,
            context.posX + 218,
            context.posY + 8,
            context.location,
            context.visible,
            context.room,
            context.texture,
            context.width,
            context.height
        );

        //noinspection unchecked
        MapRenderer.render(ctx, new BackgroundLayer(
            20,
            12,
            EDITOR_TILESET_SELECTED,
            EDITOR_TILES.toArray(new Pair[0])
        ).draw(TileBuffer.create(20, 12)));
    }

    private static void drawMapViewer(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context, context.posX + MAP_OFFSET_X, context.posY + MAP_OFFSET_Y, MAP_SIZE_X, MAP_SIZE_Y, MAP_COLOUR);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void handleMouseClicks(PB3K.InputData data) {
        switch (MODE) {
            case EDITOR_MAP -> {
                if (data.mouseButton() == 0) {
                    int tileX = (int) (float) (data.mouseX() - 4) / 32;
                    int tileY = (int) (float) (data.mouseY()) / 32;

                    System.out.println("Location: " + (data.mouseX() - 4) + ", " + (data.mouseY() - 4));

                    if (tileY == 13) {
                        if (tileX == 0 || tileX == 1) {
                            System.out.println("Decrementing page");
                            EDITOR_TILE_PAGE--;
                            if (EDITOR_TILE_PAGE < 0) {
                                EDITOR_TILE_PAGE = 0;
                            }
                        }

                        if (tileX == 5 || tileX == 6) {
                            EDITOR_TILE_PAGE++;
                        }
                        return;
                    }

                    if (tileX <= 5) {
                        tileY = (int) (float) ((data.mouseY() - (tileY * 2.15))) / 32;
                        tileX = (int) (float) ((data.mouseX() - 6) + (tileX * 2)) / 32;


                        System.out.println("Tile: " + tileX + ", " + tileY);
                        EDITOR_TILE_SELECTED = (((6 * (tileY - 1)) + tileX) - (tileX == 6 ? 1 : 0) + (EDITOR_TILE_PAGE * 66));
                        System.out.println("Selected new tile " + EDITOR_TILE_SELECTED);
                        return;
                    }

                    System.out.println("Tile: " + tileX + ", " + tileY);

                    tileY = (int) (float) ((data.mouseY() - (tileY * 2.15))) / 32;
                    EDITOR_TILES.add(new Pair<>(new Pair<>(tileX - 7, tileY), EDITOR_TILE_SELECTED));
                }

                if (data.mouseButton() == 2) {
                    EDITOR_TILES.clear();
                }

                if (data.mouseButton() == 1 && EDITOR_TILES.size() > 0) {
                    EDITOR_TILES.remove(EDITOR_TILES.size()-1);
                }
            }

            case EDITOR_BATTLE -> {

            }
        }
    }

    private static void handleKeyboardInput(PB3K.InputData data) {

    }

    private static void handleModeSwitch() {
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_GRAVE_ACCENT)) {
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_M)) {
                MODE = ViewMode.EDITOR_MAP;
            }

            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_P)) {
                MODE = MODE == ViewMode.EDITOR_BATTLE ? ViewMode.PLAYER_BATTLE : ViewMode.PLAYER_MAP;
            }
        }
    }

    private static void handleMovement() {
        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west= false;

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_W)) {
            CURRENT_CONTEXT.updateSpriteLocation(0, -1);
            north = true;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_A)) {
            CURRENT_CONTEXT.updateSpriteLocation(-1, 0);
            west = true;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_S)) {
            CURRENT_CONTEXT.updateSpriteLocation(0, 1);
            south = true;
            north = false;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_D)) {
            CURRENT_CONTEXT.updateSpriteLocation(1, 0);
            east = true;
            west = false;
        }

        String texture = CURRENT_CONTEXT.texture.contains("_") ? CURRENT_CONTEXT.texture.substring(0, CURRENT_CONTEXT.texture.indexOf("_")) : CURRENT_CONTEXT.texture;
        if (texture.length() == 0) {
            System.out.println("Zero-length texture?!");
        }

        if (north) {
            texture = texture + "_north";
        }

        if (south) {
            texture = texture + "_south";
        }

        if (east) {
            texture = texture + "_east";
        }

        if (west) {
            texture = texture + "_west";
        }
        CURRENT_CONTEXT.texture = texture;
    }
}