package limbo.drive.starfield;

import limbo.drive.LimboDrive;
import limbo.drive.starfield.gui.StarfieldGenerationGUI;
import limbo.drive.util.render.core.PB3K;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    public static class Entity extends BlockEntity implements NamedScreenHandlerFactory {

        public Entity(BlockPos pos, BlockState state) {
            super(LimboDrive.BlockEntities.STARMAP, pos, state);
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("limbodrive.starmap.gui");
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            PB3K.RenderTarget.initialize(new StarfieldGenerationGUI());
            return new PB3K.RenderTarget(syncId, playerInventory);
        }
    }
}
