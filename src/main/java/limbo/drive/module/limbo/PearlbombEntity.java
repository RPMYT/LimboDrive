package limbo.drive.module.limbo;

import limbo.drive.LimboDrive;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class PearlbombEntity extends EnderPearlEntity {
    private final boolean sticky;
    private Entity stuckTo = null;
    private boolean hasStuck = false;

    boolean shouldDetonate = false;
    private int timeSinceSticking = 0;

    public PearlbombEntity(EntityType<? extends PearlbombEntity> type, World world) {
        super(type, world);
        this.sticky = false;
    }

    public PearlbombEntity(World world, LivingEntity owner, boolean sticky) {
        super(LimboDrive.Initializer.PEARLBOMB, world);
        this.setOwner(owner);
        this.sticky = sticky;
        this.setPos(owner.getX(), owner.getEyeY() - 0.10000000149011612, owner.getZ());
    }

    private void detonate() {
        if (!this.getWorld().isClient) {
            this.shouldDetonate = false;
            Explosion explosion = this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 6.5f, World.ExplosionSourceType.MOB);
            Box box = Box.of(this.getPos(), 4.5, 2.25, 4.5);

            this.stuckTo = null;
            for (Entity other : this.getWorld().getOtherEntities(this, box)) {
                System.out.println(other);
                if (other instanceof LivingEntity living) {
                    float damage = 65 - (living.distanceTo(this) * 4.5f);
                    living.takeKnockback(1.25 / living.distanceTo(this), 2.0, 2.0);

                    if (this.getOwner() != null && living.getUuid() == this.getOwner().getUuid()) {
                        damage = living.getMaxHealth();
                    }

                    if (living instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                        damage = 0;
                    }

                    System.out.println("damaging entity " + living + " with amount " + damage);
                    living.damage(new LimboDriveDamage(this.getOwner() == null ? this : this.getOwner()), damage);
                }
            }

            explosion.collectBlocksAndDamageEntities();
            explosion.affectWorld(true);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onCollision(HitResult result) {
        if (this.stuckTo != null || this.shouldDetonate || this.hasStuck) {
            return;
        }

        if (!sticky) {
            super.onCollision(result);
            this.shouldDetonate = true;
            return;
        }

        if (result instanceof EntityHitResult ehr) {
            this.stuckTo = ehr.getEntity();
        }

        if (result instanceof BlockHitResult bhr) {
            if (this.getWorld().getBlockState(bhr.getBlockPos()).getBlock() instanceof HoneyBlock) {
                Vec3d previous = this.getVelocity();
                Vec3d updated = previous.negate().multiply(1.3);

                this.setVelocity(updated.x * 1.1, updated.y * 1.1, updated.z * 1.1, 2.3f, 0.15f);
                this.updateRotation();

                System.out.println("New velocity: " + this.getVelocity());
            } else {
                this.hasStuck = true;
            }
        }
    }

    @Override
    public void tick() {

        super.tick();
//        System.out.println(this.getPos());

        if (this.shouldDetonate) {
            this.detonate();
        }

        if (this.stuckTo != null) {
            this.setPos(this.stuckTo.getX(), this.stuckTo.getY(), this.stuckTo.getZ());
            this.timeSinceSticking++;

            if (this.timeSinceSticking >= 600) {
                this.shouldDetonate = true;
            }
        }

        if (this.age >= 1200) {
            this.detonate();
        }
    }
}
