package limbo.drive.module.limbo;

import limbo.drive.LimboDrive;
import net.minecraft.block.Block;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;

public class LimboDriveDamage extends DamageSource {
    private final Entity attacker;

    public LimboDriveDamage(@Nullable Entity attacker) {
        super(LimboDrive.AtomicFuckery.REGISTRY_MANAGER.get().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.OUT_OF_WORLD));
        this.attacker = attacker;
    }

    @Override
    public Text getDeathMessage(LivingEntity killed) {
        Random random = Random.create();

        boolean xkcd = random.nextBetween(0, 100) == 100;
        if (this.attacker != null && this.getPosition() != null) {
            World world = this.attacker.getWorld();
            if (!world.isClient) {
                if (world.getDimension() == LimboDrive.AtomicFuckery.REGISTRY_MANAGER.get().get(RegistryKeys.DIMENSION_TYPE).get(DimensionTypes.THE_END)) {
                    BlockPos pos = new BlockPos((int) this.getPosition().x, (int) this.getPosition().y, (int) this.getPosition().z);
                    if (world.getBlockState(pos).getBlock() instanceof EndGatewayBlock) {
                        xkcd = true;
                    }
                }
            }
        }

        if (xkcd) {
            return Text.translatable("limbodrive.death.xkcd", killed.getName());
        }

        return this.attacker instanceof PearlbombEntity ?
            Text.translatable("limbodrive.death.singularity", killed.getName(), this.attacker.getName()) :
            Text.translatable("limbodrive.death.rejection", killed.getName());
    }
}
