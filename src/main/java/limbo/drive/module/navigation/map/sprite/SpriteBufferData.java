package limbo.drive.module.navigation.map.sprite;

import limbo.drive.module.navigation.map.tile.CollisionType;
import limbo.drive.util.stupidity.NotReallyFinal;
import net.minecraft.util.Pair;

public class SpriteBufferData {

    public interface Sprite {
        Type type();

        enum Type {
            OBJECT,
            CHARACTER
        }

        int x();
        int y();

        int width();
        int height();

        void reposition(int deltaX, int deltaY);

        String texture();
    }

    public interface Object extends Sprite {
        @Override
        default Type type() {
            return Type.OBJECT;
        }
    }

    public interface Character extends Sprite {
        @Override
        default Type type() {
            return Type.CHARACTER;
        }

        MovementType movement();

        enum MovementType {
            ANCHORED,
            CHASING,
            BOTH
        }

        void retexture(String updated);
    }

    public record ObjectSprite(Pair<Integer, Integer> location, int width, int height, String texture) implements Object {
        @Override
        public void reposition(int deltaX, int deltaY) {
            int previousX = this.location.getLeft();
            int previousY = this.location.getRight();
            SpriteBuffer.previousLocations.put(this, this.location);
            this.location.setLeft(previousX + deltaX);
            this.location.setRight(previousY + deltaY);
        }

        @Override
        public int x() { return this.location.getLeft(); }

        @Override
        public int y() { return this.location.getRight(); }
    }
    public record AnchoredCharacterSprite(
            Pair<Integer, Integer> location,
            int width,
            int height,
            int radius,
            NotReallyFinal<String> textureNRF
    ) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.ANCHORED;
        }

        @Override
        public void reposition(int deltaX, int deltaY) {
            int previousX = this.location.getLeft();
            int previousY = this.location.getRight();

            if (SpriteBuffer.TILES != null) {
                if (SpriteBuffer.TILES.getCollisionAt((previousX+deltaX)/8, (previousY+deltaY)/8) != CollisionType.SOLID
                    && SpriteBuffer.TILES.getCollisionAt(((previousX+deltaX)/8)+1, ((previousY+deltaY)/8)+1) != CollisionType.SOLID) {
                    SpriteBuffer.previousLocations.put(this, new Pair<>(previousX, previousY));
                    this.location.setLeft(previousX + deltaX);
                    this.location.setRight(previousY + deltaY);
                }
            }
        }

        @Override
        public int x() { return this.location.getLeft(); }

        @Override
        public int y() { return this.location.getRight(); }

        @Override
        public void retexture(String updated) { this.textureNRF.value = updated; }

        public String texture() { return this.textureNRF.value; }
    }
    public record ChasingCharacterSprite(
            Pair<Integer, Integer> location,
            int width,
            int height,
            int speed,
            NotReallyFinal<String> textureNRF
    ) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.CHASING;
        }

        @Override
        public void reposition(int deltaX, int deltaY) {
            int previousX = this.location.getLeft();
            int previousY = this.location.getRight();
            SpriteBuffer.previousLocations.put(this, this.location);
            this.location.setLeft(previousX + deltaX);
            this.location.setRight(previousY + deltaY);
        }

        @Override
        public int x() { return this.location.getLeft(); }

        @Override
        public int y() { return this.location.getRight(); }

        @Override
        public void retexture(String updated) { this.textureNRF.value = updated; }

        public String texture() { return this.textureNRF.value; }
    }
    public record AnchoredChasingCharacterSprite(
            Pair<Integer, Integer> location,
            int width,
            int height,
            int speed,
            int radius,
            NotReallyFinal<String> textureNRF
    ) implements Character {
        @Override
        public MovementType movement() {
            return MovementType.BOTH;
        }

        @Override
        public void reposition(int deltaX, int deltaY) {
            int previousX = this.location.getLeft();
            int previousY = this.location.getRight();
            SpriteBuffer.previousLocations.put(this, this.location);
            this.location.setLeft(previousX + deltaX);
            this.location.setRight(previousY + deltaY);
        }

        @Override
        public int x() { return this.location.getLeft(); }

        @Override
        public int y() { return this.location.getRight(); }

        @Override
        public void retexture(String updated) { this.textureNRF.value = updated; }

        public String texture() { return this.textureNRF.value; }
    }
}
