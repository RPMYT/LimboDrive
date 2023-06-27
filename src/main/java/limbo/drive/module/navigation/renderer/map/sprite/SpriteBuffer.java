package limbo.drive.module.navigation.renderer.map.sprite;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import limbo.drive.module.navigation.renderer.map.tile.CollisionType;
import limbo.drive.module.navigation.renderer.map.tile.TileBuffer;
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
        int startX = 116 + context.posX + offsetX;
        int startY = 8 + context.posY + offsetY;

        if (others.length > 0) {
            if (others[0] instanceof TileBuffer tiles) {
                this.checkCollision(tiles);
            }
        }

        for (SpriteBufferData.Sprite sprite : this.contents) {
            switch (sprite.type()) {
                case OBJECT -> {
                    SpriteBufferData.ObjectSprite object = (SpriteBufferData.ObjectSprite) sprite;
                    if (context.visible.getLeft().apply(object.location())) {
                        ScreenDrawing.texturedRect(context.context,
                                (object.x()) + startX,
                                (object.y()) + startY,
                                object.width(),
                                object.height(),
                                new Identifier("limbodrive:textures/gui/sprites/objects/" + object.texture() + ".png"),
                                0xFF_FFFFFF);
                    }
                }

                case CHARACTER -> {
                    Pair<Integer, Integer> position = new Pair<>(sprite.x(), sprite.y());
                    if (context.visible.getRight().apply(position)) {
                        String texture = ((SpriteBufferData.Character) sprite).texture();
                        if (!texture.contains("_")) {
                            texture = texture + "_south";
                        }

                        ScreenDrawing.texturedRect(context.context,
                                position.getLeft() + startX,
                                position.getRight() + startY,
                                ((SpriteBufferData.Character) sprite).width(),
                                ((SpriteBufferData.Character) sprite).height(),
                                new Identifier("limbodrive:textures/gui/sprites/characters/" + texture + ".png"),
                                0xFF_FFFFFF);
                    }
                }
            }
        }
    }

    private void checkCollision(TileBuffer tiles) {
        for (SpriteBufferData.Sprite sprite : this.contents) {
            Optional<TileBuffer.Tile> tile = tiles.getTileAt(sprite.x() / 8, sprite.y() / 8);
            if (tile.isPresent() && tile.get().data().type() == CollisionType.SOLID) {
                Pair<Integer, Integer> previous = previousLocations.get(sprite);
                sprite.reposition(previous.getLeft(), previous.getRight());
            }
        }
    }
}
