package limbo.drive.util.render.core;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;

public class Screen extends CottonInventoryScreen<PB3K.RenderTarget> {
    public Screen(PB3K.RenderTarget target, PlayerInventory inventory) {
        super(target, inventory);
    }
}