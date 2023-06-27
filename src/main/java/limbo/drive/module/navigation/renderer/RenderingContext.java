package limbo.drive.module.navigation.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;

import java.util.function.Function;

public class RenderingContext {
    public final DrawContext context;
    public int posX;
    public int posY;

    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> location;
    public final Pair<Function<Pair<Integer, Integer>, Boolean>, Function<Pair<Integer, Integer>, Boolean>> visible;

    public final String room;
    public String texture;
    public int width;
    public int height;


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
    public RenderingContext(DrawContext context,
                            int posX,
                            int posY,
                            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> location,
                            Pair<Function<Pair<Integer, Integer>, Boolean>, Function<Pair<Integer, Integer>, Boolean>> visible,
                            String room,
                            String texture,
                            int width,
                            int height
    ) {
        this.context = context;
        this.posX = posX;
        this.posY = posY;
        this.location = location;
        this.visible = visible;
        this.room = room;
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

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
            new Pair<>(new Pair<>(compound.getInt("PlayerTileX"), compound.getInt("PlayerTileY")),
                    new Pair<>(compound.getInt("PlayerSpriteX"), compound.getInt("PlayerSpriteY"))),
            new Pair<>(tiles -> true, sprites -> true),
            compound.getString("CurrentRoom"),
            compound.getString("PlayerTexture"),
            compound.getInt("PlayerWidth"),
            compound.getInt("PlayerHeight")
        );
    }
}
