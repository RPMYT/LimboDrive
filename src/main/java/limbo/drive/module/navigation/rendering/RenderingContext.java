package limbo.drive.module.navigation.rendering;

import limbo.drive.module.navigation.gui.NavigationGUI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

public class RenderingContext {
    public final DrawContext context;
    public int renderPositionX;
    public int renderPositionY;

    public final String room;
    public String texture;
    public int width;
    public int height;

    public int playerPositionX;
    public int playerPositionY;

    public RenderMode mode;


    /**
     *
     * @param context The {@link DrawContext}, used for rendering
     * @param renderPositionX The currently being drawn X position
     * @param renderPositionY The currently being drawn Y position
     * @param room The room currently being rendered. Entirely arbitrary.
     * @param texture The texture of the player character. This MUST have directional variants!!
     * @param width The width of the player character, in pixels.
     * @param height The height of the player character, in pixels.
     * @param startX The starting X position of the player character.
     * @param startY The starting Y position of the player character.
     * @param mode The {@link RenderMode} in use currently. Used by {@link NavigationGUI} for drawing the map.
     */
    public RenderingContext(DrawContext context,
                            int renderPositionX,
                            int renderPositionY,
                            String room,
                            String texture,
                            int width,
                            int height,
                            int startX,
                            int startY,
                            RenderMode mode
    ) {
        this.mode = mode;
        this.room = room;
        this.context = context;
        this.renderPositionX = renderPositionX;
        this.renderPositionY = renderPositionY;

        this.width = width;
        this.height = height;
        this.texture = texture;
        this.playerPositionX = startX;
        this.playerPositionY = startY;
    }

    public NbtCompound serialize() {
        NbtCompound compound = new NbtCompound();

        compound.putString("PlayerTexture", texture);
        compound.putInt("PlayerWidth", width);
        compound.putInt("PlayerHeight", height);

        compound.putInt("PlayerPositionX", this.playerPositionX);
        compound.putInt("PlayerPositionY", this.playerPositionY);

        compound.putString("CurrentRoom", room);

        return compound;
    }

    public static RenderingContext deserialize(NbtCompound compound) {
        return new RenderingContext(
            null,
            0,
            0,
            compound.getString("CurrentRoom"),
            compound.getString("PlayerTexture"),

            compound.getInt("PlayerWidth"),
            compound.getInt("PlayerHeight"),

            compound.getInt("PlayerPositionX"),
            compound.getInt("PlayerPositionY"),

            RenderMode.MAP_VIEWER
        );
    }

    public boolean isTileVisible(int x, int y) {
        return true;
    }

    public boolean isSpriteVisible(int x, int y) {
        return true;
    }
}
