package limbo.drive.module.navigation.gui;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import limbo.drive.module.navigation.rendering.MapRenderer;
import limbo.drive.module.navigation.rendering.RenderMode;
import limbo.drive.module.navigation.rendering.RenderingContext;
import limbo.drive.module.navigation.util.NavigationData;
import limbo.drive.module.navigation.util.NavigationPermissions;
import limbo.drive.util.graphics.PB3K;
import limbo.drive.util.graphics.gui.BackgroundType;
import limbo.drive.util.graphics.gui.BorderType;
import limbo.drive.util.graphics.gui.DisplayProperties;
import limbo.drive.util.graphics.gui.GuiBase;
import limbo.drive.module.world.item.LimboNavigatorItem;
import limbo.drive.util.data.PacketIdentifiers;
import limbo.drive.module.navigation.map.sprite.SpriteBuffer;
import limbo.drive.module.navigation.map.sprite.SpriteBufferData;
import limbo.drive.module.navigation.map.tile.*;
import limbo.drive.util.stupidity.NotReallyFinal;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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

        NavigationData.SPRITES = null;
    }

    public static void contextualize(RenderingContext context) {
        NavigationData.CURRENT_CONTEXT = context;
    }

    private static void draw(DrawContext context, int posX, int posY) {
        RenderingContext current = new RenderingContext(
            context,
            posX,
            posY,
            NavigationData.CURRENT_CONTEXT.room,

            NavigationData.CURRENT_CONTEXT.texture,
            NavigationData.CURRENT_CONTEXT.width,
            NavigationData.CURRENT_CONTEXT.height,
            NavigationData.CURRENT_CONTEXT.playerPositionX,
            NavigationData.CURRENT_CONTEXT.playerPositionY,

            NavigationData.CURRENT_CONTEXT.mode
        );

        if (NavigationData.SPRITES == null) {

            //noinspection SuspiciousNameCombination
            NavigationData.SPRITES = SpriteBuffer.create(-131, -1)
                .add(new SpriteBufferData.AnchoredCharacterSprite(
                        new Pair<>(current.playerPositionX, current.playerPositionY),
                        current.width,
                        current.height,
                        0,
                        new NotReallyFinal<>(current.texture)
                    )
                );
        }

        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        if (!NavigationData.DEBUG_REQUESTED
            && InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_LEFT_CONTROL)
            && InputUtil.isKeyPressed(handle, InputUtil.GLFW_KEY_GRAVE_ACCENT)
        ) {
            NavigationData.DEBUG_REQUESTED = true;
            ClientPlayNetworking.send(PacketIdentifiers.REQUEST_DEBUG_MODE, PacketByteBufs.empty());
        }

        switch (NavigationData.CURRENT_CONTEXT.mode) {
            case MAP_EDITOR -> drawMapEditor(current);
            case MAP_VIEWER -> drawMapViewer(current);
        }

        handleMovement();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof LimboNavigatorItem) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.put("NavigationContext", NavigationData.CURRENT_CONTEXT.serialize());
            stack.setNbt(nbt);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void drawMapEditor(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context,
                context.renderPositionX + NavigationData.MAP_OFFSET_X,
                context.renderPositionY + NavigationData.MAP_OFFSET_Y,
                NavigationData.EDITOR_TILESET_SIZE_X,
                NavigationData.EDITOR_TILESET_SIZE_Y,
                NavigationData.MAP_COLOUR);

        DRAW_LOCATIONS.clear();
        int currentlyDrawing = NavigationData.EDITOR_TILE_PAGE * 507;
        for (int column = 1; column < 39; column++) {
            for (int row = 0; row < 13; row++) {
                ScreenDrawing.texturedRect(context.context,
                        (context.renderPositionX + NavigationData.MAP_OFFSET_X + 1 + (row * 8)) + (2 * row),
                        context.renderPositionY + NavigationData.MAP_OFFSET_Y + 4 + (column * 8) + (2 * column),
                        8,
                        8,
                        new Identifier(
                            "limbodrive:textures/gui/tilesets/" + NavigationData.EDITOR_TILESET_SELECTED + "/" + currentlyDrawing + ".png"
                        ),
                        0xFF_FFFFFF);

                ArrayList<Pair<Integer, Integer>> LOCATIONS = new ArrayList<>();
                int drawStartX = NavigationData.MAP_OFFSET_X + 1 + (row * 8) + (2 * row);
                int drawStartY = NavigationData.MAP_OFFSET_Y + 4 + (column * 8) + (2 * column);
                for (int positionX = drawStartX; positionX < drawStartX + 8; positionX++) {
                    for (int positionY = drawStartY; positionY < drawStartY + 8; positionY++) {
                        LOCATIONS.add(new Pair<>(positionX, positionY));
                    }
                }
                DRAW_LOCATIONS.put(new Pair<>(row, column), LOCATIONS);

                if (NavigationData.EDITOR_TILE_SELECTED == currentlyDrawing) {
                    ScreenDrawing.coloredRect(context.context,
                            (context.renderPositionX + NavigationData.MAP_OFFSET_X + 1 + (row * 8)) + (2 * row),
                            context.renderPositionY + NavigationData.MAP_OFFSET_Y + 4 + (column * 8) + (2 * column),
                            8,
                            8,
                            0x6F_FFFF00);
                }
                currentlyDrawing++;
            }
        }

        ScreenDrawing.drawStringWithShadow(context.context,
                "<== Current Page: " + NavigationData.EDITOR_TILE_PAGE + " ==>",
                HorizontalAlignment.CENTER,
                context.renderPositionX + NavigationData.MAP_OFFSET_X + 14,
                context.renderPositionY + NavigationData.MAP_OFFSET_Y + 3,
                102,
                0xFF_FFFFFF);

        RenderingContext ctx = new RenderingContext(
            context.context,
            context.renderPositionX + 218,
            context.renderPositionY + 8,
            context.room,
            context.texture,
            context.width,
            context.height,
            context.playerPositionX,
            context.playerPositionY,
            context.mode
        );

        //noinspection unchecked
        TileBuffer tiles = (TileBuffer) new BackgroundLayer(
                20,
                12,
                NavigationData.EDITOR_TILESET_SELECTED,
                EDITOR_TILES.toArray(new Pair[0])
        ).draw(TileBuffer.create(20, 12));

        MapRenderer renderer = new MapRenderer(tiles, NavigationData.SPRITES);
        renderer.render(ctx);
    }

    private static void drawMapViewer(RenderingContext context) {
        ScreenDrawing.coloredRect(context.context,
                context.renderPositionX + NavigationData.MAP_OFFSET_X,
                context.renderPositionY + NavigationData.MAP_OFFSET_Y,
                NavigationData.MAP_SIZE_X,
                NavigationData.MAP_SIZE_Y,
                NavigationData.MAP_COLOUR);
    }

    private static void handleMouseClicks(PB3K.InputData data) {
        switch (NavigationData.CURRENT_CONTEXT.mode) {
            case MAP_EDITOR -> handleEditorClicks(data);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void handleEditorClicks(PB3K.InputData data) {
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
                                break;
                            }
                        }
                    }
                });

                if (found.get()) {
                    NavigationData.EDITOR_TILE_SELECTED = tile.get();
                    return;
                } else {
                    if (tileY == 0) {
                        if (tileX == 0) {
                            NavigationData.EDITOR_TILE_PAGE--;
                            if (NavigationData.EDITOR_TILE_PAGE < 0) {
                                NavigationData.EDITOR_TILE_PAGE = 0;
                            }
                        }

                        if (tileX == 3) {
                            NavigationData.EDITOR_TILE_PAGE++;
                        }
                    }
                }

                return;
            }

            int minitileX = ((int) (float) (data.mouseX() - 4) / 8);
            int minitileY = (int) (float) (data.mouseY() - 4) / 8;

            EDITOR_TILES.add(new Pair<>(new Pair<>((minitileX - 25), minitileY - 1), new TileData(
                NavigationData.EDITOR_TILE_SELECTED * (NavigationData.EDITOR_TILE_PAGE + 1),
                NavigationData.EDITOR_TILESET_SELECTED,
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

    @SuppressWarnings("DataFlowIssue")
    private static void handleKeyboardInput(PB3K.InputData data) {
        if (NavigationData.DEBUG_ENABLED && NavigationPermissions.DEBUG_ENABLE) {
            char typed = (char) (data.character().intValue());

            switch (typed) {
                case '1' -> NavigationData.CURRENT_CONTEXT.mode = RenderMode.MAP_VIEWER;

                case '2' -> {
                    if (NavigationPermissions.DEBUG_EDIT_MAPS) {
                        NavigationData.CURRENT_CONTEXT.mode = RenderMode.MAP_EDITOR;
                    }
                }

                case '3' -> {
                    if (NavigationPermissions.DEBUG_EDIT_BATTLES) {
                        NavigationData.CURRENT_CONTEXT.mode = RenderMode.BATTLE_EDITOR;
                    }
                }
            }

            NavigationData.DEBUG_ENABLED = false;
        }

        if (NavigationPermissions.DEBUG_ENABLE) {
            if ((char) data.character().intValue() == '`') {
                NavigationData.DEBUG_ENABLED = true;
            }
        }
    }

    private static void handleMovement() {
        if (NavigationData.SPRITES == null) {
            return;
        }

        if (NavigationData.SPRITES.getPlayer() == null) {
            return;
        }

        String append = "";

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_W)) {
            NavigationData.SPRITES.getPlayer().reposition(0, -1);
            append = "_north";
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_S)) {
            NavigationData.SPRITES.getPlayer().reposition(0, 1);
            append = "_south";
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_A)) {
            NavigationData.SPRITES.getPlayer().reposition(-1, 0);
            append = append + "_west";
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_D)) {
            NavigationData.SPRITES.getPlayer().reposition(1, 0);
            append = append + "_east";
        }

        String texture = NavigationData.SPRITES.getPlayer().texture().contains("_") ?
                NavigationData.SPRITES.getPlayer().texture().substring(0, NavigationData.SPRITES.getPlayer().texture().indexOf("_")) :
                NavigationData.SPRITES.getPlayer().texture();

        if (texture.length() == 0) {
            System.out.println("Zero-length texture?!");
        }

        if (!append.equals("")) {
            NavigationData.SPRITES.getPlayer().retexture(texture + append);
        }
    }
}