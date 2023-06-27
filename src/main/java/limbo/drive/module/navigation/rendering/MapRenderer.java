package limbo.drive.module.navigation.rendering;

import limbo.drive.module.navigation.map.sprite.SpriteBuffer;
import limbo.drive.module.navigation.map.tile.TileBuffer;

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