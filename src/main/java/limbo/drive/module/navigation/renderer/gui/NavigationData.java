package limbo.drive.module.navigation.renderer.gui;

import limbo.drive.module.navigation.renderer.map.sprite.SpriteBuffer;

public class NavigationData {
    static final int MAP_OFFSET_X = 4;
    static final int MAP_OFFSET_Y = 2;
    static final int MAP_SIZE_X = 748;
    static final int MAP_SIZE_Y = 394;
    static final int MAP_COLOUR = 0xFF_2A2A2A;
    static final int EDITOR_TILESET_SIZE_X = 130;
    static final int EDITOR_TILESET_SIZE_Y = 394;
    static boolean DEBUG_ENABLED = false;
    static boolean DEBUG_REQUESTED = false;
    static RenderingContext CURRENT_CONTEXT;
    static String EDITOR_TILESET_SELECTED = "debug";
    static int EDITOR_TILE_SELECTED = 0;
    static int EDITOR_TILE_PAGE = 0;
    static SpriteBuffer SPRITES = null;
}
