package limbo.drive.util.registry;

import limbo.drive.module.world.entity.PearlbombEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LimboEntityRegistry {
    public static final EntityType<PearlbombEntity> PEARLBOMB = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("limbodrive", "pearlbomb"),
        FabricEntityTypeBuilder.<PearlbombEntity>create(
            SpawnGroup.MISC,
            PearlbombEntity::new
        ).dimensions(EntityDimensions.fixed(0.1f, 0.1f)).build()
    );

    public static void register() {
        // nothing here; entity types are registered immediately
        // this is just to load the class
    }
}
