package limbo.drive.module.world.util;

import limbo.drive.util.registry.LimboBlockRegistry;
import limbo.drive.util.data.RiftDataManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RiftHandler {
    public static void start(World world, BlockPos position) {
        int lengthDown = 8;
        
        Direction.Axis rotation = null;

        if (RiftDataManager.CURRENT_FRAME_PIECE == 0) {
            if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1)).getBlock() == Blocks.REINFORCED_DEEPSLATE) {
                if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.DOWN)).getBlock() == Blocks.COBBLED_DEEPSLATE) {
                    if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.SOUTH)).getBlock() == Blocks.DEEPSLATE_TILES) {
                        rotation = Direction.Axis.Z;
                    } else if (world.getBlockState(position.offset(Direction.DOWN, lengthDown - 1).offset(Direction.EAST)).getBlock() == Blocks.DEEPSLATE_TILES) {
                        rotation = Direction.Axis.X;
                    }
                }
            }
        }
        
        if (rotation == null) {
            if (RiftDataManager.CURRENT_CORNER_POSITION != null && RiftDataManager.CURRENT_FRAME_PIECE == 55) {
                RiftHandler.add(position);
                RiftHandler.finish(RiftDataManager.CURRENT_AXIS);
            } else if (RiftDataManager.CURRENT_CORNER_POSITION != null) {
                RiftHandler.add(position);
            } else if (!RiftDataManager.FAILED.contains(position)) {
                RiftDataManager.FAILED.add(position);
            }
        } else {
            RiftDataManager.CURRENT_AXIS = rotation;
            if (RiftDataManager.CURRENT_CORNER_POSITION == null) {
                RiftDataManager.CURRENT_CORNER_POSITION = position;
                RiftHandler.create(position);
            }
        }
    }
    
    private static void create(BlockPos corner) {
        RiftDataManager.CURRENT_FRAME_PIECE = 0;

        if (RiftDataManager.CURRENT_CORNER_POSITION != corner) {
            RiftDataManager.CURRENT_RIFT = UUID.randomUUID();
            RiftDataManager.CURRENT_CORNER_POSITION = corner;

            Pair<ArrayList<BlockPos>, Integer> data = RiftDataManager.RIFT_LOCATIONS.getOrDefault(RiftDataManager.CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));

            ArrayList<BlockPos> positions = data.getLeft();
            positions.add(corner);

            positions.addAll(RiftDataManager.FAILED);
            RiftDataManager.CURRENT_FRAME_PIECE += RiftDataManager.FAILED.size();
            RiftDataManager.FAILED.clear();


            data.setLeft(positions);
            RiftDataManager.RIFT_LOCATIONS.put(RiftDataManager.CURRENT_RIFT, data);
            RiftDataManager.CURRENT_FRAME_PIECE++;
        }
    }


    private static void add(BlockPos position) {
        if (RiftDataManager.CURRENT_RIFT == null) {
            RiftDataManager.CURRENT_RIFT = UUID.randomUUID();
        }

        Pair<ArrayList<BlockPos>, Integer> data = RiftDataManager.RIFT_LOCATIONS.getOrDefault(RiftDataManager.CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));
        ArrayList<BlockPos> positions = data.getLeft();

        if (!positions.contains(position)) {
            positions.add(position);
        }

        data.setLeft(positions);

        RiftDataManager.CURRENT_FRAME_PIECE++;
        System.out.println("Current piece: " + RiftDataManager.CURRENT_FRAME_PIECE);
        RiftDataManager.RIFT_LOCATIONS.put(RiftDataManager.CURRENT_RIFT, data);
    }

    private static void finish(Direction.Axis axis) {
        Pair<ArrayList<BlockPos>, Integer> data = RiftDataManager.RIFT_LOCATIONS.getOrDefault(RiftDataManager.CURRENT_RIFT, new Pair<>(new ArrayList<>(), ThreadLocalRandom.current().nextInt(1, 10)));

        ArrayList<BlockPos> positions = data.getLeft();

        if (positions.isEmpty()) {
            positions.add(BlockPos.ORIGIN);
        }

        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 20; row++) {
                positions.add(RiftDataManager.CURRENT_CORNER_POSITION.offset(Direction.DOWN).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH).offset(Direction.DOWN, column).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH, row));

                //noinspection DataFlowIssue
                MinecraftClient.getInstance().getServer().getOverworld().setBlockState(RiftDataManager.CURRENT_CORNER_POSITION.offset(Direction.DOWN).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH).offset(Direction.DOWN, column).offset(axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH, row), Blocks.LIME_WOOL.getDefaultState());
            }
        }

        data.setLeft(positions);

        RiftDataManager.CURRENT_FRAME_PIECE = 0;
        RiftDataManager.CURRENT_CORNER_POSITION = null;
        RiftDataManager.RIFT_LOCATIONS.put(RiftDataManager.CURRENT_RIFT, data);
        RiftDataManager.RIFT_COUNT++;
    }

    public static void open(ServerWorld world) {
        boolean foundUnopenedRift = false;

        ArrayList<Pair<UUID, Pair<ArrayList<BlockPos>, Integer>>> possible = new ArrayList<>();
        RiftDataManager.RIFT_LOCATIONS.forEach((identifier, rift) -> possible.add(new Pair<>(identifier, rift)));
        Collections.shuffle(possible);
        for (Pair<UUID, Pair<ArrayList<BlockPos>, Integer>> rift : possible) {
            for (BlockPos block : rift.getRight().getLeft()) {
                if (world.isAir(block)) {
                    foundUnopenedRift = true;
                    world.setBlockState(block, LimboBlockRegistry.RIFT.getDefaultState());
                }
            }
        }

        if (!foundUnopenedRift) {
            world.getServer().getPlayerManager().broadcast(Text.of("The world is at rest, for now...").copy().formatted(Formatting.GRAY), false);
        } else {
            world.getServer().getPlayerManager().broadcast(Text.of("The rift between worlds expands...").copy().formatted(Formatting.RED), false);
        }
    }
}
