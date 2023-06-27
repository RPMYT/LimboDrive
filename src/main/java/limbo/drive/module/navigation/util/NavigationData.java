package limbo.drive.module.navigation.util;

import limbo.drive.module.navigation.rendering.RenderingContext;
import limbo.drive.module.navigation.map.sprite.SpriteBuffer;

public class NavigationData {
    public static final int MAP_OFFSET_X = 4;
    public static final int MAP_OFFSET_Y = 2;
    public static final int MAP_SIZE_X = 748;
    public static final int MAP_SIZE_Y = 394;
    public static final int MAP_COLOUR = 0xFF_2A2A2A;
    public static final int EDITOR_TILESET_SIZE_X = 130;
    public static final int EDITOR_TILESET_SIZE_Y = 394;
    public static boolean DEBUG_ENABLED = false;
    public static boolean DEBUG_REQUESTED = false;
    public static RenderingContext CURRENT_CONTEXT;
    public static String EDITOR_TILESET_SELECTED = "debug";
    public static int EDITOR_TILE_SELECTED = 0;
    public static int EDITOR_TILE_PAGE = 0;
    public static SpriteBuffer SPRITES = null;
}
