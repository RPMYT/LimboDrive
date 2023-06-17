package limbo.drive.util.render.component;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.util.render.PB3K;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class Buttons {
    private static final ArrayList<Button> BUTTONS = new ArrayList<>();

    public static void register(Button button) {
        BUTTONS.add(button);
    }

    static {
        PB3K.registerRenderHandler(PB3K.RenderStage.CONTROLS, (context, stage, display, which, posX, posY, mouseX, mouseY) -> {
            BUTTONS.forEach(button -> {
                ScreenDrawing.texturedRect(context, posX + button.x, posY + button.y, button.width, button.height, button.texture, button.color);
            });
        });

        PB3K.registerInputHandler(PB3K.InputType.MOUSE, data -> {
            BUTTONS.forEach(button -> {
                if (data.where() == button.screen) {
                    if (data.mouseX() >= button.x && data.mouseX() <= button.x + button.width) {
                        if (data.mouseY() >= button.y && data.mouseY() <= button.y + button.height) {
                            button.handler.onClicked(data.mouseButton());
                        }
                    }
                }
            });
        });
    }

    public record Button(ClickHandler handler, Identifier screen, int width, int height, Identifier texture, int color, int x, int y) {}

    @FunctionalInterface
    public interface ClickHandler {
        void onClicked(int mouseButton);
    }
}
