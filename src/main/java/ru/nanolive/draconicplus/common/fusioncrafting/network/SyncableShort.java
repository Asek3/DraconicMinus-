package ru.nanolive.draconicplus.common.fusioncrafting.network;

import com.brandon3055.brandonscore.BrandonsCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileInventoryBase;
import ru.nanolive.draconicplus.network.PacketDispatcher;

/**
 * Created by brandon3055 on 26/3/2016.
 */
public class SyncableShort extends SyncableObject {

    public short value;
    private short lastTickValue;

    public SyncableShort(short value, boolean syncInTile, boolean syncInContainer, boolean updateOnReceived) {
        super(syncInTile, syncInContainer, updateOnReceived);
        this.value = this.lastTickValue = value;
    }

    public SyncableShort(short value, boolean syncInTile, boolean syncInContainer) {
        super(syncInTile, syncInContainer);
        this.value = this.lastTickValue = value;
    }

    @Override
    public void detectAndSendChanges(TileInventoryBase tile, EntityPlayer player, boolean forceSync) {
        if (lastTickValue != value || forceSync) {
            lastTickValue = value;
            tile.markDirty();
            if (player == null) {
            	PacketDispatcher.NETWORK.sendToAllAround(new PacketSyncableObject(tile, index, value, updateOnReceived), tile.syncRange());
            } else if (player instanceof EntityPlayerMP) {
            	PacketDispatcher.NETWORK.sendTo(new PacketSyncableObject(tile, index, value, updateOnReceived), (EntityPlayerMP) player);
            } else System.out.println("SyncableInt#detectAndSendChanges No valid destination for sync packet!");
        }
    }

    @Override
    public void updateReceived(PacketSyncableObject packet) {
        if (packet.dataType == PacketSyncableObject.SHORT_INDEX) {
            value = packet.shortValue;
        }
    }

    @Override
    public void toNBT(NBTTagCompound compound) {
        compound.setShort("SyncableShort" + index, value);
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
        if (compound.hasKey("SyncableShort" + index)) {
            value = compound.getShort("SyncableShort" + index);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}