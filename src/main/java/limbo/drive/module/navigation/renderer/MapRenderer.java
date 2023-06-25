package limbo.drive.module.navigation.renderer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Pair;

public class MapRenderer {
    public static ImmutableList<Pair<Integer, Integer>> CURRENTLY_DRAWN;

    public static void render(RenderingContext context, RenderBuffer buffer) {
        buffer.flush(context);
    }
}