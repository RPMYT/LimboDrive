package limbo.drive.util.render.core;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import limbo.drive.util.render.gui.GuiBase;
import net.minecraft.client.gui.DrawContext;

public final class Display extends WPanel {
    private final GuiBase gui;

    public Display(GuiBase gui) {
        this.gui = gui;
    }

    private boolean completedSetup = false;

    @Override
    public void validate(GuiDescription c) {
        completedSetup = false;
        super.validate(c);
        this.gui.render(null, PB3K.RenderStage.SETUP, this, 0, 0, 0, 0);
        completedSetup = true;
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        this.gui.render(context, PB3K.RenderStage.BACKGROUND, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.TEXTURES, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.CONTROLS, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.STRINGS, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.EFFECTS, this, x, y, mouseX, mouseY);
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        this.gui.input(PB3K.InputType.MOUSE, new PB3K.InputData(
            PB3K.InputType.MOUSE,
            this.gui.properties().identifier(),
            x,
            y,
            button,
            0,
            null,
            null,
            null
        ));

        return InputResult.PROCESSED;
    }

    @Override
    public boolean canResize() {
        return !this.completedSetup;
    }
}
