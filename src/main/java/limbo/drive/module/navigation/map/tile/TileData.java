package limbo.drive.module.navigation.map.tile;

public record TileData(int index, String tileset, CollisionType collision, TileProperties... properties) { }
