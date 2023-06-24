package limbo.drive.module.limbo;

import limbo.drive.LimboDrive;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndGatewayBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
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

    @Override
    public boolean shouldRender(double distance) {
        return !(this.stuckTo == this);
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
                    float damage = 65 - (living.distanceTo(this) * 12.5f);
                    living.takeKnockback(1.25 / living.distanceTo(this), 2.0, 2.0);

                    if (this.getOwner() != null && living.getUuid() == this.getOwner().getUuid()) {
                        damage = living.getMaxHealth();
                    }

                    if (living instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                        damage = 0;
                    }

//                    System.out.println("damaging entity " + living + " with amount " + damage);
                    living.damage(new RejectionDamage(this.getOwner() == null ? this : this.getOwner()), damage);
                }
            }
            if (this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof EndGatewayBlock) {
                if (LimboDrive.Fuckery.REGISTRY_MANAGER != null) {
                    ServerWorld world = (ServerWorld) this.getWorld();
                    if (world.getDimension() == LimboDrive.Fuckery.REGISTRY_MANAGER.get(RegistryKeys.DIMENSION_TYPE).get(DimensionTypes.THE_END)) {
                        if (!world.getEntitiesByType(EntityType.ENDER_DRAGON, dragon -> dragon.getPhaseManager().getCurrent().getType() == PhaseType.DYING).isEmpty()) {
                            if (Worldbleed.RIFT_COUNT == 0) {
                                world.getServer().getPlayerManager().broadcast(Text.of("The link between Limbo and Reality has been renewed, forever altering this world...").copy().formatted(Formatting.DARK_GRAY), false);
                                world.getServer().getPlayerManager().broadcast(Text.of("Things may no longer be as they seem.").copy().formatted(Formatting.DARK_RED), false);
                            }

                            Worldbleed.open(world.getServer().getOverworld());

                            // Singleton? More like SINGULARITY!
                            Explosion hypermurderizer = this.getWorld().createExplosion(this, 0, 64, 0, 18f, World.ExplosionSourceType.MOB);

                            this.stuckTo = null;
                            Box huge = Box.of(new Vec3d(0, 64, 0), 580, 280, 580);
                            for (Entity other : this.getWorld().getOtherEntities(this, huge)) {
//                                System.out.println(other);
                                if (other instanceof LivingEntity living) {
                                    living.takeKnockback(87.25 / living.distanceTo(this), 2.0, 2.0);
                                    float damage = Float.MAX_VALUE;

                                    if (living instanceof PlayerEntity player && player.getAbilities().creativeMode) {
                                        damage = 0;
                                    }

//                        System.out.println("damaging entity " + living + " with amount " + damage);
                                    living.damage(new RejectionDamage(this.getOwner() == null ? this : this.getOwner()), damage);

                                    if (!living.isDead() && !(living instanceof EnderDragonEntity) && damage != 0) {
                                        // Cancelling death events? Not cool.
                                        living.kill();

                                        if (!living.isDead()) {
                                            // You wanna be difficult? Fine.
                                            // This *will* freeze the world in singleplayer...
                                            // ...but that's what you get for cheating.
                                            living.remove(RemovalReason.DISCARDED);
                                        }
                                    }
                                }
                            }

                            hypermurderizer.collectBlocksAndDamageEntities();
                            hypermurderizer.affectWorld(true);
                        }
                    }
                }
            }

            explosion.collectBlocksAndDamageEntities();
            explosion.affectWorld(true);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        return !state.isAir();
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
            if (ehr.getEntity() != this) {
                this.stuckTo = ehr.getEntity();
            }
        }

        if (result instanceof BlockHitResult bhr) {
            if (!this.getWorld().getBlockState(bhr.getBlockPos()).isAir()) {
                if (this.getWorld().getBlockState(bhr.getBlockPos()).getBlock() instanceof HoneyBlock) {
                    Vec3d previous = this.getVelocity();
                    Vec3d updated = previous.negate();

                    this.setVelocity(updated.x, updated.y, updated.z, 1.17f, 0.15f);
                    this.updateRotation();
                } else {
                    this.hasStuck = true;
                    this.stuckTo = this;
                }
            }
        }
    }

    @Override
    public void tick() {
        if (!(this.getWorld().getBlockState(this.getBlockPos()).getBlock() instanceof AirBlock)) {
            this.stuckTo = this;
        }

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
        } else {
            super.tick();
        }

        if (this.age >= 1200) {
            this.detonate();
        }
    }

    @Override
    public boolean isSpectator() {
        return true;
    }
}
