package limbo.drive.starfield.gui;

import com.google.common.collect.Lists;
import limbo.drive.util.render.component.MouseControl;
import limbo.drive.util.render.gui.BackgroundType;
import limbo.drive.util.render.gui.BorderType;
import limbo.drive.util.render.gui.DisplayProperties;
import limbo.drive.util.render.gui.GuiBase;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class StarfieldGenerationGUI extends GuiBase {

    public StarfieldGenerationGUI() {
        super(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.COLOUR,
                new Identifier(
                    "limbodrive",
                    "starfield_generation"
                ),
                410,
                225,
                0xFF_1F2041,
                null,
                null,
                new Identifier(
                    "limbodrive",
                    "textures/gui/starmap.png"
                )
            ),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(
                new MouseControl(
                    data -> System.out.println("You clicked button " + data.mouseButton() + "!"),
                    96,
                    32,
                    new Identifier(
                        "limbodrive",
                        "textures/gui/button.png"
                    ),
                    0xFF_FFFFFF,
                    8,
                    24,
                    Text.literal("Test Button"),
                    null
                )
            )
        );
    }
}
