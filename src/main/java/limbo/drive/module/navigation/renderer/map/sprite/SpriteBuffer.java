package limbo.drive.module.navigation.renderer.map.sprite;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.module.navigation.renderer.RenderBuffer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import limbo.drive.module.navigation.renderer.map.tile.TileBuffer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class SpriteBuffer extends RenderBuffer {
    private interface Sprite {
        Type type();

        enum Type {
            OBJECT,
            CHARACTER
        }
    }

    private interface Object extends Sprite {
        @Override
        default Type type() {
            return Type.OBJECT;
        }
    }

    private interface Character extends Sprite {
        @Override
        default Type type() {
            return Type.CHARACTER;
        }

        MovementType movement() ;

        public enum MovementType {
            ANCHORED,
            CHASING,
            BOTH
        }

        int x();
        int y();
        int width();
        int height();
        String texture();
    }

    private record ObjectSprite(int x, int y, int width, int height, String texture) implements Object {}
    private record AnchoredCharacterSprite(int x, int y, int width, int height, int radius, String texture) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.ANCHORED;
        }
    }
    private record ChasingCharacterSprite(int x, int y, int width, int height, int speed, String texture) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.CHASING;
        }
    }
    private record AnchoredChasingCharacterSprite(int x, int y, int width, int height, int speed, int radius, String texture) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.BOTH;
        }
    }

    private final ArrayList<Sprite> contents = new ArrayList<>();

    private final int offsetX;
    private final int offsetY;

    private SpriteBuffer(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public SpriteBuffer object(int x, int y, int width, int height, String texture) {
        contents.add(new ObjectSprite(x, y, width, height, texture));
        return this;
    }

    public SpriteBuffer characterAnchored(int x, int y, int width, int height, int radius, String texture) {
        contents.add(new AnchoredCharacterSprite(x, y, width, height, radius, texture));
        return this;
    }

    public SpriteBuffer characterChasing(int x, int y, int width, int height, int speed, String texture) {
        contents.add(new ChasingCharacterSprite(x, y, width, height, speed, texture));
        return this;
    }

    public SpriteBuffer characterAnchoredChasing(int x, int y, int width, int height, int radius, int speed, String texture) {
        contents.add(new AnchoredChasingCharacterSprite(x, y, width, height, speed, radius, texture));
        return this;
    }

    public static SpriteBuffer create(int offsetX, int offsetY) {
        return new SpriteBuffer(offsetX, offsetY);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void flush(RenderingContext context) {
        int startX = 116 + context.posX + offsetX;
        int startY = 8 + context.posY + offsetY;

        for (Sprite sprite : this.contents) {
            switch (sprite.type()) {
                case OBJECT -> {
                    ObjectSprite object = (ObjectSprite) sprite;
                    if (context.visible.getLeft().apply(new Pair<>(object.x, object.y))) {
                        ScreenDrawing.texturedRect(context.context, (object.x) + startX, (object.y) + startY, object.width, object.height, new Identifier("limbodrive:textures/gui/sprites/objects/" + object.texture + ".png"), 0xFF_FFFFFF);
                    }
                }

                case CHARACTER -> {
                    Pair<Integer, Integer> position = new Pair<>(((Character) sprite).x(), ((Character) sprite).y());
                    if (context.visible.getRight().apply(position)) {
                        String texture = ((Character) sprite).texture();
                        if (!texture.contains("_")) {
                            texture = texture + "_south";
                        }

                        ScreenDrawing.texturedRect(context.context, position.getLeft() + startX, position.getRight() + startY, ((Character) sprite).width(), ((Character) sprite).height(), new Identifier("limbodrive:textures/gui/sprites/characters/" + texture + ".png"), 0xFF_FFFFFF);
                    }
                }
            }
        }
    }
}
