package limbo.drive.module.limbo;

import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtType;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Worldbleed {
    public static final BlockPattern RIFT_PATTERN = BlockPatternBuilder.start()
        .aisle("####################")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("#XXXXXXXXXXXXXXXXXX#")
        .aisle("####################")

        .where('X', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.AIR)))
        .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.REINFORCED_DEEPSLATE)))
        .build();


    public static int RIFT_COUNT = 0;
    public static final HashMap<UUID, Pair<ArrayList<BlockPos>, Integer>> RIFT_LOCATIONS = new HashMap<>();
    private static UUID CURRENT_RIFT = UUID.randomUUID();

    public static void add(BlockPos position) {
        ArrayList<BlockPos> positions = RIFT_LOCATIONS.getOrDefault(CURRENT_RIFT, new Pair<>(new ArrayList<>(), 0)).getLeft();
        positions.add(position);
        RIFT_LOCATIONS.put(CURRENT_RIFT, new Pair<>(positions, ThreadLocalRandom.current().nextInt(1, 10)));
    }

    public static void finish() {
        CURRENT_RIFT = UUID.randomUUID();
    }

    public static void initialize(NbtCompound data) {
        NbtList rifts = data.getList("Rifts", NbtList.COMPOUND_TYPE);

        RIFT_COUNT = rifts.size();
        if (!rifts.isEmpty()) {
            for (NbtElement element : rifts) {
                if (element instanceof NbtCompound rift) {
                    UUID identifier = rift.getUuid("Identifier");
                    NbtList blocks = rift.getList("Blocks", NbtList.LONG_TYPE);
                    int size = rift.getInt("Size");

                    ArrayList<BlockPos> positions = new ArrayList<>();
                    for (NbtElement block : blocks) {
                        if (block instanceof NbtLong) {
                            positions.add(BlockPos.fromLong(((NbtLong) block).longValue()));
                        }
                    }

                    RIFT_LOCATIONS.put(identifier, new Pair<>(positions, size));
                }
            }
        }
    }

    public static NbtCompound save() {
        NbtCompound data = new NbtCompound();

        NbtList list = new NbtList();
        RIFT_LOCATIONS.forEach((identifier, rift) -> {
            NbtCompound compound = new NbtCompound();

            NbtList blocks = new NbtList();
            for (BlockPos position : rift.getLeft()) {
                blocks.add(NbtLong.of(position.asLong()));
            }

            compound.put("Blocks", blocks);
            compound.putUuid("Identifier", identifier);
            compound.putInt("Size", rift.getRight());
        });
        data.put("Rifts", list);

        return data;
    }
}
