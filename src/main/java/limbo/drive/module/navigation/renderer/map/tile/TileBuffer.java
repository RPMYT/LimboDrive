package limbo.drive.module.navigation.renderer.map.tile;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class TileBuffer extends RenderBuffer {
    public record Tile(int x, int y, TileData data) {}

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
            data
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
                        (tile.x * 8) + startX,
                        (tile.y * 8) + startY,
                        8,
                        8,
                        new Identifier("limbodrive:textures/gui/tilesets/" + tile.data.tileset() + "/" + tile.data.index() + ".png"),
                        0xFF_FFFFFF
                    );
                }
            }
        }
    }

    public TileBuffer clear() {
        this.contents.clear();
        return this;
    }
}