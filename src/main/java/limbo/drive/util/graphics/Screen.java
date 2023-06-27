package limbo.drive.util.graphics;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;

@Environment(EnvType.CLIENT)
public class Screen extends CottonInventoryScreen<PB3K.RenderTarget> {
    public Screen(PB3K.RenderTarget target, PlayerInventory inventory) {
        super(target, inventory);
    }
}