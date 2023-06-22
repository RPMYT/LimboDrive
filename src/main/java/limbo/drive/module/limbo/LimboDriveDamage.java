package limbo.drive.module.limbo;

import limbo.drive.LimboDrive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
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
        if (random.nextBetween(0, 100) == 100) {
            return Text.translatable("limbodrive.death.xkcd");
        }

        return this.attacker instanceof PearlbombEntity ?
            Text.translatable("limbodrive.death.singularity", killed.getName(), this.attacker.getName()) :
            Text.translatable("limbodrive.death.rejection", killed.getName());
    }
}
