package limbo.drive.module.limbo;

import net.minecraft.block.BlockState;
import net.minecraft.block.SculkBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class VoidSculkBlock extends SculkBlock {
    private static final SculkSpreadManager SPREAD_MANAGER = new SculkSpreadManager(false, BlockTags.SCULK_REPLACEABLE, 70, Integer.MAX_VALUE, 80, 0);

    public VoidSculkBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        SPREAD_MANAGER.spread(pos, random.nextBetween(0, 3));
    }
}
