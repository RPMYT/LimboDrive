package limbo.drive.module.navigation.renderer.data;

import limbo.drive.module.navigation.renderer.map.tile.Tileset;

public class Tilesets {
    public static final Tileset MINECRAFTIAN = Tileset.Builder.begin("minecraftian")
        .addBackground('g', "minecraftian/grass")
        .addBackground('d', "minecraftian/dirt")
        .addBackground('s', "minecraftian/stone")
        .addBackground('w', "minecraftian/wood")
        .addBackground('l', "minecraftian/log")
        .addBackground('O', "minecraftian/door_top")
        .addBackground('o', "minecraftian/door_bottom")
        .build();

    public static final Tileset DIRTSHACKLED = Tileset.Builder.begin("dirtshackled")
        .addSizedBackground('!', 32, 32, "dirtshackled/stop")
        .build();
}
