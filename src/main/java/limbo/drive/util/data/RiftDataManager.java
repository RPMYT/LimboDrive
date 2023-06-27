package limbo.drive.util.data;

import limbo.drive.LimboDrive;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.*;
import net.minecraft.util.Pair;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RiftDataManager {
    public static final HashMap<UUID, Pair<ArrayList<BlockPos>, Integer>> RIFT_LOCATIONS = new HashMap<>();
    public static final ArrayList<BlockPos> FAILED = new ArrayList<>();
    public static int RIFT_COUNT = 0;
    public static UUID CURRENT_RIFT = null;
    public static BlockPos CURRENT_CORNER_POSITION = null;
    public static int CURRENT_FRAME_PIECE = 0;
    public static Direction.Axis CURRENT_AXIS = null;

    public static void load(NbtCompound data) {
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
                LimboDrive.LOGGER.warn("Found a null rift ID?!");
            } else {
                LimboDrive.LOGGER.info("Saving rift ID " + identifier);
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
    
    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server ->{
            Path path = server.getSavePath(WorldSavePath.GENERATED);
            File saved = server.getFile(path + "worldbleed.dat");

            boolean proceed = true;
            if (!saved.exists()) {
                proceed = false;
                try {
                    saved.createNewFile();
                } catch (IOException exception) {
                    LimboDrive.LOGGER.error("Failed to create LimboDrive Worldbleed data file.");
                    LimboDrive.LOGGER.error("You probably have a permissions error somewhere; might wanna fix that.");
                    LimboDrive.LOGGER.error("Here's the stacktrace:");
                    exception.printStackTrace();
                }
            }

            if (proceed) {
                try {
                    NbtCompound read = NbtIo.readCompressed(saved);
                    RiftDataManager.load(read);
                } catch (IOException exception) {
                    LimboDrive.LOGGER.error("Failed to read LimboDrive Worldbleed data.");
                    LimboDrive.LOGGER.error("This is pretty bad; the mod uses this quite a lot..");
                    LimboDrive.LOGGER.error("Might wanna fix this; though hopefully it's minor.");
                    LimboDrive.LOGGER.error("Here's the stacktrace:");
                    exception.printStackTrace();
                }
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            File saved = server.getFile("worldbleed.dat");

            boolean proceed = true;
            if (!saved.exists()) {
                try {
                    saved.createNewFile();
                } catch (IOException exception) {
                    proceed = false;
                    LimboDrive.LOGGER.fatal("Failed to create LimboDrive Worldbleed data file.");
                    LimboDrive.LOGGER.fatal("Guess the world is safe, for now.");
                    LimboDrive.LOGGER.fatal("You probably have a permissions error somewhere; here's the stacktrace:");
                    exception.printStackTrace();
                }
            }

            if (proceed) {
                try {
                    NbtCompound data = RiftDataManager.save();
                    NbtIo.writeCompressed(data, saved);
                } catch (IOException exception) {
                    LimboDrive.LOGGER.fatal("Failed to write LimboDrive Worldbleed data.");
                    LimboDrive.LOGGER.fatal("Guess the world is safe, for now.");
                    LimboDrive.LOGGER.fatal("PLEASE send the following stacktrace to the developer!! ('lilirine' on Discord; or make an issue at https://github.com/RPMYT/LimboDrive");
                    exception.printStackTrace();
                }
            }
        });
    }
}
