package limbo.drive.module.navigation.map.sprite;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.rendering.RenderBuffer;
import limbo.drive.module.navigation.rendering.RenderingContext;
import limbo.drive.module.navigation.map.tile.CollisionType;
import limbo.drive.module.navigation.map.tile.TileBuffer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class SpriteBuffer extends RenderBuffer {
    
    public SpriteBufferData.Character getPlayer() {
        return (SpriteBufferData.Character) this.contents.get(0);
    }

    private final ArrayList<SpriteBufferData.Sprite> contents = new ArrayList<>();
    static final HashMap<SpriteBufferData.Sprite, Pair<Integer, Integer>> previousLocations = new HashMap<>();

    static TileBuffer TILES = null;

    private final int offsetX;
    private final int offsetY;

    private SpriteBuffer(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public SpriteBuffer add(SpriteBufferData.Sprite sprite) {
        this.contents.add(sprite);
        return this;
    }

    public static SpriteBuffer create(int offsetX, int offsetY) {
        return new SpriteBuffer(offsetX, offsetY);
    }

    @Override
    protected void flush(RenderingContext context, RenderBuffer... others) {
        int startX = 116 + context.renderPositionX + offsetX;
        int startY = 8 + context.renderPositionY + offsetY;

        if (others.length > 0) {
            if (others[0] instanceof TileBuffer tiles) {
                TILES = tiles;
            }
        }

        for (SpriteBufferData.Sprite sprite : this.contents) {
            if (context.isSpriteVisible(sprite.x(), sprite.y())) {
                ScreenDrawing.texturedRect(context.context,
                    sprite.x() + startX,
                    sprite.y() + startY,
                    sprite.width(),
                    sprite.height(),
                    new Identifier("limbodrive:textures/gui/sprites/" + sprite.type().name().toLowerCase() + "/" + sprite.texture() + ".png"),
                    0xFF_FFFFFF);
            }
        }
    }

    private void checkCollision(TileBuffer tiles) {
        for (SpriteBufferData.Sprite sprite : this.contents) {
            Optional<TileBuffer.Tile> tile = tiles.getTileAt(sprite.x() / 8, sprite.y() / 8);
            if (tile.isPresent() && tile.get().data().collision() == CollisionType.SOLID) {
                Pair<Integer, Integer> previous = previousLocations.get(sprite);

                if (previous != null) {
                    System.out.println("Previous: " + previous.getLeft() + ", " + previous.getRight());
                    System.out.println("Current: " + sprite.x() + ", " + sprite.y());
                    sprite.reposition(previous.getLeft() / 8, previous.getRight() / 8);
                }
            }
        }
    }
}
