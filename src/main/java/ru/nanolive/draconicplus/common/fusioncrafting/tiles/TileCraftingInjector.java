package ru.nanolive.draconicplus.common.fusioncrafting.tiles;

import java.util.List;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import ru.nanolive.draconicplus.common.fusioncrafting.ICraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionCraftingInventory;
import ru.nanolive.draconicplus.common.fusioncrafting.blocks.CraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.network.SyncableByte;
import ru.nanolive.draconicplus.common.fusioncrafting.network.SyncableInt;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class TileCraftingInjector extends TileInventoryBase implements IEnergyReceiver, ICraftingInjector, ISidedInventory {

    public final SyncableByte facing = new SyncableByte((byte)0, true, false, true);
    private final SyncableInt energy = new SyncableInt(0, true, false);
    public IFusionCraftingInventory currentCraftingInventory = null;

    public TileCraftingInjector(){
        this.setInventorySize(1);
        registerSyncableObject(facing, true);
        registerSyncableObject(energy, true);
    }

    public int ticker = 0;
    
    @Override
    public void updateEntity() {
    	if(ticker++ > 60) {
    		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    		ticker = 0;
    	}
    }
    
    @Override
    public void updateBlock() {
        super.updateBlock();
        detectAndSendChanges();
    }

    //region IEnergy

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        validateCraftingInventory();
        if (currentCraftingInventory != null){
            int maxRFPerTick = currentCraftingInventory.getRequiredCharge() / 300;
            int maxAccept = Math.min(maxReceive, Math.min(currentCraftingInventory.getRequiredCharge() - energy.value, maxRFPerTick));

            if (!simulate){
                energy.value += maxAccept;
            }

            return maxAccept;
        }

        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energy.value;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from != from.getOrientation(facing.value);
    }

    //endregion

    //region ICraftingPedestal

    @Override
    public int getPedestalTier() {
    	return this.blockMetadata;
    }

    @Override
    public ItemStack getStackInPedestal() {
        return getStackInSlot(0);
    }

    @Override
    public void setStackInPedestal(ItemStack stack) {
        setInventorySlotContents(0, stack);
    }

    @Override
    public boolean setCraftingInventory(IFusionCraftingInventory craftingInventory) {
        if (validateCraftingInventory() && !worldObj.isRemote) {
            return false;
        }
        currentCraftingInventory = craftingInventory;
        return true;
    }

    @Override
    public EnumFacing getDirection() {
        return EnumFacing.getFront(facing.value);
    }

    @Override
    public int getCharge() {
        return energy.value;
    }

    private boolean validateCraftingInventory(){
        if (getStackInPedestal() != null && currentCraftingInventory != null && currentCraftingInventory.craftingInProgress() && !((TileEntity)currentCraftingInventory).isInvalid()){
            return true;
        }

        currentCraftingInventory = null;
        return false;
    }


    @Override
    public void onCraft() {
        if (currentCraftingInventory != null){
            energy.value = 0;
        }
    }

    //endregion

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{0};
	}
    
    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, int direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int direction) {
        return true;
    }
    
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        updateBlock();
    }
    
	 @Override
	 public void readFromNBT(NBTTagCompound nbttagcompound) {
	        super.readFromNBT(nbttagcompound);
			
	        facing.value = nbttagcompound.getByte("Facing");
	        blockMetadata = nbttagcompound.getInteger("Metadata");
	        energy.value = nbttagcompound.getInteger("Energy");
			
	        NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
	        this.inventoryStacks  = new ItemStack[getSizeInventory()];
	        for (int i = 0; i < nbttaglist.tagCount(); i++) {
	            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
	            int j = nbttagcompound1.getByte("Slot") & 0xFF;
	            if (j >= 0 && j < this.inventoryStacks .length) {
	                this.inventoryStacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
	            }
	        }
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
	        super.writeToNBT(nbttagcompound);
			
	        nbttagcompound.setByte("Facing", facing.value);
	        nbttagcompound.setInteger("Metadata", blockMetadata);
	        nbttagcompound.setInteger("Energy", energy.value);
	        
	        NBTTagList nbttaglist = new NBTTagList();
	        for (int i = 0; i < this.inventoryStacks.length; i++) {
	            if (this.inventoryStacks[i] != null) {
	                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	                nbttagcompound1.setByte("Slot", (byte)i);
	                this.inventoryStacks[i].writeToNBT(nbttagcompound1);
	                nbttaglist.appendTag(nbttagcompound1);
	            }
	        }
	        nbttagcompound.setTag("Items", nbttaglist);
	}

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}