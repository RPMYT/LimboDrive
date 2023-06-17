package limbo.drive.util.render;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class PB3K {
    private static ScreenHandlerType<RenderTarget> HANDLER;

    public static void initialize() {
        HandledScreens.<RenderTarget, Screen>register(HANDLER, (handler, playerInventory, title) -> new Screen(handler, playerInventory));
    }

    public static void setup() {
        HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier(
                "limbodrive",
                "pixel_blaster_3000"
            ),
            new ScreenHandlerType<>((syncId, playerInventory) -> new RenderTarget(syncId, playerInventory, null),
                FeatureFlags.VANILLA_FEATURES)
        );
    }

    public static class RenderTarget extends SyncedGuiDescription {
        public RenderTarget(int syncId, PlayerInventory playerInventory, Identifier which) {
            super(HANDLER, syncId, playerInventory);

            this.setRootPanel(new Display(which));
            this.rootPanel.validate(this);
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
        void render(DrawContext context, RenderStage stage, Display display, Identifier which, int posX, int posY, int mouseX, int mouseY);
    }

    public static void registerRenderHandler(RenderStage stage, RenderCallback handler) {
        ArrayList<RenderCallback> handlers = RENDER.getOrDefault(stage, new ArrayList<>());
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
