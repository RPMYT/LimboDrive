package limbo.drive.module.mixin;


import limbo.drive.module.limbo.Worldbleed;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class RiftOpenerMixin {
    @Unique
    private BlockPos last;

    @Inject(at = @At("HEAD"), method = "onBlockAdded")
    public void addRift(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (state.getBlock() == Blocks.REINFORCED_DEEPSLATE) {
            System.out.println("Checking for valid rift...");
            BlockPattern.Result result = Worldbleed.RIFT_PATTERN.searchAround(world, pos);
            if (result != null) {
                BlockPos ftl = result.getFrontTopLeft();
                if (this.last == null) {
                    this.last = ftl;
                }

                if (this.last == ftl) {
                    System.out.println("Adding the block at " + pos + " to the current rift...");
                    Worldbleed.add(pos);
                } else {
                    System.out.println("Finished rift creation!");
                    Worldbleed.finish();
                }
            }
        }
    }
}
