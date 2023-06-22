package limbo.drive.old.starmap;

import limbo.drive.api.graphics.core.PB3K;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class StarfieldMapItem extends Item {
    public StarfieldMapItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        PB3K.RenderTarget.initialize(new StarfieldMapGUI());
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
