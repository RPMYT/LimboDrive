package limbo.drive.api.graphics.core.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.api.graphics.core.Display;
import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.api.graphics.core.component.MouseControl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GuiBase {
    private DisplayProperties properties;
    
    @SafeVarargs
    public GuiBase(
        @NotNull DisplayProperties properties,
        @Nullable ArrayList<MouseControl> mouseControls,
        @Nullable ArrayList<PB3K.InputCallback> mouse,
        @Nullable ArrayList<PB3K.InputCallback> keyboard,
        @Nullable ArrayList<PB3K.RenderCallback>... callbacks
    ) {
        this.properties = properties;
        this.MOUSE_CONTROLS = mouseControls == null ? new ArrayList<>() : mouseControls;

        //noinspection unchecked
        ArrayList<PB3K.RenderCallback>[] array = new ArrayList[6];

        if (callbacks.length < 6) {
            int available = callbacks.length - 1;
            for (int index = 0; index <= available; index++) {
                array[index] = callbacks[index] == null ? new ArrayList<>() : callbacks[index];
                System.out.println(array[index]);
            }
            for (int index = available + 1; index < array.length; index++) {
                array[index] = new ArrayList<>();
            }
        }

        System.out.println("0: " + array[0]);
        System.out.println("1: " + array[1]);
        RENDERERS.put(PB3K.RenderStage.SETUP, array[0]);
        RENDERERS.put(PB3K.RenderStage.BACKGROUND, array[1]);
        RENDERERS.put(PB3K.RenderStage.TEXTURES, array[2]);
        RENDERERS.put(PB3K.RenderStage.CONTROLS, array[3]);
        RENDERERS.put(PB3K.RenderStage.STRINGS, array[4]);
        RENDERERS.put(PB3K.RenderStage.EFFECTS, array[5]);

        INPUT_HANDLERS.put(PB3K.InputType.MOUSE, mouse == null ? new ArrayList<>() : mouse);
        INPUT_HANDLERS.put(PB3K.InputType.KEYBOARD, keyboard == null ? new ArrayList<>() : keyboard);
    }

    protected final ArrayList<MouseControl> MOUSE_CONTROLS;

    protected final HashMap<PB3K.RenderStage, ArrayList<PB3K.RenderCallback>> RENDERERS = new HashMap<>();
    protected final HashMap<PB3K.InputType, ArrayList<PB3K.InputCallback>> INPUT_HANDLERS = new HashMap<>();

    public void render(DrawContext context, PB3K.RenderStage stage, Display display, int posX, int posY, int mouseX, int mouseY) {
        __RenderSetup(context, stage, display, posX, posY);
        this.RENDERERS.get(stage).forEach(renderer -> renderer.render(
                context,
                stage,
                display,
                posX,
                posY,
                mouseX,
                mouseY
        ));
    }

    @SuppressWarnings("DataFlowIssue")
    public void input(PB3K.InputType type, PB3K.InputData data) {
        this.INPUT_HANDLERS.get(type).forEach(handler -> handler.process(data));

        if (type == PB3K.InputType.MOUSE) {
            this.MOUSE_CONTROLS.forEach(control -> {
                if (data.mouseX() >= control.x() && data.mouseX() <= control.x() + control.width()) {
                    if (data.mouseY() >= control.y() && data.mouseY() <= control.y() + control.height()) {
                        control.handler().onClicked(data);
                    }
                }
            });
        }
    }

    public final DisplayProperties properties() {
        return this.properties;
    }
    
    protected void __ErrorSetup(Display display) {
        this.properties = new DisplayProperties(
            BackgroundType.TEXTURED,
            BorderType.NONE,
            new Identifier(
                "limbodrive",
                "error"
            ),
            display.getWidth(),
            display.getHeight(),
            null,
            null,
            null,
            new Identifier(
                "limbodrive",
                "textures/gui/error.png"
            )
        );
    }

    protected void __RenderSetup(DrawContext context, PB3K.RenderStage stage, Display display, int posX, int posY) {
        switch (stage) {
            case SETUP -> display.setSize(this.properties.width(), this.properties.height());
            case BACKGROUND -> {
                switch (this.properties.backgroundType()) {
                    case COLOURED -> {
                        if (this.properties.backgroundColour() == null) {
                            PB3K.LOGGER.warn("Attempted to draw a coloured background GUI, but the colour was null!");
                            this.__ErrorSetup(display);
                            return;
                        }

                        ScreenDrawing.coloredRect(
                            context,
                            posX,
                            posY,
                            this.properties.width(),
                            this.properties.height(),
                            this.properties.backgroundColour()
                        );
                    }

                    case TEXTURED -> {
                        if (this.properties.backgroundTexture() == null) {
                            PB3K.LOGGER.warn("Attempted to draw a coloured background GUI, but the texture was null!");
                            this.__ErrorSetup(display);
                            return;
                        }

                        ScreenDrawing.texturedRect(
                            context,
                            posX,
                            posY,
                            this.properties.width(),
                            this.properties.height(),
                            this.properties.backgroundTexture(),
                            this.properties.backgroundColour() == null ? 0xFF_FFFFFF : this.properties.backgroundColour()
                        );
                    }
                }
            }
            case CONTROLS -> this.MOUSE_CONTROLS.forEach(control -> {
                control.draw(context, posX, posY);
                control.write(context, posX, posY);
            });
        }
    }

    public void onClosed(PlayerEntity player) {}
}
