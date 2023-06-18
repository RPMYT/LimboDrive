package limbo.drive.util.render.core;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import limbo.drive.util.render.gui.BackgroundType;
import limbo.drive.util.render.gui.BorderType;
import limbo.drive.util.render.gui.DisplayProperties;
import limbo.drive.util.render.gui.GuiBase;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class PB3K {
    public static final Logger LOGGER = LogManager.getLogger("Pixel Blaster 3000");

    private static ScreenHandlerType<RenderTarget> HANDLER;

    public static void initialize() {
        LOGGER.info("Initializing display...");
        HandledScreens.<PB3K.RenderTarget, Screen>register(HANDLER, (handler, playerInventory, title) -> new Screen(handler, playerInventory));
    }

    public static void setup() {
        LOGGER.info("Starting the Pixel Blaster, stand by for launch!");
        HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier(
                "limbodrive",
                "pixel_blaster_3000"
            ),
            new ScreenHandlerType<>(RenderTarget::new,
                FeatureFlags.VANILLA_FEATURES)
        );
    }

    public static class RenderTarget extends SyncedGuiDescription {
        private static final GuiBase ERROR = new GuiBase(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.NONE,
                new Identifier(
                    "limbodrive",
                    "error"
                ),
                410,
                225,
                null,
                null,
                null,
                new Identifier(
                    "limbodrive",
                    "textures/gui/error.png"
                )
            ),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList()
        ) {};

        private static GuiBase TARGET_GUI = ERROR;

        public static void initialize(GuiBase gui) {
            TARGET_GUI = gui;
        }

        public RenderTarget(int syncId, PlayerInventory playerInventory) {
            super(HANDLER, syncId, playerInventory);

            Display display = new Display(TARGET_GUI);
            this.setRootPanel(display);
            display.validate(this);
        }

        @Override
        public void onClosed(PlayerEntity player) {
            super.onClosed(player);
            TARGET_GUI = ERROR;
        }
    }

    public enum RenderStage {
        SETUP,
        BACKGROUND,
        TEXTURES,
        CONTROLS,
        STRINGS,
        EFFECTS
    }

    @FunctionalInterface
    public interface RenderCallback {
        void render(DrawContext context, RenderStage stage, Display display, int posX, int posY, int mouseX, int mouseY);
    }

    public enum InputType {
        MOUSE,
        KEYBOARD,
    }

    public record InputData(
        InputType type,
        Identifier where,
        @Nullable Integer mouseX,
        @Nullable Integer mouseY,
        @Nullable Integer mouseButton,
        @Nullable Integer scrollAmount,
        @Nullable Integer character,
        @Nullable Integer scancode,
        @Nullable Integer modifiers
    ) {}

    @FunctionalInterface
    public interface InputCallback {
        void process(InputData data);
    }
}
