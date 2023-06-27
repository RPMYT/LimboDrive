package limbo.drive.module.navigation.renderer;

public abstract class RenderBuffer {
     protected abstract void flush(RenderingContext context, RenderBuffer... others);
}