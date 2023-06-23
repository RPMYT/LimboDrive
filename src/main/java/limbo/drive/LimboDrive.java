package limbo.drive;

import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.module.limbo.PearlbombDetonatorItem;
import limbo.drive.module.limbo.PearlbombEntity;
import limbo.drive.module.limbo.PearlbombItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class LimboDrive {
	public static class AtomicFuckery {
		public static final AtomicReference<DynamicRegistryManager> REGISTRY_MANAGER = new AtomicReference<>(null);
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
			this.performFuckery();

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

		private void performFuckery() {
			ServerLifecycleEvents.SERVER_STARTED.register(server -> AtomicFuckery.REGISTRY_MANAGER.set(server.getRegistryManager()));

			ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			});
		}
	}
}