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
import limbo.drive.module.navigation.renderer.map.sprite.SpriteBufferData;
import limbo.drive.module.navigation.renderer.map.tile.*;
import limbo.drive.util.NotReallyFinal;
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
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final int MAP_SIZE_X = 748;
    private static final int MAP_SIZE_Y = 394;
    private static final int MAP_COLOUR = 0xFF_2A2A2A;

    private static final int EDITOR_TILESET_SIZE_X = 130;
    private static final int EDITOR_TILESET_SIZE_Y = 394;
    private static String EDITOR_TILESET_SELECTED = "debug";
    private static int EDITOR_TILE_SELECTED = 0;
    private static int EDITOR_TILE_PAGE = 0;

    private static SpriteBuffer SPRITES = null;

    private static final HashMap<Pair<Integer, Integer>, ArrayList<Pair<Integer, Integer>>> DRAW_LOCATIONS = new HashMap<>();

    private static final ArrayList<Pair<Pair<Integer, Integer>, TileData>> EDITOR_TILES = new ArrayList<>();

    public NavigationGUI() {
        super(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.COLOUR,
                new Identifier(
                    "limbodrive",
                    "navigator"
                ),
                752,
                400,
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

        SPRITES = null;
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

    @SuppressWarnings("SuspiciousNameCombination")
    private static void drawMapEditor(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context,
                context.posX + MAP_OFFSET_X,
                context.posY + MAP_OFFSET_Y,
                EDITOR_TILESET_SIZE_X,
                EDITOR_TILESET_SIZE_Y,
                MAP_COLOUR);

        DRAW_LOCATIONS.clear();
        int currentlyDrawing = EDITOR_TILE_PAGE * 507;
        for (int column = 1; column < 39; column++) {
            for (int row = 0; row < 13; row++) {
                ScreenDrawing.texturedRect(context.context,
                        (context.posX + MAP_OFFSET_X + 1 + (row * 8)) + (2 * row),
                        context.posY + MAP_OFFSET_Y + 4 + (column * 8) + (2 * column),
                        8,
                        8,
                        new Identifier("limbodrive:textures/gui/tilesets/" + EDITOR_TILESET_SELECTED + "/" + currentlyDrawing + ".png"),
                        0xFF_FFFFFF);

                ArrayList<Pair<Integer, Integer>> LOCATIONS = new ArrayList<>();
                int drawStartX = MAP_OFFSET_X + 1 + (row * 8) + (2 * row);
                int drawStartY = MAP_OFFSET_Y + 4 + (column * 8) + (2 * column);
                for (int positionX = drawStartX; positionX < drawStartX + 8; positionX++) {
                    for (int positionY = drawStartY; positionY < drawStartY + 8; positionY++) {
                        LOCATIONS.add(new Pair<>(positionX, positionY));
                    }
                }
                DRAW_LOCATIONS.put(new Pair<>(row, column), LOCATIONS);

                if (EDITOR_TILE_SELECTED == currentlyDrawing) {
                    ScreenDrawing.coloredRect(context.context,
                            (context.posX + MAP_OFFSET_X + 1 + (row * 8)) + (2 * row),
                            context.posY + MAP_OFFSET_Y + 4 + (column * 8) + (2 * column),
                            8,
                            8,
                            0x6F_FFFF00);
                }
                currentlyDrawing++;
            }
        }

        ScreenDrawing.drawStringWithShadow(context.context,
                "<== Current Page: " + EDITOR_TILE_PAGE + " ==>",
                HorizontalAlignment.CENTER,
                context.posX + MAP_OFFSET_X + 14,
                context.posY + MAP_OFFSET_Y + 3,
                102,
                0xFF_FFFFFF);

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
        TileBuffer tiles = (TileBuffer) new BackgroundLayer(
                20,
                12,
                EDITOR_TILESET_SELECTED,
                EDITOR_TILES.toArray(new Pair[0])
        ).draw(TileBuffer.create(20, 12));

        if (SPRITES == null) {
            SPRITES = SpriteBuffer.create(-131, -1)
                    .add(new SpriteBufferData.AnchoredCharacterSprite(
                            ctx.location.getRight(),
                            ctx.width,
                            ctx.height,
                            0,
                            new NotReallyFinal<>(ctx.texture)
                        )
                    );
        }

        MapRenderer renderer = new MapRenderer(tiles, SPRITES);
        renderer.render(ctx);
    }

    private static void drawMapViewer(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context,
                context.posX + MAP_OFFSET_X,
                context.posY + MAP_OFFSET_Y,
                MAP_SIZE_X,
                MAP_SIZE_Y,
                MAP_COLOUR);
    }

    @SuppressWarnings("DataFlowIssue")
    private static void handleMouseClicks(PB3K.InputData data) {
        switch (MODE) {
            case EDITOR_MAP -> {
                if (data.mouseButton() == 0) {
                    int tileX = (int) (float) (data.mouseX() - 4) / 32;
                    int tileY = (int) (float) (data.mouseY() - 4) / 32;

                    if (tileX <= 5) {
                        AtomicInteger tile = new AtomicInteger(0);
                        AtomicBoolean found = new AtomicBoolean(false);
                        DRAW_LOCATIONS.forEach((index, locations) -> {
                            if (!found.get()) {
                                for (Pair<Integer, Integer> location : locations) {
                                    if (found.get()) {
                                        break;
                                    }

                                    if (Objects.equals(location.getLeft(), data.mouseX()) && Objects.equals(location.getRight(), data.mouseY())) {
                                        found.set(true);
                                        tile.set(index.getLeft() + ((index.getRight() - 1) * 13));
                                        System.out.println("Found tile, ID " + tile.get());
                                        break;
                                    }  //System.out.println("Scanning next location " + location.getLeft() + ", " + location.getRight() + " tile index " + index.getLeft() + ", " + index.getRight() + " mouse location " + (data.mouseX()) + ", " + (data.mouseY()));

                                }
                            }
                        });
                        System.out.println("Click location, found: " + found.get());

                        if (found.get()) {
                            EDITOR_TILE_SELECTED = tile.get();
                            System.out.println("Selected new tile " + EDITOR_TILE_SELECTED);
                            return;
                        } else {
                            if (tileY == 0) {
                                if (tileX == 0) {
                                    System.out.println("Decrementing page");
                                    EDITOR_TILE_PAGE--;
                                    if (EDITOR_TILE_PAGE < 0) {
                                        EDITOR_TILE_PAGE = 0;
                                    }
                                }

                                if (tileX == 3) {
                                    EDITOR_TILE_PAGE++;
                                }
                            }
                        }

                        return;
                    }
                    System.out.println("Tile X: " + tileX);

                    int minitileX = ((int) (float) (data.mouseX() - 4) / 8);
                    int minitileY = (int) (float) (data.mouseY() - 4) / 8;

                    EDITOR_TILES.add(new Pair<>(new Pair<>((minitileX - 25), minitileY - 1), new TileData(
                        EDITOR_TILE_SELECTED * (EDITOR_TILE_PAGE + 1),
                        EDITOR_TILESET_SELECTED,
                        CollisionType.SOLID,
                        TileProperties.NONE
                    )));
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
        boolean west = false;

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_W)) {
            SPRITES.getPlayer().reposition(0, -1);
            north = true;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_A)) {
            SPRITES.getPlayer().reposition(-1, 0);
            west = true;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_S)) {
            SPRITES.getPlayer().reposition(0, 1);
            south = true;
            north = false;
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_D)) {
            SPRITES.getPlayer().reposition(1, 0);
            east = true;
            west = false;
        }

        String texture = SPRITES.getPlayer().texture().contains("_") ?
                SPRITES.getPlayer().texture().substring(0, SPRITES.getPlayer().texture().indexOf("_")) :
                SPRITES.getPlayer().texture();
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

        SPRITES.getPlayer().retexture(texture);
    }
}