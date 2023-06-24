package limbo.drive.module.navigation.renderer.map.layer;

import limbo.drive.module.navigation.renderer.RenderBuffer;

public interface MapLayer {
    LayerType type();
    RenderBuffer draw(RenderBuffer buffer);
}
