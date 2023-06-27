package limbo.drive.util.registry;

import limbo.drive.module.world.block.LimboRiftBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LimboBlockRegistry {
    public static final Block RIFT = new LimboRiftBlock();

    public static void register() {
        Registry.register(
                Registries.BLOCK,
                new Identifier("limbodrive", "rift"),
                LimboBlockRegistry.RIFT
        );
    }
}
