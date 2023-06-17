package limbo.drive.starfield.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.starfield.StarmapGUI;
import limbo.drive.util.render.PB3K;
import limbo.drive.util.render.component.Buttons;
import net.minecraft.util.Identifier;

public final class StarfieldGenerationGUI {
    public static void init() {}

    static {
        PB3K.registerRenderHandler(PB3K.RenderStage.SETUP, (context, stage, display, which, posX, posY, mouseX, mouseY) -> {
            if (which.toString().equals("limbodrive:starmap")) {
                display.setSize(410, 225);
            }
        });

        PB3K.registerRenderHandler(PB3K.RenderStage.BACKGROUND, (context, stage, display, which, posX, posY, mouseX, mouseY) -> {
            if (which.toString().equals("limbodrive:starmap")) {
                ScreenDrawing.coloredRect(context, posX, posY, display.getWidth(), display.getHeight(), 0xFF_FFFF00);
            }
        });

        Buttons.register(new Buttons.Button(
            mouseButton -> {
                System.out.println(mouseButton);
            },
            new Identifier(
                "limbodrive",
                "starmap"
            ),
            96,
            32,
            new Identifier(
                "limbodrive",
                "textures/gui/button.png"
            ),
            0xFF_FFFFFF,
            8,
            24
        ));
    }
}
