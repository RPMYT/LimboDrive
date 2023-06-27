package limbo.drive.module.navigation.rendering;

public abstract class RenderBuffer {
     protected abstract void flush(RenderingContext context, RenderBuffer... others);
}