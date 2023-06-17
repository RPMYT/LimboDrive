package limbo.drive.util.render.component;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.util.render.PB3K;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Buttons {
    private static final ArrayList<Button> BUTTONS = new ArrayList<>();

    public static void register(Button button) {
        BUTTONS.add(button);
    }

    private static void draw(DrawContext context, Button button, Identifier screen, int posX, int posY) {
        if (screen.equals(button.screen)) {
            ScreenDrawing.texturedRect(context,
                posX + button.x,
                posY + button.y,
                button.width,
                button.height,
                button.texture,
                button.color
            );
        }
    }

    private static void write(DrawContext context, Button button, Identifier screen, int posX, int posY) {
        if (screen.equals(button.screen) && button.text != null) {
            ScreenDrawing.drawString(
                context,
                button.text.asOrderedText(),
                posX + button.x + 5,
                posY + button.y + (button.height / 4) + 4,
                button.textColor == null ? 0xFF_FFFFFF : button.textColor
            );
        }
    }

    static {
        PB3K.registerRenderHandler(PB3K.RenderStage.CONTROLS, (context, stage, display, posX, posY, mouseX, mouseY) -> {
            for (Button button : BUTTONS) {
                draw(context, button, display.identifier, posX, posY);
            }
        });

        PB3K.registerRenderHandler(PB3K.RenderStage.STRINGS, (context, stage, display, posX, posY, mouseX, mouseY) -> {
            for (Button button : BUTTONS) {
                write(context, button, display.identifier, posX, posY);
            }
        });

        PB3K.registerInputHandler(PB3K.InputType.MOUSE, data -> BUTTONS.forEach(button -> {
            if (data.where().equals(button.screen)) {
                if (data.mouseX() >= button.x && data.mouseX() <= button.x + button.width) {
                    if (data.mouseY() >= button.y && data.mouseY() <= button.y + button.height) {
                        button.handler.onClicked(data.mouseButton());
                    }
                }
            }
        }));
    }

    public record Button(
        ClickHandler handler,
        Identifier screen,
        int width,
        int height,
        Identifier texture,
        int color,
        int x,
        int y,
        @Nullable Text text,
        @Nullable Integer textColor
    ) {}

    @FunctionalInterface
    public interface ClickHandler {
        void onClicked(int mouseButton);
    }
}
