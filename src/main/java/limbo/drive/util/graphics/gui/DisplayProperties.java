package limbo.drive.util.graphics.gui;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record DisplayProperties(
    BackgroundType backgroundType,
    BorderType borderType,
    Identifier identifier,
    int width,
    int height,
    @Nullable Integer borderColour,
    @Nullable Integer backgroundColour,
    @Nullable Identifier borderTexture,
    @Nullable Identifier backgroundTexture
 ) {}