package limbo.drive.module.navigation.renderer.map.tile;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class Tileset {
    public final String name;
    public final ImmutableMap<Character, TileData> tiles;

    private Tileset(String name, ImmutableMap<Character, TileData> tiles) {
        this.name = name;
        this.tiles = tiles;
    }

    public static class Builder {
        private final String name;
        private final ArrayList<Pair<Character, TileData>> TILES = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public static Builder begin(String name) {
            return new Builder(name);
        }

        public Builder addBackground(char mapping, String texture) {
            TILES.add(new Pair<>(mapping, new TileData(16, 16, false, texture)));
            return this;
        }

        public Builder addSizedBackground(char mapping, int width, int height, String texture) {
            TILES.add(new Pair<>(mapping, new TileData(width, height, false, texture)));
            return this;
        }

        public Builder addForeground(char mapping, String texture) {
            TILES.add(new Pair<>(mapping, new TileData(16, 16, true, texture)));
            return this;
        }

        public Builder addSizedForeground(char mapping, int width, int height, String texture) {
            TILES.add(new Pair<>(mapping, new TileData(width, height, true, texture)));
            return this;
        }

        public Tileset build() {
            ImmutableMap.Builder<Character, TileData> builder = ImmutableMap.builder();

            for (Pair<Character, TileData> tile : TILES) {
                builder.put(tile.getLeft(), tile.getRight());
            }

            return new Tileset(this.name, builder.build());
        }
    }
}
