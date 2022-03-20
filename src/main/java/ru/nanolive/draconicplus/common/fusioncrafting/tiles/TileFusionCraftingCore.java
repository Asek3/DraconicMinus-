package ru.nanolive.draconicplus.common.fusioncrafting.tiles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.ICraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionCraftingInventory;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionRecipe;
import ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.GlStateManager;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.DPEffectHandler;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.EffectTrackerFusionCrafting;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.EffectTrackerFusionCrafting.SubParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.ParticleFusionCrafting;
import ru.nanolive.draconicplus.common.fusioncrafting.client.sound.FusionRotationSound;
import ru.nanolive.draconicplus.common.fusioncrafting.network.PacketTileMessage;
import ru.nanolive.draconicplus.common.fusioncrafting.network.SyncableBool;
import ru.nanolive.draconicplus.common.fusioncrafting.network.SyncableShort;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class TileFusionCraftingCore extends TileInventoryBase implements IFusionCraftingInventory, ISidedInventory {

    public List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
    public final SyncableBool isCrafting = new SyncableBool(false, true, false, true);
    /**
     * 0 = Not crafting<br>
     * 1 -> 1000 = Charge percentage<br>
     * 1000 -> 2000 = Crafting progress
     */
    public final SyncableShort craftingStage = new SyncableShort((short) 0, true, false, false);
    public IFusionRecipe activeRecipe = null;

    @SideOnly(Side.CLIENT)
    public LinkedList<EffectTrackerFusionCrafting> effects;

    public TileFusionCraftingCore() {
        setInventorySize(2);
        registerSyncableObject(isCrafting, false);
        registerSyncableObject(craftingStage, false);
        this.shouldRefreshOnState = false;
    }

    //region Logic
    
    private boolean updateBlock;
    
    public int ticker = 0;
    
    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            detectAndSendChanges();
        }
        //LogHelper.info("- " + isCrafting);

        if (worldObj.isRemote) {
            updateEffects();
        }

        //Update Crafting
        if (isCrafting.value && !worldObj.isRemote) {

            for (ICraftingInjector pedestal : pedestals) {
                if (((TileEntity) pedestal).isInvalid()) {
                    invalidateCrafting();
                    return;
                }
            }

            if (activeRecipe == null || !activeRecipe.matches(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord)) || activeRecipe.canCraft(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord)) == null || !activeRecipe.canCraft(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord)).equals("true")) {
                invalidateCrafting();
                return;
            }

            long totalCharge = 0;

            for (ICraftingInjector pedestal : pedestals) {
                if (pedestal.getStackInPedestal() == null) {
                    continue;
                }
                totalCharge += pedestal.getCharge();
            }

            int averageCharge = (int) (totalCharge / activeRecipe.getRecipeIngredients().size());
            double percentage = averageCharge / (double) activeRecipe.getEnergyCost();

            if (percentage <= 1D && craftingStage.value < 1000) {
                craftingStage.value = (short) (percentage * 1000D);
                if (craftingStage.value == 0 && percentage > 0) {
                    craftingStage.value = 1;
                }
            }
            else if (craftingStage.value < 2000) {
                craftingStage.value += 2;
            }
            else if (craftingStage.value >= 2000) {
                activeRecipe.craft(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord));

                for (ICraftingInjector pedestal : pedestals) {
                    pedestal.onCraft();
                }
                //Reset tile... Oops
                isCrafting.value = false;
                updateBlock = true;
            }
        }
        else if (!worldObj.isRemote && !isCrafting.value && craftingStage.value > 0) {
            craftingStage.value = 0;
        }
        if(updateBlock) {
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            updateBlock = false;
        }
        
    	if(ticker++ > 60) {
    		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    		ticker = 0;
    	}
    }

    public void attemptStartCrafting() {
        updateInjectors(worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord));
        activeRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord));

        if (activeRecipe != null && activeRecipe.canCraft(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord)) != null && activeRecipe.canCraft(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord)).equals("true")) {
            isCrafting.value = true;
        } else {
            activeRecipe = null;
        }
    }

    private void invalidateCrafting() {
        isCrafting.value = false;
        activeRecipe = null;
        craftingStage.value = 0;
        pedestals.clear();
    }

    public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
        return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }
    
    /**
     * Clears the injector list and then re acquires all valid pedestals.
     */
    public static void updateInjectors(World world, BlockPos pos) {
    	if(!(world.getTileEntity(pos.x, pos.y, pos.z) instanceof TileFusionCraftingCore))
    		return;
    	TileFusionCraftingCore tileF = (TileFusionCraftingCore) world.getTileEntity(pos.x, pos.y, pos.z);
    	
    	
        if (tileF.isCrafting.value) {
            return;
        }

        tileF.pedestals.clear();
        int range = 16;

        List<BlockPos> positions = new ArrayList<BlockPos>();
        //X
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-range, -1, -1), pos.add(range, 1, 1))));
        //Y
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -range, -1), pos.add(1, range, 1))));
        //Z
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(pos.add(-1, -1, -range), pos.add(1, 1, range))));

        for (BlockPos checkPos : positions) {
            TileEntity tile = world.getTileEntity(checkPos.x, checkPos.y, checkPos.z);
            
            if (tile instanceof ICraftingInjector) {
                ICraftingInjector pedestal = (ICraftingInjector) tile;
                Vec3D dirVec = new Vec3D(tile.xCoord, tile.yCoord, tile.zCoord).subtract(pos);
                double dist = getDistanceAtoB(new Vec3D(tile.xCoord, tile.yCoord, tile.zCoord), new Vec3D(pos.x, pos.y, pos.z));

                if (dist >= 2 && pedestal.setCraftingInventory(tileF)) {
                	tileF.pedestals.add(pedestal);
                }
            }
        }
        
    }

    @Override
    public boolean craftingInProgress() {
        return isCrafting.value;
    }

    @Override
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP player) {
    	if(!worldObj.isRemote)
    	attemptStartCrafting();
    }

    @Override
    public int getRequiredCharge() {
        if (activeRecipe == null) {
            return 0;
        } else {
            return activeRecipe.getEnergyCost();
        }
    }

    @Override
    public int getCraftingStage() {
        return craftingStage.value;
    }

    //endregion

    //region Inventory

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        updateBlock();
    }

    @Override
    public ItemStack getStackInCore(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public void setStackInCore(int slot, ItemStack stack) {
        setInventorySlotContents(slot, stack);
    }

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{0, 1};
	}

	@Override
	public List<ICraftingInjector> getInjectors() {
		return pedestals;
	}
    
    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, int direction) {
        return index == 0;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int direction) {
        return index == 1;
    }

	 @Override
	 public void readFromNBT(NBTTagCompound nbttagcompound) {
	        super.readFromNBT(nbttagcompound);
				        
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
    
    //endregion

    //region Effects

    @SideOnly(Side.CLIENT)
    public void initializeEffects() {
        pedestals.clear();
        int range = 16;

        List<BlockPos> positions = new ArrayList<BlockPos>();
        //X
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(-range, -1, -1), new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(range, 1, 1))));
        //Y
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(-1, -range, -1), new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(1, range, 1))));
        //Z
        positions.addAll(Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(-1, -1, -range), new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(1, 1, range))));

        for (BlockPos checkPos : positions) {
            TileEntity tile = worldObj.getTileEntity(checkPos.x, checkPos.y, checkPos.z);

            if (tile instanceof ICraftingInjector) {
                ICraftingInjector pedestal = (ICraftingInjector) tile;
                Vec3D dirVec = new Vec3D(tile.xCoord, tile.yCoord, tile.zCoord).subtract(new BlockPos(this.xCoord, this.yCoord, this.zCoord));
                double dist = getDistanceAtoB(new Vec3D(tile.xCoord, tile.yCoord, tile.zCoord), new Vec3D(this.xCoord, this.yCoord, this.zCoord));

                if (dist >= 2 /*&& EnumFacing.getFacingFromVector((int) dirVec.x, (int) dirVec.y, (int) dirVec.z) == pedestal.getDirection().getOpposite()*/ && pedestal.setCraftingInventory(this)) {
                    pedestals.add(pedestal);
                }
            }
        }

        activeRecipe = RecipeManager.FUSION_REGISTRY.findRecipe(this, worldObj, new BlockPos(this.xCoord, this.yCoord, this.zCoord));

        if (activeRecipe == null) {
            effects = null;
            return;
        }

        effects = new LinkedList<EffectTrackerFusionCrafting>();

        for (ICraftingInjector pedestal : pedestals) {
            if (pedestal.getStackInPedestal() == null) {
                continue;
            }

            pedestal.setCraftingInventory(this);
            Vec3D spawn = new Vec3D(((TileEntity) pedestal).xCoord, ((TileEntity) (pedestal)).yCoord, ((TileEntity) pedestal).zCoord);
            spawn.add(0.5 + pedestal.getDirection().getFrontOffsetX() * 0.45, 0.5 + pedestal.getDirection().getFrontOffsetY() * 0.45, 0.5 + pedestal.getDirection().getFrontOffsetZ() * 0.45);
            effects.add(new EffectTrackerFusionCrafting(worldObj, spawn, new Vec3D(this.xCoord, this.yCoord, this.zCoord), this));
        }
        
        for(int i = 0; i < 4; i++) {
        	switch(i) {
        	case(0): {
                DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(worldObj, new Vec3D(this.xCoord+1.5, this.yCoord, this.zCoord+0.5), new Vec3D(this.xCoord+1.5, this.yCoord, this.zCoord+0.5), this));
                break;
        	}
        	case(1): {
                DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(worldObj, new Vec3D(this.xCoord+0.5, this.yCoord, this.zCoord+1.5), new Vec3D(this.xCoord+0.5, this.yCoord, this.zCoord+1.5), this));
        		break;
        	}
        	case(2): {
                DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(worldObj, new Vec3D(this.xCoord-0.5, this.yCoord, this.zCoord+0.5), new Vec3D(this.xCoord-0.5, this.yCoord, this.zCoord+0.5), this));
        		break;
        	}
        	case(3): {
                DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/blocks/fusion_crafting/fusion_particle.png"), new ParticleFusionCrafting(worldObj, new Vec3D(this.xCoord+0.5, this.yCoord, this.zCoord-0.5), new Vec3D(this.xCoord+0.5, this.yCoord, this.zCoord-0.5), this));
        		break;
        	}
        	}
        }
    }

    private double effectRotation = 0;
    private boolean allLocked = false;
    private boolean halfCycle = false;

    @SideOnly(Side.CLIENT)
    public void updateEffects() {
        if (effects == null) {
            if (isCrafting.value) {
                initializeEffects();
                effectRotation = 0;
                allLocked = false;
            }
            return;
        }

//        craftingStage.value = 1500;


        //region Calculate Distance
        double distFromCore = 1.2;
        if (getCraftingStage() > 1600){
            distFromCore *= 1.0D - (getCraftingStage() - 1600) / 400D;
        }

        if (allLocked){
            effectRotation -= Math.min(((getCraftingStage() - 1100D) / 900D) * 0.8D, 0.5D);
            if (effectRotation > 0){
                effectRotation = 0;
            }

        }

        int index = 0;
        int count = effects.size();
        boolean flag = true;
        boolean isMoving = getCraftingStage() > 1000;
        for (EffectTrackerFusionCrafting effect : effects) {
            effect.onUpdate(isMoving);
            if (!effect.positionLocked){
                flag = false;
            }

            if (isMoving) {
                effect.scale = 0.7F + ((float) (distFromCore / 1.2D) * 0.3F);
                effect.green = effect.blue = (float) (distFromCore - 0.2);
                effect.red = 1F - (float) (distFromCore - 0.2);
            }

            double indexPos = (double)index / (double)count;
            double offset = indexPos * (Math.PI * 2);
            double offsetX = Math.sin(effectRotation + offset) * distFromCore;
            double offsetZ = Math.cos(effectRotation + offset) * distFromCore;

            double mix = effectRotation / 5F;
            double xAdditive = offsetX * Math.sin(-mix);
            double zAdditive = offsetZ * Math.cos(-mix);

            double offsetY = (xAdditive + zAdditive) * 0.2 * (distFromCore / 1.2);

            effect.circlePosition.set(this.xCoord + 0.5 + offsetX, this.yCoord + 0.5 + offsetY, this.zCoord + 0.5 + offsetZ);
            index++;
        }

        //LogHelper.info(Math.sin(effectRotation));
//        double rotationPos = Math.sin(effectRotation * 2);
//        float pitch = 0.1F + (((getCraftingStage() - 1000) / 1000F) * 1.9F);
//
//        if (rotationPos > 0 && !halfCycle){
//            halfCycle = true;
//            worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESoundHandler.fusionRotation, SoundCategory.BLOCKS, 1F, pitch, false);
//        }
//        else if (rotationPos < 0 && halfCycle) {
//            halfCycle = false;
//            worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, DESoundHandler.fusionRotation, SoundCategory.BLOCKS, 1F, pitch, false);
//        }

        if (!allLocked && flag){
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(new FusionRotationSound(this));
        }

        allLocked = flag;

        if (!isCrafting.value){
            for (int i = 0; i < 100 ; i++) {
            	DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, new Vec3D(this.xCoord, this.yCoord, this.zCoord).add(0.5, 0.5, 0.5)));
            }

            worldObj.playSound(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, MoreInfo.MODID + ":" + "fusion_complete", 2F, 1F, false);
            effects = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderEffects(float partialTicks) {
  //     craftingStage.value = 1500;
        if (effects != null) {
            ResourceHelperDP.bindTexture("textures/blocks/fusion_crafting/fusion_particle.png");
            Tessellator tessellator = Tessellator.instance;

            //Pre-Render
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);

            for (EffectTrackerFusionCrafting effect : effects) {
                effect.renderEffect(tessellator, partialTicks);
            }

            //Post-Render
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
    	BlockPos bp1 = new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(-16, -16, -16);
    	BlockPos bp2 = new BlockPos(this.xCoord, this.yCoord, this.zCoord).add(17, 17, 17);
        return AxisAlignedBB.getBoundingBox(bp1.x, bp1.y, bp1.z, bp2.x, bp2.y, bp2.z);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

}