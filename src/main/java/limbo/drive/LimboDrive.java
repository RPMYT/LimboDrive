package limbo.drive;

import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.module.limbo.PearlbombDetonatorItem;
import limbo.drive.module.limbo.PearlbombEntity;
import limbo.drive.module.limbo.PearlbombItem;
import limbo.drive.module.limbo.Worldbleed;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class LimboDrive {
	public static class Fuckery {
		public static DynamicRegistryManager REGISTRY_MANAGER;
	}

	public static class Client {
		public static class Initializer implements ClientModInitializer {
			@Override
			public void onInitializeClient() {

				PB3K.initialize();
				EntityRendererRegistry.register(LimboDrive.Initializer.PEARLBOMB, FlyingItemEntityRenderer::new);
			}
		}
	}

    public static final Logger LOGGER = LogManager.getLogger("Limbo Drive");

	public static class Initializer implements ModInitializer {


		public static final EntityType<PearlbombEntity> PEARLBOMB = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("limbodrive", "pearlbomb"),
			FabricEntityTypeBuilder.<PearlbombEntity>create(
				SpawnGroup.MISC,
				PearlbombEntity::new
			).dimensions(EntityDimensions.fixed(0.1f, 0.1f)).build()
		);

		public static final Item PEARLBOMB_DETONATOR = new PearlbombItem(false);
		public static final Item PEARLBOMB_DETONATOR_STICKY = new PearlbombItem(true);

		public static final Item DETONATOR = new PearlbombDetonatorItem();

		@Override
		public void onInitialize() {
			this.registerEvents();

			Registry.register(
				Registries.ITEM,
				new Identifier("limbodrive", "pearlbomb"),
				PEARLBOMB_DETONATOR
			);

			Registry.register(
				Registries.ITEM,
				new Identifier("limbodrive", "pearlbomb_sticky"),
				PEARLBOMB_DETONATOR_STICKY
			);

			Registry.register(
				Registries.ITEM,
				new Identifier("limbodrive", "detonator"),
				DETONATOR
			);

			PB3K.setup();
		}

		private void registerEvents() {
			ServerLifecycleEvents.SERVER_STARTED.register(server ->{
				// I sure do love me some fuckery
				Fuckery.REGISTRY_MANAGER = server.getRegistryManager();

				File saved = server.getFile("world/bleed.dat");

				boolean proceed = true;
				if (!saved.exists()) {
					proceed = false;
					try {
						saved.createNewFile();
					} catch (IOException exception) {
						LOGGER.error("Failed to create LimboDrive Worldbleed data file.");
						LOGGER.error("You probably have a permissions error somewhere; might wanna fix that.");
						LOGGER.error("Here's the stacktrace:");
						exception.printStackTrace();
					}
				}

				if (proceed) {
					try {
						NbtCompound read = NbtIo.readCompressed(saved);
						Worldbleed.initialize(read);
					} catch (IOException exception) {
						LOGGER.error("Failed to read LimboDrive Worldbleed data.");
						LOGGER.error("This is pretty bad; the mod uses this quite a lot..");
						LOGGER.error("Might wanna fix this; though hopefully it's minor.");
						LOGGER.error("Here's the stacktrace:");
						exception.printStackTrace();
					}
				}
			});

			ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
				File saved = server.getFile("world/bleed.dat");

				boolean proceed = true;
				if (!saved.exists()) {
					try {
						saved.createNewFile();
					} catch (IOException exception) {
						proceed = false;
						LOGGER.fatal("Failed to create LimboDrive Worldbleed data file.");
						LOGGER.fatal("Guess the world is safe, for now.");
						LOGGER.fatal("You probably have a permissions error somewhere; here's the stacktrace:");
						exception.printStackTrace();
					}
				}

				if (proceed) {
					try {
						NbtCompound data = Worldbleed.save();
						NbtIo.writeCompressed(data, saved);
					} catch (IOException exception) {
						LOGGER.fatal("Failed to write LimboDrive Worldbleed data.");
						LOGGER.fatal("Guess the world is safe, for now.");
						LOGGER.fatal("PLEASE send the following stacktrace to the developer!! ('lilirine' on Discord; or make an issue at https://github.com/RPMYT/LimboDrive");
						exception.printStackTrace();
					}
				}
			});
		}
	}
}