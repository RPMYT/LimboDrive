package limbo.drive.module.navigation.map.tile;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.rendering.RenderBuffer;
import limbo.drive.module.navigation.rendering.RenderingContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

public class TileBuffer extends RenderBuffer {
    public record Tile(int x, int y, TileData data) {}

    private final int width;
    private final int height;
    private final ArrayList<Tile> contents = new ArrayList<>();

    private TileBuffer(int width, int height) {
        this.width = width * 16;
        this.height = height * 16;
    }

    public static TileBuffer create(int width, int height) {
        return new TileBuffer(width, height);
    }

    public TileBuffer add(int x, int y, TileData data) {
        contents.add(new Tile(
            Math.max(0, x),
            Math.max(0, y),
            data
        ));

        return this;
    }

    public CollisionType getCollisionAt(int x, int y) {
        Optional<Tile> tile = this.getTileAt(x ,y);
        return tile.isEmpty() ? CollisionType.SOLID : tile.get().data.collision();
    }

    public Optional<Tile> getTileAt(int x, int y) {
        for (Tile content : this.contents) {
            if (content.x == x && content.y == y) {
                return Optional.of(content);
            }
        }

        return Optional.empty();
    }

    protected void flush(RenderingContext context, RenderBuffer... others) {
        int startX = context.renderPositionX - 12;
        int startY = 6 + context.renderPositionY;

        for (Tile tile : this.contents) {
            if (context.isTileVisible(tile.x, tile.y)) {
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