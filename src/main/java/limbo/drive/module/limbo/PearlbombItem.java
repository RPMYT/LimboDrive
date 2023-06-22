package limbo.drive.module.limbo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PearlbombItem extends Item {
    static final HashMap<UUID, ArrayList<UUID>> PLAYER_STICKY_BOMBS = new HashMap<>();

    private final boolean sticky;

    public PearlbombItem(boolean sticky) {
        super(new Item.Settings().maxCount(16));
        this.sticky = sticky;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            PearlbombEntity entity = new PearlbombEntity(world, user, this.sticky);

            if (this.sticky) {
                ArrayList<UUID> bombs = PLAYER_STICKY_BOMBS.getOrDefault(user.getUuid(), new ArrayList<>());
                bombs.add(entity.getUuid());
                PLAYER_STICKY_BOMBS.put(user.getUuid(), bombs);
            }

            entity.setItem(stack);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(entity);

            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(stack, world.isClient());
    }
}
