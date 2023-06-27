package limbo.drive.util.registry;

import limbo.drive.module.world.item.LimboNavigatorItem;
import limbo.drive.module.world.item.PearlbombDetonatorItem;
import limbo.drive.module.world.item.PearlbombItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LimboItemRegistry {
    public static final Item PEARLBOMB_DETONATOR = new PearlbombItem(false);
    public static final Item PEARLBOMB_DETONATOR_STICKY = new PearlbombItem(true);
    public static final Item DETONATOR = new PearlbombDetonatorItem();
    public static final Item NAVIGATOR = new LimboNavigatorItem();

    public static void register() {
        Registry.register(
                Registries.ITEM,
                new Identifier("limbodrive", "pearlbomb"),
                LimboItemRegistry.PEARLBOMB_DETONATOR
        );

        Registry.register(
                Registries.ITEM,
                new Identifier("limbodrive", "pearlbomb_sticky"),
                LimboItemRegistry.PEARLBOMB_DETONATOR_STICKY
        );

        Registry.register(
                Registries.ITEM,
                new Identifier("limbodrive", "detonator"),
                LimboItemRegistry.DETONATOR
        );

        Registry.register(
                Registries.ITEM,
                new Identifier("limbodrive", "navigator"),
                LimboItemRegistry.NAVIGATOR
        );
    }
}
