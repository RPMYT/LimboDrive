package limbo.drive;

import limbo.drive.starfield.StarfieldGenerator;
import limbo.drive.starfield.StarmapBlock;
import limbo.drive.starfield.StarmapGUI;
import limbo.drive.starfield.data.Star;
import limbo.drive.starfield.gui.StarfieldGenerationGUI;
import limbo.drive.util.render.PB3K;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LimboDrive {
	protected static final NbtCompound DATA = new NbtCompound();

	public static class Client {
		public static class Initializer implements ClientModInitializer {
			@Override
			public void onInitializeClient() {
				PB3K.initialize();
			}
		}
	}

    public static final Logger LOGGER = LogManager.getLogger("Limbo Drive");

	public static class Initializer implements ModInitializer {
		private static boolean youWereWarned = false;

		@Override
		public void onInitialize() {
			this.initData();

			Screens.STARMAP = Registry.register(Registries.SCREEN_HANDLER, new Identifier(
							"limbodrive",
							"starmap"
					),
					new ScreenHandlerType<>((syncId, inventory) -> new StarmapGUI.Description(syncId, inventory, ScreenHandlerContext.EMPTY),
							FeatureFlags.VANILLA_FEATURES));

			Blocks.init();
			BlockEntities.init();

			PB3K.setup();
			StarfieldGenerationGUI.init();
		}

		private void initData() {
			ServerLifecycleEvents.SERVER_STARTING.register(server -> {
				File root = new File(server.getSavePath(WorldSavePath.ROOT) + "/limbodrive.dat");
				if (! root.exists()) {
					try {
						root.createNewFile();
						StarfieldGenerator.initialize();
						NbtCompound data = new NbtCompound();
						NbtList quadrantList = new NbtList();
						StarfieldGenerator.STARS.forEach((quadrant, clusters) -> {
							NbtList clusterList = new NbtList();
							clusters.forEach((cluster, stars) -> {
								NbtList starList = new NbtList();
								stars.forEach((index, star) -> starList.add(-- index, star.serialize()));
								clusterList.add(-- cluster, starList);
							});
							quadrantList.add(-- quadrant, clusterList);
						});

						DATA.put("GalaxyData", quadrantList);
						data.put("GalaxyData", quadrantList);
					} catch (IOException exception) {
						youWereWarned = true;
						LOGGER.fatal("!!! WARNING WARNING WARNING !!!");
						LOGGER.fatal("FAILED TO WRITE LIMBO DRIVE PERSISTENT DATA!");
						LOGGER.fatal("ANY MOD-SPECIFIC DATA WILL BE RESET ON THE NEXT WORLD LOAD!");
						LOGGER.fatal("IT IS *HIGHLY* RECOMMENDED TO CLOSE THE GAME NOW AND FIX THE ISSUE!");
						LOGGER.fatal("SERIOUSLY, ALL DATA WILL BE LOST NEXT WORLD LOAD!!");
						LOGGER.fatal("!!! WARNING WARNING WARNING !!!\n");

						exception.printStackTrace();
					}
					return;
				} else {
					try {
						NbtCompound read = NbtIo.readCompressed(root);
						read.getKeys().forEach(key -> DATA.put(key, read.get(key)));
						NbtList list = DATA.getList("GalaxyData", NbtList.LIST_TYPE);
						StarfieldGenerator.STARS.clear();

						HashMap<Integer, HashMap<Integer, HashMap<Integer, Star>>> quadrants = new HashMap<>();

						int currentC = 0;
						int currentQ = 0;

						for (NbtElement quadrant : list) {
							HashMap<Integer, HashMap<Integer, Star>> clusters = new HashMap<>();
							if (quadrant instanceof NbtList clusterList) {
								for (NbtElement cluster : clusterList) {
									if (cluster instanceof NbtList starList) {
										HashMap<Integer, Star> stars = new HashMap<>();
										for (NbtElement star : starList) {
											if (star instanceof NbtCompound compound) {
												Star deserialized = Star.deserialize(compound);
												stars.put(deserialized.position(), deserialized);
											}
										}
										clusters.put(++ currentC, stars);
									}
								}

								currentC = 0;
								quadrants.put(++ currentQ, clusters);
							}
						}

						StarfieldGenerator.STARS.putAll(quadrants);
					} catch (IOException exception) {
						LOGGER.fatal("!!! CRITICAL CRITICAL CRITICAL !!!");
						LOGGER.fatal("FAILED TO READ LIMBO DRIVE PERSISTENT DATA!");
						LOGGER.fatal("ALL MOD-SPECIFIC DATA WILL BE RESET!");
						LOGGER.fatal("!!! CRITICAL CRITICAL CRITICAL !!!\n");

						exception.printStackTrace();
					}
				}
			});

			ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
				File root = new File(server.getSavePath(WorldSavePath.ROOT) + "/limbodrive.dat");
				if (root.exists()) {
					try {
						NbtIo.writeCompressed(DATA, root);
					} catch (IOException exception) {
						LOGGER.fatal("!!! CRITICAL CRITICAL CRITICAL !!!");
						LOGGER.fatal("FAILED TO WRITE LIMBO DRIVE PERSISTENT DATA DURING SHUTDOWN!");
						LOGGER.fatal("ANY MOD-SPECIFIC DATA HAS NOW BEEN LOST!");
						LOGGER.fatal(youWereWarned ? "YOU WERE WARNED ABOUT THIS." : "SOMETHING HAS GONE SERIOUSLY WRONG!!");
						LOGGER.fatal("!!! CRITICAL CRITICAL CRITICAL !!!\n");

						exception.printStackTrace();
					}
				}
			});
		}
	}

	public static class Screens {
		public static ScreenHandlerType<StarmapGUI.Description> STARMAP;
	}

	public static class Blocks {
		public static final Block STARMAP = new StarmapBlock();

		public static void init() {
			Registry.register(Registries.BLOCK, new Identifier(
					"limbodrive",
					"starmap"
			), STARMAP);
		}
	}

	public static class BlockEntities {
		public static final BlockEntityType<StarmapBlock.Entity> STARMAP = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				new Identifier("limbodrive", "starmap"),
				FabricBlockEntityTypeBuilder.create(StarmapBlock.Entity::new, Blocks.STARMAP).build()
		);

		public static void init() {}
	}
}