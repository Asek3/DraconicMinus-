package ru.nanolive.draconicplus.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketSpawnParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketSyncableObject;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketTileMessage;

public class PacketDispatcher {

	private static byte packetId = 0;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("DraconicPlus");

    public static final void preInit() {
        registerMessage(PacketSyncableObject.class);
        registerMessage(PacketTileMessage.class);
        registerMessage(PacketSpawnParticle.class);
    }

    private static final <T extends AbstractMessage<T> & cpw.mods.fml.common.network.simpleimpl.IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
        if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
          packetId = (byte)(packetId + 1);
          NETWORK.registerMessage(clazz, clazz, packetId, Side.CLIENT);
        } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
          packetId = (byte)(packetId + 1);
          NETWORK.registerMessage(clazz, clazz, packetId, Side.SERVER);
        } else {
        	NETWORK.registerMessage(clazz, clazz, packetId, Side.CLIENT);
          packetId = (byte)(packetId + 1);
          NETWORK.registerMessage(clazz, clazz, packetId, Side.SERVER);
        } 
      }
    
    
    public static void sendTo(IMessage message, EntityPlayerMP player) {
    	NETWORK.sendTo(message, player);
    }
    
    public static void sendToAllAround(IMessage message, TargetPoint point) {
    	NETWORK.sendToAllAround(message, point);
    }
    
    public static void sendToServer(IMessage message) {
    	NETWORK.sendToServer(message);
    }

}
