package limbo.drive.module.mixin;


import limbo.drive.module.world.Worldbleed;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class RiftOpenerMixin {

    @Inject(at = @At("HEAD"), method = "onBlockAdded")
    public void addRift(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (state.getBlock() == Blocks.REINFORCED_DEEPSLATE) {
            Worldbleed.start(world, pos);
        }
    }
}
