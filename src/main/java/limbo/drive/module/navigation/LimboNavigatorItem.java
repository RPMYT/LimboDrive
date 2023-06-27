package limbo.drive.module.navigation;

import limbo.drive.api.graphics.core.PB3K;
import limbo.drive.module.navigation.renderer.gui.NavigationGUI;
import limbo.drive.module.navigation.renderer.gui.RenderMode;
import limbo.drive.module.navigation.renderer.gui.RenderingContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LimboNavigatorItem extends Item implements NamedScreenHandlerFactory {
    public LimboNavigatorItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        PB3K.RenderTarget.initialize(new NavigationGUI());

        RenderingContext context;
        ItemStack stack = user.getStackInHand(hand);

        //noinspection DataFlowIssue
        if (stack.hasNbt() && stack.getNbt().contains("NavigationContext")) {
            context = RenderingContext.deserialize(stack.getNbt().getCompound("NavigationContext"));
        } else {
            context = new RenderingContext(
                null,
                0,
                0,
                "TestRoom",
                "test_south",
                12,
                12,
                0,
                0,
                RenderMode.MAP_VIEWER
            );
        }

        NavigationGUI.contextualize(context);
        user.openHandledScreen(this);
        return TypedActionResult.consume(stack);
    }

    @Override
    public Text getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PB3K.RenderTarget(syncId, playerInventory);
    }
}