package limbo.drive.module.starfield;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import limbo.drive.api.graphics.core.gui.BackgroundType;
import limbo.drive.api.graphics.core.gui.BorderType;
import limbo.drive.api.graphics.core.gui.DisplayProperties;
import limbo.drive.api.graphics.core.gui.GuiBase;
import net.minecraft.util.Identifier;

public class StarfieldMapGUI extends GuiBase {
    public StarfieldMapGUI() {
        super(
            new DisplayProperties(
                BackgroundType.TEXTURED,
                BorderType.COLOUR,
                new Identifier(
                    "limbodrive",
                    "starfield_map"
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
            Lists.newArrayList(
                (context, stage, display, posX, posY, mouseX, mouseY) -> {
                    ScreenDrawing.coloredRect(context, posX, posY, 64, 64, 0xFF_00FF00);
                }
            ),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList(),
            Lists.newArrayList()
        );
    }
}
