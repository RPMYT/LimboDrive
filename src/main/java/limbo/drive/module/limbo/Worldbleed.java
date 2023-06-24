package limbo.drive.module.limbo;

import limbo.drive.LimboDrive;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Worldbleed {
    public static int RIFT_COUNT = 0;
    public static final HashMap<UUID, Pair<ArrayList<BlockPos>, Integer>> RIFT_LOCATIONS = new HashMap<>();
    private static UUID CURRENT_RIFT = null;

    private static BlockPos CURRENT_CORNER_POSITION = null;
    private static int CURRENT_FRAME_PIECE = 0;
    private static Direction.Axis CURRENT_AXIS = null;

    private static final ArrayList<BlockPos> FAILED = new ArrayList<>();
    
    public static void start(World world, BlockPos position) {
        // TODO refactor this into a generic frame checker
        int lengthDown = 8;
        
        Direction.Axis rotation = null;

        if (CURRENT_FRAME_PIECE == 0) {
            if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1)).getBlock() == Blocks.REINFORCED_DEEPSLATE) {
//                MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position + ", test 1/3: PASS").copy().formatted(Formatting.GREEN));
                if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.DOWN)).getBlock() == Blocks.COBBLED_DEEPSLATE) {
//                    MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position + " , test 2/3: PASS").copy().formatted(Formatting.GREEN));
                    if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.SOUTH)).getBlock() == Blocks.DEEPSLATE_TILES) {
//                        MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position + ", test 2/3: PASS").copy().formatted(Formatting.GREEN));
                        rotation = Direction.Axis.Z;
                    } else if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.EAST)).getBlock() == Blocks.DEEPSLATE_TILES) {
//                        MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position + ", test 3/3: PASS").copy().formatted(Formatting.GREEN));
                        rotation = Direction.Axis.X;
                    }  //                        MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position +
                    //                            ", test 3/3: FAIL: expected Deepslate Tiles at " +
                    //                            position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.SOUTH) +
                    //                            " or " +
                    //                            position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.EAST) +
                    //                            ".").copy().formatted(Formatting.RED));

                }  //                    MinecraftClient.getInstance().player.sendMessage(Text.of("Position " + position +
                //                        ", test 2/3: FAIL: expected Cobbled Deepslate at " +
                //                        position.offset(Direction.DOWN, lengthDown) +
                //                        ".").copy().formatted(Formatting.RED));

            }
        }

