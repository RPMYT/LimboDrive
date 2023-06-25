package limbo.drive.module.navigation.renderer.map.tile;

import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.map.layer.LayerType;
import limbo.drive.module.navigation.renderer.map.layer.MapLayer;
import net.minecraft.util.Pair;

public record BackgroundLayer(int width, int height, int tileset, Pair<Pair<Integer, Integer>, Integer>... tiles) implements MapLayer {
    @Override
    public LayerType type() {
        return LayerType.BACKGROUND;
    }

    @Override
    public RenderBuffer draw(RenderBuffer buffer) {
        if (buffer instanceof TileBuffer) {
            for (Pair<Pair<Integer, Integer>, Integer> tile : tiles) {
                Pair<Integer, Integer> location = tile.getLeft();
                ((TileBuffer) buffer).add(location.getLeft(), location.getRight(), tile.getRight(), tileset);
            }
        }

        return buffer;
    }
}
