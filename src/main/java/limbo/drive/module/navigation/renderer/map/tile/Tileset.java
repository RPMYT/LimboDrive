package limbo.drive.module.navigation.renderer.map.tile;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class Tileset {
    public final String name;
    public final ImmutableMap<Character, String> tiles;

    private Tileset(String name, ImmutableMap<Character, String> tiles) {
        this.name = name;
        this.tiles = tiles;
    }

    public static class Builder {
        private final String name;
        private final ArrayList<Pair<Character, String>> TILES = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public static Builder begin(String name) {
            return new Builder(name);
        }

        public Builder add(char mapping, String texture) {
            TILES.add(new Pair<>(mapping, texture));
            return this;
        }

        public Tileset build() {
            ImmutableMap.Builder<Character, String> builder = ImmutableMap.builder();

            for (Pair<Character, String> tile : TILES) {
                builder.put(tile.getLeft(), tile.getRight());
            }

            return new Tileset(this.name, builder.build());
        }
    }
}
