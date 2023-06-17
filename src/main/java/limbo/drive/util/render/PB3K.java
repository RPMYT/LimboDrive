package limbo.drive.util.render;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
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

import java.util.ArrayList;
import java.util.HashMap;

public class PB3K {
    static final Logger LOGGER = LogManager.getLogger("Pixel Blaster 3000");

    private static ScreenHandlerType<RenderTarget> HANDLER;

    public static void initialize() {
        LOGGER.info("Initializing display...");
        HandledScreens.<RenderTarget, Screen>register(HANDLER, (handler, playerInventory, title) -> new Screen(handler, playerInventory));
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

        RENDER.put(RenderStage.SETUP, new ArrayList<>());
        RENDER.put(RenderStage.BACKGROUND, new ArrayList<>());
        RENDER.put(RenderStage.TEXTURES, new ArrayList<>());
        RENDER.put(RenderStage.CONTROLS, new ArrayList<>());
        RENDER.put(RenderStage.STRINGS, new ArrayList<>());
        RENDER.put(RenderStage.EFFECTS, new ArrayList<>());
    }

    public static class RenderTarget extends SyncedGuiDescription {
        private static Identifier TARGET = new Identifier(
            "limbodrive",
            "error"
        );

        public static void initialize(Identifier target) {
            TARGET = target;
        }

        public RenderTarget(int syncId, PlayerInventory playerInventory) {
            super(HANDLER, syncId, playerInventory);

            Display display = new Display(TARGET);
            this.setRootPanel(display);
            display.validate(this);
        }

        @Override
        public void onClosed(PlayerEntity player) {
            super.onClosed(player);
            TARGET = new Identifier(
                "limbodrive",
                "error"
            );
        }
    }

    static final HashMap<InputType, ArrayList<InputCallback>> INPUT = new HashMap<>();
    static final HashMap<RenderStage, ArrayList<RenderCallback>> RENDER = new HashMap<>();

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

    public static void registerRenderHandler(RenderStage stage, RenderCallback handler) {
        ArrayList<RenderCallback> handlers = RENDER.get(stage);
        handlers.add(handler);

        RENDER.put(stage, handlers);
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

    public static void registerInputHandler(InputType type, InputCallback handler) {
        ArrayList<InputCallback> handlers = INPUT.getOrDefault(type, new ArrayList<>());
        handlers.add(handler);

        INPUT.put(type, handlers);
    }
}
