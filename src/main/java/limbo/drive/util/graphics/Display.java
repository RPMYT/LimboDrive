package limbo.drive.util.graphics;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import limbo.drive.util.graphics.gui.GuiBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;

public final class Display extends WPanel {
    private final GuiBase gui;

    public Display(GuiBase gui) {
        this.gui = gui;
    }

    private boolean completedSetup = false;

    private final HashMap<Pair<Integer, Integer>, Integer> heldKeys = new HashMap<>();

    @Override
    public void validate(GuiDescription c) {
        completedSetup = false;
        super.validate(c);
        this.gui.render(null, PB3K.RenderStage.SETUP, this, 0, 0, 0, 0);
        completedSetup = true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        this.heldKeys.forEach((key, modifiers) -> {
            this.gui.input(PB3K.InputType.KEYBOARD, new PB3K.InputData(
                PB3K.InputType.KEYBOARD,
                this.gui.properties().identifier(),
                0,
                0,
                0,
                -1,
                key.getLeft(),
                key.getRight(),
                modifiers
            ));
        });
        this.heldKeys.clear();

        this.gui.render(context, PB3K.RenderStage.BACKGROUND, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.TEXTURES, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.CONTROLS, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.STRINGS, this, x, y, mouseX, mouseY);
        this.gui.render(context, PB3K.RenderStage.EFFECTS, this, x, y, mouseX, mouseY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public InputResult onClick(int x, int y, int button) {
        this.requestFocus();
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
    public boolean canFocus() {
        return true;
    }

    @Override
    public InputResult onCharTyped(char ch) {
        if (ch == 256) {
            return InputResult.IGNORED;
        }

        this.gui.input(PB3K.InputType.KEYBOARD, new PB3K.InputData(
            PB3K.InputType.KEYBOARD,
            this.gui.properties().identifier(),
            0,
            0,
            0,
            0,
            (int) ch,
            GLFW.glfwGetKeyScancode(ch),
            0
        ));
        return InputResult.PROCESSED;
    }

    @Override
     public InputResult onKeyPressed(int ch, int key, int modifiers) {
        if (ch == 256) {
            return InputResult.IGNORED;
        }

        heldKeys.put(new Pair<>(ch, key), modifiers);
        return InputResult.PROCESSED;
    }

    @Override
    public InputResult onKeyReleased(int ch, int key, int modifiers) {
        if (ch == 256) {
            return InputResult.IGNORED;
        }

        ArrayList<Pair<Integer, Integer>> released = new ArrayList<>();

        heldKeys.forEach((keycodes, mods) -> {
            if (keycodes.getLeft() == ch || keycodes.getRight() == key) {
                released.add(keycodes);
            }
        });

        for (Pair<Integer, Integer> keycode : released) {
            heldKeys.remove(keycode);
        }

        return InputResult.PROCESSED;
    }

    @Override
    public boolean canResize() {
        return !this.completedSetup;
    }
}
