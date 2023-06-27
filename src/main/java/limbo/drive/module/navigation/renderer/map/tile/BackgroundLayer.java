package limbo.drive.module.navigation.renderer.map.tile;

import limbo.drive.module.navigation.renderer.gui.RenderBuffer;
import net.minecraft.util.Pair;

public record BackgroundLayer(int width, int height, String tileset, Pair<Pair<Integer, Integer>, TileData>... tiles)  {
    @SafeVarargs
    public BackgroundLayer {
    }

    public RenderBuffer draw(RenderBuffer buffer) {
        if (buffer instanceof TileBuffer) {
            for (Pair<Pair<Integer, Integer>, TileData> tile : tiles) {
                Pair<Integer, Integer> location = tile.getLeft();
                ((TileBuffer) buffer).add(location.getLeft(), location.getRight(), tile.getRight());
            }
        }

        return buffer;
    }
}
