package limbo.drive.module.navigation.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;

import java.util.function.Function;

/**
 *
 * @param context The {@link DrawContext}, used for rendering
 * @param posX The currently being drawn X position
 * @param posY The currently being drawn Y position
 * @param location The tile (left) or sprite (right) X/Y coordinate of the player character
 * @param visible Checks if a given tile (left) or sprite (right) is visible
 * @param room The room currently being rendered
 * @param texture The texture of the player character
 * @param width The width of the player character
 * @param height The height of the player character
 */
public record RenderingContext(DrawContext context, int posX, int posY, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> location, Pair<Function<Pair<Integer, Integer>, Boolean>, Function<Pair<Integer, Integer>, Boolean>> visible, String room, String texture, int width, int height) {
    public NbtCompound serialize() {
        NbtCompound compound = new NbtCompound();

        compound.putString("PlayerTexture", texture);
        compound.putInt("PlayerWidth", width);
        compound.putInt("PlayerHeight", height);

        compound.putInt("PlayerTileX", location.getLeft().getLeft());
        compound.putInt("PlayerTileY", location.getLeft().getRight());

        compound.putInt("PlayerSpriteX", location.getRight().getLeft());
        compound.putInt("PlayerSpriteY", location.getRight().getRight());

        compound.putString("CurrentRoom", room);

        return compound;
    }

    public static RenderingContext deserialize(NbtCompound compound) {
        return new RenderingContext(
            null,
            0,
            0,
            new Pair<>(new Pair<>(compound.getInt("PlayerTileX"), compound.getInt("PlayerTileY")), new Pair<>(compound.getInt("PlayerSpriteX"), compound.getInt("PlayerSpriteY"))),
            new Pair<>(tiles -> true, sprites -> true),
            compound.getString("CurrentRoom"),
            compound.getString("PlayerTexture"),
            compound.getInt("PlayerWidth"),
            compound.getInt("PlayerHeight")
        );
    }

    public void updateTileLocation(int deltaX, int deltaY) {
        Pair<Integer, Integer> tile = this.location.getLeft();
        int previousX = tile.getLeft();
        int previousY = tile.getRight();
        tile.setLeft(previousX + deltaX);
        tile.setRight(previousY + deltaY);
        this.location.setLeft(tile);
    }

    public void updateSpriteLocation(int deltaX, int deltaY) {
        Pair<Integer, Integer> sprite = this.location.getRight();
        int previousX = sprite.getLeft();
        int previousY = sprite.getRight();
        sprite.setLeft(previousX + deltaX);
        sprite.setRight(previousY + deltaY);
        this.location.setRight(sprite);
    }
}
