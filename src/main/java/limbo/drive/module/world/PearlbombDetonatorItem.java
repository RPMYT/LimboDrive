package limbo.drive.module.world;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PearlbombDetonatorItem extends Item {
    public PearlbombDetonatorItem() {
        super(new Item.Settings()
            .maxCount(1)
            .rarity(Rarity.UNCOMMON)
        );
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
            int detonated = 0;
            if (!world.isClient()) {
                detonated = PearlbombItem.PLAYER_STICKY_BOMBS.get(user.getUuid()).size();
                PearlbombItem.PLAYER_STICKY_BOMBS.get(user.getUuid()).clear();
            }

            user.getItemCooldownManager().set(this, 20 * detonated);
            return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        } else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.of("What makes me a good Demoman?"));
        tooltip.add(Text.of(""));
        tooltip.add(Text.of("If I were a bad Demoman...").copy().formatted(Formatting.ITALIC));
        tooltip.add(Text.of("I wouldn't be sittin' here, discussin' it with you, now would I?").copy().formatted(Formatting.ITALIC));
    }
}
