package limbo.drive.util.data;

import limbo.drive.module.navigation.util.NavigationPermissions;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;

public class NetworkHandler {
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.REQUEST_DEBUG_MODE, (client, handler, buf, responseSender) -> {
            NavigationPermissions.DEBUG_ENABLE = buf.readBoolean();
            NavigationPermissions.DEBUG_EDIT_MAPS = buf.readBoolean();
            NavigationPermissions.DEBUG_EDIT_BATTLES = buf.readBoolean();
            NavigationPermissions.DEBUG_EDIT_CHARDATA = buf.readBoolean();
        });
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.REQUEST_DEBUG_MODE, (server, player, handler, buf, responseSender) -> {
            if (server.getPlayerManager().isOperator(player.getGameProfile())) {
                PacketByteBuf response = PacketByteBufs.create();
                response.writeBoolean(true);
                response.writeBoolean(true);
                response.writeBoolean(true);
                response.writeBoolean(true);
                ServerPlayNetworking.send(player, PacketIdentifiers.REQUEST_DEBUG_MODE, response);
            }
        });
    }
}
