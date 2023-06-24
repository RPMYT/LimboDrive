package limbo.drive.module.navigation.renderer.data;

import limbo.drive.module.navigation.renderer.map.tile.Tileset;

public class Tilesets {
    public static final Tileset MINECRAFTIAN = Tileset.Builder.begin("minecraftian")
        .add('g', "minecraftian/grass")
        .add('d', "minecraftian/dirt")
        .add('s', "minecraftian/stone")
        .add('w', "minecraftian/wood")
        .add('l', "minecraftian/log")
        .add('O', "minecraftian/door_top")
        .add('o', "minecraftian/door_bottom")
        .build();
}
