package limbo.drive;

import limbo.drive.util.graphics.PB3K;
import limbo.drive.util.registry.LimboBlockRegistry;
import limbo.drive.util.registry.LimboEntityRegistry;
import limbo.drive.util.registry.LimboItemRegistry;
import limbo.drive.util.data.NetworkHandler;
import limbo.drive.util.data.RiftDataManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.registry.DynamicRegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LimboDrive {
	public static class Fuckery {
		public static DynamicRegistryManager REGISTRY_MANAGER;
	}

	public static class Client {
		public static class Initializer implements ClientModInitializer {
			@Override
			public void onInitializeClient() {
				PB3K.setupClient();
				EntityRendererRegistry.register(LimboEntityRegistry.PEARLBOMB, FlyingItemEntityRenderer::new);
				NetworkHandler.registerClient();
			}
		}
	}

    public static final Logger LOGGER = LogManager.getLogger("Limbo Drive");

	public static class Initializer implements ModInitializer {
		@Override
		public void onInitialize() {
			this.registerEvents();
			PB3K.setupServer();

			LimboItemRegistry.register();
			LimboBlockRegistry.register();
			LimboEntityRegistry.register();

			NetworkHandler.registerServer();
			RiftDataManager.registerEvents();
		}

		private void registerEvents() {
			ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				// I sure do love me some fuckery
				Fuckery.REGISTRY_MANAGER = server.getRegistryManager();
			});
		}
	}
}