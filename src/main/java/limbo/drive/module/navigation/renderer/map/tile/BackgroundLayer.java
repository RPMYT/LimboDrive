package limbo.drive.module.navigation.renderer.map.tile;

import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.map.layer.LayerType;
import limbo.drive.module.navigation.renderer.map.layer.MapLayer;

public record BackgroundLayer(int width, int height, Tileset tileset, String... tiles) implements MapLayer {
    @Override
    public LayerType type() {
        return LayerType.BACKGROUND;
    }

    @Override
    public RenderBuffer draw(RenderBuffer buffer) {
        if (buffer instanceof TileBuffer) {
            for (int column = 0; column < tiles.length; column++) {
                for (int row = 0; row < tiles[column].length(); row++) {
                    ((TileBuffer) buffer).textured(row, column, tileset.tiles.get(tiles[column].charAt(row)));
                }
            }
        }

        return buffer;
    }
}
