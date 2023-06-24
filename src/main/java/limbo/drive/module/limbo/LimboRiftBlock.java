package limbo.drive.module.limbo;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LimboRiftBlock extends Block {
    public LimboRiftBlock() {
        super(FabricBlockSettings.copyOf(Blocks.NETHER_PORTAL));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.damage(new RejectionDamage(null), Float.MAX_VALUE);
    }

    @Override
    public boolean canMobSpawnInside(BlockState state) {
        return false;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient) {
            if (!player.getAbilities().creativeMode) {
                world.getServer().getPlayerManager().broadcast(Text.translatable("limbodrive.impossible", player.getName()), false);
            }
        }
    }
}
