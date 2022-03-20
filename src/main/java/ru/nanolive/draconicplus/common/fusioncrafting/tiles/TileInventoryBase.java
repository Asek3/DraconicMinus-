package ru.nanolive.draconicplus.common.fusioncrafting.tiles;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import ru.nanolive.draconicplus.common.fusioncrafting.IDataRetainerTile;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketSyncableObject;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketTileMessage;
import ru.nanolive.draconicplus.common.fusioncrafting.network.SyncableObject;

/**
 * Created by brandon3055 on 26/3/2016.
 * The base class for all inventory tiles
 */
public class TileInventoryBase extends TileEntity implements IInventory {

    protected ItemStack[] inventoryStacks = new ItemStack[0];
    protected int stackLimit = 64;

    public TileInventoryBase() {

    }

    protected void setInventorySize(int size) {
        inventoryStacks = new ItemStack[size];
    }

    @Override
    public int getSizeInventory() {
        return inventoryStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index < inventoryStacks.length && index >= 0 ? inventoryStacks[index] : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = getStackInSlot(index);

        if (itemstack != null) {
            if (itemstack.stackSize <= count) {
                setInventorySlotContents(index, null);
            } else {
                itemstack = itemstack.splitStack(count);
                if (itemstack.stackSize == 0) {
                    setInventorySlotContents(index, null);
                }
            }
        }
        return itemstack;
    }
    
	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack item = getStackInSlot(index);

        if (item != null) {
            setInventorySlotContents(index, null);
        }

        return item;
	}

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= inventoryStacks.length){
            return;
        }

        inventoryStacks[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (worldObj == null) {
            return false;
        }
        else if (worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) {
            return false;
        }
        return player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) < 64;
    }
    
    protected Map<Byte, SyncableObject> syncableObjectMap = new HashMap<Byte, SyncableObject>();
    protected int objIndexCount = 0;
    protected int viewRange = -1;
    protected boolean shouldRefreshOnState = true;
    
    public NetworkRegistry.TargetPoint syncRange() {
        if (viewRange == -1 && !worldObj.isRemote) {
            Field f = ReflectionHelper.findField(PlayerManager.class, "playerViewRadius", "field_72698_e");
            f.setAccessible(true);
            try {
                viewRange = f.getInt(((WorldServer) worldObj).getPlayerManager());
            }
            catch (IllegalAccessException e) {
                System.out.println("A THING BROKE!!!!!!!");
                e.printStackTrace();
            }
        } else if (worldObj.isRemote) {
        	System.out.println("Hay! Someone is doing a bad thing!!! Check your side!!!!!!!");
        }
        return new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, viewRange * 16);
    }

    /*@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (this instanceof IDataRetainerTile) {
            ((IDataRetainerTile) this).readRetainedData(pkt.func_148857_g());
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            syncableObject.fromNBT(pkt.func_148857_g());
        }

    }*/
    
    public void receivePacketFromServer(PacketTileMessage packet) {}
    
	public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {}
    
    public void detectAndSendChanges() {
        detectAndSendChanges(false);
    }

    public void detectAndSendChanges(boolean forceSync) {
        if (worldObj.isRemote) return;
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.syncInTile) {
                syncableObject.detectAndSendChanges(this, null, forceSync);
            }
        }
    }

    public void detectAndSendChangesToPlayer(boolean forceSync, EntityPlayerMP playerMP) {
        if (worldObj.isRemote) return;
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.syncInContainer) {
                syncableObject.detectAndSendChanges(this, playerMP, forceSync);
            }
        }
    }

    public void registerSyncableObject(SyncableObject object) {
        registerSyncableObject(object, true);
    }

    public void registerSyncableObject(SyncableObject object, boolean saveToNBT) {
        registerSyncableObject(object, saveToNBT, false);
    }

    /**
     * Registers a syncable object. These objects will automatically be synchronized with the client.
     * Note: you are required to call detectAndSendChanges server side in order for objects to detect and send changes to the client.
     * @param object The object.
     * @param saveToNBT If true the object will ba saved and loaded from NBT.
     * @param saveToItem If true and the tile is an instance of {@link IDataRetainerTile} the object will be saved and loaded from the item when the tile is broken.
     */
    public void registerSyncableObject(SyncableObject object, boolean saveToNBT, boolean saveToItem) {
        if (objIndexCount > Byte.MAX_VALUE) {
            throw new RuntimeException("TileBCBase#registerSyncableObject To many objects registered!");
        }
        syncableObjectMap.put((byte) objIndexCount, object.setIndex(objIndexCount));
        object.setSaveMode(saveToNBT, saveToItem);

        objIndexCount++;
    }
    
    public void updateBlock() {
        worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
    }
    
    public void receiveSyncPacketFromServer(PacketSyncableObject packet) {
        if (syncableObjectMap.containsKey(packet.index)) {
            SyncableObject object = syncableObjectMap.get(packet.index);
            object.updateReceived(packet);

            if (object.updateOnReceived) {
                updateBlock();
            }
        }
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}


}