//        MinecraftClient.getInstance().player.sendMessage(Text.of(""));
        
        if (rotation == null) {
            if (CURRENT_CORNER_POSITION != null && CURRENT_FRAME_PIECE == 55) {
                Worldbleed.add(position);
                Worldbleed.finish(CURRENT_AXIS);
            } else if (CURRENT_CORNER_POSITION != null) {
                Worldbleed.add(position);
            } else if (!FAILED.contains(position)) {
                FAILED.add(position);
            }
        } else {
            CURRENT_AXIS = rotation;
            if (CURRENT_CORNER_POSITION == null) {
                CURRENT_CORNER_POSITION = position;
                Worldbleed.create(position);
            }
        }
    }
    
    public static void create(BlockPos corner) {
        CURRENT_FRAME_PIECE = 0;

        if (CURRENT_CORNER_POSITION != corner) {
            CURRENT_RIFT = UUID.randomUUID();
            System.out.println("New random UUID ('create'): " + CURRENT_RIFT);
            CURRENT_CORNER_POSITION = corner;

            Pair<ArrayList<BlockPos>, Integer> data = RIFT_LOCATIONS.getOrDefault(CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));

            ArrayList<BlockPos> positions = data.getLeft();
            positions.add(corner);

            positions.addAll(FAILED);
            CURRENT_FRAME_PIECE += FAILED.size();
            FAILED.clear();


            data.setLeft(positions);
            RIFT_LOCATIONS.put(CURRENT_RIFT, data);
            CURRENT_FRAME_PIECE++;
        }
    }


    public static void add(BlockPos position) {
        if (CURRENT_RIFT == null) {
            CURRENT_RIFT = UUID.randomUUID();
            System.out.println("New random UUID ('add'): " + CURRENT_RIFT);
        }

        System.out.println("Adding to rift ID " + CURRENT_RIFT);
        Pair<ArrayList<BlockPos>, Integer> data = RIFT_LOCATIONS.getOrDefault(CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));
        ArrayList<BlockPos> positions = data.getLeft();

        if (!positions.contains(position)) {
            positions.add(position);
        } else {
            System.out.println("Tried to add a block more than once?? Pos: " + position);
        }
        data.setLeft(positions);

        CURRENT_FRAME_PIECE++;
        System.out.println("Current piece: " + CURRENT_FRAME_PIECE);
        RIFT_LOCATIONS.put(CURRENT_RIFT, data);
    }

    public static void finish(Direction.Axis axis) {
        System.out.println("Finalizing rift ID " + CURRENT_RIFT);
        Pair<ArrayList<BlockPos>, Integer> data = RIFT_LOCATIONS.getOrDefault(CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));

        ArrayList<BlockPos> positions = data.getLeft();

        if (positions.isEmpty()) {
            positions.add(BlockPos.ORIGIN);
        }

        System.out.println("Before air: " + positions.size());
        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 20; row++) {
                positions.add(CURRENT_CORNER_POSITION.offset(Direction.DOWN).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH).offset(Direction.DOWN, column).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH, row));
                MinecraftClient.getInstance().getServer().getOverworld().setBlockState(CURRENT_CORNER_POSITION.offset(Direction.DOWN).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH).offset(Direction.DOWN, column).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH, row), Blocks.LIME_WOOL.getDefaultState());
            }
        }

        System.out.println("After air: " + positions.size());
        data.setLeft(positions);

        CURRENT_FRAME_PIECE = 0;
        CURRENT_CORNER_POSITION = null;
        RIFT_LOCATIONS.put(CURRENT_RIFT, data);
        RIFT_COUNT++;
    }

    public static void open(ServerWorld world) {
        boolean foundUnopenedRift = false;

        ArrayList<Pair<UUID, Pair<ArrayList<BlockPos>, Integer>>> possible = new ArrayList<>();
        RIFT_LOCATIONS.forEach((identifier, rift) -> possible.add(new Pair<>(identifier, rift)));
        Collections.shuffle(possible);
        for (Pair<UUID, Pair<ArrayList<BlockPos>, Integer>> rift : possible) {
            System.out.println(rift.getLeft());
            for (BlockPos block : rift.getRight().getLeft()) {
                System.out.println("Block at " + block + " is " + world.getBlockState(block).getBlock());
                if (world.isAir(block)) {
                    foundUnopenedRift = true;
                    world.setBlockState(block, LimboDrive.Initializer.RIFT.getDefaultState());
                }
            }
        }

        if (!foundUnopenedRift) {
            world.getServer().getPlayerManager().broadcast(Text.of("The world is at rest, for now...").copy().formatted(Formatting.GRAY), false);
        } else {
            world.getServer().getPlayerManager().broadcast(Text.of("The rift between worlds expands...").copy().formatted(Formatting.RED), false);
        }
    }

    public static void initialize(NbtCompound data) {
        CURRENT_FRAME_PIECE = 0;
        CURRENT_RIFT = null;
        CURRENT_CORNER_POSITION = BlockPos.ORIGIN;

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
        CURRENT_FRAME_PIECE = 0;
        CURRENT_RIFT = null;
        CURRENT_CORNER_POSITION = BlockPos.ORIGIN;

        NbtCompound data = new NbtCompound();

        NbtList list = new NbtList();
        RIFT_LOCATIONS.forEach((identifier, rift) -> {
            if (identifier == null) {
                System.out.println("Found a null rift ID?!");
            } else {
                System.out.println("Saving rift ID " + identifier);
            }
            NbtCompound compound = new NbtCompound();

            NbtList blocks = new NbtList();
            for (BlockPos position : rift.getLeft()) {
                blocks.add(NbtLong.of(position.asLong()));
            }

            compound.put("Blocks", blocks);
            compound.putUuid("Identifier", identifier == null ? UUID.randomUUID() : identifier);
            compound.putInt("Size", rift.getRight());

            list.add(compound);
        });

        data.put("Rifts", list);

        return data;
    }
}
