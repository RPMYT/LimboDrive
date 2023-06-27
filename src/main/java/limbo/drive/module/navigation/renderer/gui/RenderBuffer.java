package limbo.drive.module.navigation.renderer.gui;

public abstract class RenderBuffer {
     protected abstract void flush(RenderingContext context, RenderBuffer... others);
}