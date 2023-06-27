package limbo.drive.module.navigation.renderer;

import com.google.common.collect.ImmutableList;
import limbo.drive.module.navigation.renderer.map.sprite.SpriteBuffer;
import limbo.drive.module.navigation.renderer.map.tile.TileBuffer;
import net.minecraft.util.Pair;

public class MapRenderer {
    private final RenderBuffer tiles;
    private final RenderBuffer sprites;

    public MapRenderer(TileBuffer tiles, SpriteBuffer sprites) {
        this.tiles = tiles;
        this.sprites = sprites;
    }

    public void render(RenderingContext context) {
        this.tiles.flush(context);
        this.sprites.flush(context, tiles);
    }
}