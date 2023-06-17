package limbo.drive.starfield;

import limbo.drive.LimboDrive;
import limbo.drive.util.render.PB3K;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StarmapBlock extends BlockWithEntity {
    public StarmapBlock() {
        super(FabricBlockSettings.copy(Blocks.BLACK_CONCRETE));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.SUCCESS;
    }

    public static class Entity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
        private final DefaultedList<ItemStack> CONTENTS = DefaultedList.ofSize(4, ItemStack.EMPTY);

        public Entity(BlockPos pos, BlockState state) {
            super(LimboDrive.BlockEntities.STARMAP, pos, state);
        }

        @Override
        public DefaultedList<ItemStack> getItems() {
            return CONTENTS;
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            super.readNbt(nbt);
            Inventories.readNbt(nbt, CONTENTS);
        }

        @Override
        public void writeNbt(NbtCompound nbt) {
            Inventories.writeNbt(nbt, CONTENTS);
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("limbodrive.starmap.gui");
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            PB3K.RenderTarget.initialize(new Identifier("limbodrive" , "starmap"));
            return new PB3K.RenderTarget(syncId, playerInventory);
//            return new StarmapGUI.Description(syncId, playerInventory, ScreenHandlerContext.create(this.world, this.pos));
        }
    }
    /**
     * A simple {@code Inventory} implementation with only default methods + an item list getter.
     * <br/>
     * Originally by Juuz, shamelessly stolen from Fabric Wiki by Lily
     */
    public interface Inventory extends net.minecraft.inventory.Inventory {
        /**
         * Retrieves the item list of this inventory.
         * Must return the same instance every time it's called.
         */
        DefaultedList<ItemStack> getItems();

        /**
         * Creates an inventory from the item list.
         */
        static Inventory of(DefaultedList<ItemStack> items) {
            return () -> items;
        }

        /**
         * Returns the inventory size.
         */
        @Override
        default int size() {
            return getItems().size();
        }

        /**
         * Checks if the inventory is empty.
         * @return true if this inventory has only empty stacks, false otherwise.
         */
        @Override
        default boolean isEmpty() {
            for (int i = 0; i < size(); i++) {
                ItemStack stack = getStack(i);
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Retrieves the item in the slot.
         */
        @Override
        default ItemStack getStack(int slot) {
            return getItems().get(slot);
        }

        /**
         * Removes items from an inventory slot.
         * @param slot  The slot to remove from.
         * @param count How many items to remove. If there are less items in the slot than what are requested,
         *              takes all items in that slot.
         */
        @Override
        default ItemStack removeStack(int slot, int count) {
            ItemStack result = Inventories.splitStack(getItems(), slot, count);
            if (!result.isEmpty()) {
                markDirty();
            }
            return result;
        }

        /**
         * Removes all items from an inventory slot.
         * @param slot The slot to remove from.
         */
        @Override
        default ItemStack removeStack(int slot) {
            return Inventories.removeStack(getItems(), slot);
        }

        /**
         * Replaces the current stack in an inventory slot with the provided stack.
         * @param slot  The inventory slot of which to replace the itemstack.
         * @param stack The replacing itemstack. If the stack is too big for
         *              this inventory ({@link Inventory#getMaxCountPerStack()}),
         *              it gets resized to this inventory's maximum amount.
         */
        @Override
        default void setStack(int slot, ItemStack stack) {
            getItems().set(slot, stack);
            if (stack.getCount() > stack.getMaxCount()) {
                stack.setCount(stack.getMaxCount());
            }
        }

        /**
         * Clears the inventory.
         */
        @Override
        default void clear() {
            getItems().clear();
        }

        /**
         * Marks the state as dirty.
         * Must be called after changes in the inventory, so that the game can properly save
         * the inventory contents and notify neighboring blocks of inventory changes.
         */
        @Override
        default void markDirty() {
            // Override if you want behavior.
        }

        /**
         * @return true if the player can use the inventory, false otherwise.
         */
        @Override
        default boolean canPlayerUse(PlayerEntity player) {
            return true;
        }
    }
}
