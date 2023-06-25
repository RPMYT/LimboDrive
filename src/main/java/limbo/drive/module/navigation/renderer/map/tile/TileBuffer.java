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
    private record Tile(int x, int y, int index, int tileset) {}

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

    public TileBuffer add(int x, int y, int index, int tileset) {
        contents.add(new Tile(
            x,
            y,
            index,
            tileset
        ));

        return this;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected void flush(RenderingContext context) {
        int startX = 12 + context.posX;
        int startY = 6 + context.posY;

        for (Tile tile : this.contents) {
            if (context.visible.getLeft().apply(new Pair<>(tile.x, tile.y))) {
                if (tile.x <= this.width && tile.y <= this.height) {
                    ScreenDrawing.texturedRect(context.context,
                        (tile.x * 32) + startX,
                        (tile.y * 32) + startY,
                        32,
                        32,
                        new Identifier("limbodrive:textures/gui/tiles/" + tile.tileset + "/" + tile.index + ".png"),
                        0xFF_FFFFFF);
                }
            }
        }
    }

    public TileBuffer clear() {
        this.contents.clear();
        return this;
    }
}