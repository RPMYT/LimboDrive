package limbo.drive.util.render;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class Display extends WPanel {
    private final Identifier which;

    public Display(Identifier which) {
        this.which = which;
    }

    private boolean completedSetup = false;

    @Override
    public void validate(GuiDescription c) {
        completedSetup = false;
        super.validate(c);
        System.out.println(this.which);
        PB3K.RENDER.get(PB3K.RenderStage.SETUP).forEach(renderer -> renderer.render(
            null,
            PB3K.RenderStage.SETUP,
            this,
            this.which,
            0,
            0,
            0,
            0
        ));
        completedSetup = true;
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        PB3K.RENDER.forEach((stage, renderers) -> {
            if (stage != PB3K.RenderStage.SETUP) {
                renderers.forEach(renderer -> renderer.render(context, stage, this, this.which, x, y, mouseX, mouseY));
            }
        });
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        PB3K.INPUT.get(PB3K.InputType.MOUSE).forEach(handler -> {
            handler.process(new PB3K.InputData(
                PB3K.InputType.MOUSE,
                this.which,
                x,
                y,
                button,
                0,
                null,
                null,
                null
            ));
        });

        return InputResult.PROCESSED;
    }

    @Override
    public boolean canResize() {
        return !this.completedSetup;
    }
}
