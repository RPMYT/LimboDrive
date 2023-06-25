package limbo.drive.module.navigation.renderer.map.tile;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileBuffer extends RenderBuffer {
    private record Tile(int x, int y, int colour, TileData data) {}

    private final int width;
    private final int height;
    private ArrayList<Tile> contents = new ArrayList<>();

    private TileBuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static TileBuffer create(int width, int height) {
        return new TileBuffer(width, height);
    }

    public TileBuffer add(int x, int y, TileData data) {
        contents.add(new Tile(
            x,
            y,
            0x00_000000,
            data
        ));

        return this;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected void flush(RenderingContext context) {
        int startX = 12 + context.posX;
        int startY = 6 + context.posY;

        for (Tile tile : this.contents) {
            if (!tile.data.texture().equals("empty")) {
                if (context.visible.getLeft().apply(new Pair<>(tile.x, tile.y))) {
                    if (tile.x <= this.width && tile.y <= this.height) {
                        ScreenDrawing.texturedRect(context.context,
                            (tile.x * tile.data.width()) + startX,
                            (tile.y * tile.data.height()) + startY,
                            tile.data.width(),
                            tile.data.height(),
                            new Identifier("limbodrive:textures/gui/tiles/" + tile.data.texture() + ".png"),
                            tile.colour != 0 ? tile.colour : 0xFF_FFFFFF);
                    }
                }
            } else if (tile.colour != 0) {
                if (context.visible.getLeft().apply(new Pair<>(tile.x, tile.y))) {
                    if (tile.x <= this.width && tile.y <= this.height) {
                        ScreenDrawing.coloredRect(context.context, (tile.x * 16) + startX, (tile.y * 16) + startY, 16, 16, tile.colour);
                    }
                }
            }
        }
    }

    public TileBuffer clear() {
        this.contents.clear();
        return this;
    }
}