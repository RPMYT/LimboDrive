package limbo.drive.module.navigation;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.api.graphics.core.gui.BackgroundType;
import limbo.drive.api.graphics.core.gui.BorderType;
import limbo.drive.api.graphics.core.gui.DisplayProperties;
import limbo.drive.api.graphics.core.gui.GuiBase;
import limbo.drive.module.navigation.renderer.MapRenderer;
import limbo.drive.module.navigation.renderer.RenderingContext;
import limbo.drive.module.navigation.renderer.data.TestingRooms;
import limbo.drive.module.navigation.renderer.map.sprite.SpriteBuffer;
import limbo.drive.module.navigation.renderer.map.tile.TileBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NavigationGUI extends GuiBase {
    private static RenderingContext CURRENT_CONTEXT;

    public NavigationGUI() {
        super(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.COLOUR,
                new Identifier(
                    "limbodrive",
                    "navigator"
                ),
                820,
                450,
                0xFF_1F2041,
                null,
                null,
                new Identifier(
                    "limbodrive",
                    "textures/gui/starmap.png"
                )
            ),
            null,
            null,
            null,
            Lists.newArrayList(),
            Lists.newArrayList(
                (context, stage, display, posX, posY, mouseX, mouseY) -> draw(context, posX, posY)
            )
        );
    }

    public static void contextualize(RenderingContext context) {
        CURRENT_CONTEXT = context;
    }

    private static void draw(DrawContext context, int posX, int posY) {
        RenderingContext current = new RenderingContext(
            context,
            posX,
            posY,
            CURRENT_CONTEXT.location(),
            CURRENT_CONTEXT.visible(),
            CURRENT_CONTEXT.room(),
            CURRENT_CONTEXT.texture(),
            CURRENT_CONTEXT.width(),
            CURRENT_CONTEXT.height()
        );

        int MAP_OFFSET_X = 8;
        int MAP_OFFSET_Y = 2;
        int MAP_SIZE_X = 800;
        int MAP_SIZE_Y = 446;
        int MAP_COLOUR = 0xFF_2A2A2A;
        ScreenDrawing.coloredRect(context, posX + MAP_OFFSET_X, posY + MAP_OFFSET_Y, MAP_SIZE_X, MAP_SIZE_Y, MAP_COLOUR);

        MapRenderer.render(current, TestingRooms.PLAINS.draw(TileBuffer.create(25, 25)));

        MapRenderer.render(current, SpriteBuffer.create(0, 0)
            .characterAnchored(current.location().getRight().getLeft(), current.location().getRight().getRight(), current.width(), current.height(), 0, current.texture())
        );

        handleMovement();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof LimboNavigatorItem) {
            stack.getOrCreateNbt().put("NavigationContext", CURRENT_CONTEXT.serialize());
        }
    }

    private static void handleMovement() {
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_W)) {
            CURRENT_CONTEXT.updateSpriteLocation(0, -1);
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_A)) {
            CURRENT_CONTEXT.updateSpriteLocation(-1, 0);
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_S)) {
            CURRENT_CONTEXT.updateSpriteLocation(0, 1);
        }

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_D)) {
            CURRENT_CONTEXT.updateSpriteLocation(1, 0);
        }
    }
}