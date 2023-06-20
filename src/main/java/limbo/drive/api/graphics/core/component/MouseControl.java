package limbo.drive.api.graphics.core.component;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.api.graphics.core.PB3K;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record MouseControl (
    ClickHandler handler,
    int width,
    int height,
    Identifier texture,
    int color,
    int x,
    int y,
    @Nullable Text text,
    @Nullable Integer textColor
) {
    @FunctionalInterface
    public interface ClickHandler {
        void onClicked(PB3K.InputData data);
    }

    public void draw(DrawContext context, int posX, int posY) {
        ScreenDrawing.texturedRect(context,
            posX + this.x,
            posY + this.y,
            this.width,
            this.height,
            this.texture,
            this.color
        );
    }

    public void write(DrawContext context, int posX, int posY) {
        if (this.text != null) {
            ScreenDrawing.drawString(
                context,
                this.text.asOrderedText(),
                posX + this.x + 5,
                posY + this.y + (this.height / 4) + 4,
                this.textColor == null ? 0xFF_FFFFFF : this.textColor
            );
        }
    }
}