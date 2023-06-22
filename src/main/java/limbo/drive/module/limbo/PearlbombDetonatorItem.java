package limbo.drive.module.limbo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

public class PearlbombDetonatorItem extends Item {
    public PearlbombDetonatorItem() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ServerWorld wrld = (ServerWorld) world;

            for (UUID bomb : PearlbombItem.PLAYER_STICKY_BOMBS.getOrDefault(user.getUuid(), new ArrayList<>())) {
                Entity entity = wrld.getEntity(bomb);
                if (entity instanceof PearlbombEntity pearlbomb) {
                    pearlbomb.shouldDetonate = true;
                }
            }
        }

        if (!PearlbombItem.PLAYER_STICKY_BOMBS.getOrDefault(user.getUuid(), new ArrayList<>()).isEmpty()) {
            if (!world.isClient()) {
                PearlbombItem.PLAYER_STICKY_BOMBS.get(user.getUuid()).clear();
            }

            user.getItemCooldownManager().set(this, 600);
            return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        } else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }
}
