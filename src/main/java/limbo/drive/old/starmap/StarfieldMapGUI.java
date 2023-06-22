package limbo.drive.old.starmap;

import limbo.drive.api.graphics.core.Display;
import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.api.graphics.core.gui.BackgroundType;
import limbo.drive.api.graphics.core.gui.BorderType;
import limbo.drive.api.graphics.core.gui.DisplayProperties;
import limbo.drive.api.graphics.core.gui.GuiBase;
import net.minecraft.client.gui.DrawContext;
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
            null,
            null,
            null
        );
    }

    @Override
    public void render(DrawContext context, PB3K.RenderStage stage, Display display, int posX, int posY, int mouseX, int mouseY) {
        super.render(context, stage, display, posX, posY, mouseX, mouseY);
    }
}
