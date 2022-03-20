package ru.nanolive.draconicplus.common.fusioncrafting.blocks;

import ru.nanolive.draconicplus.DraconicPlus;
import ru.nanolive.draconicplus.common.blocks.BlockDP;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileCraftingInjector;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by brandon3055 on 10/06/2016.
 */
public class CraftingInjector extends BlockDP implements ITileEntityProvider {

    public CraftingInjector(){
        super(Material.iron);
        
        this.setHardness(5F);
        this.setResistance(10F);
        this.setCreativeTab(DraconicPlus.draconicTab);
        this.setBlockName("craftingInjector");
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(soundTypeMetal);
        DraconicBlocks.register(this, CraftingInjectorItemBlock.class);
    }
    
	public int getRenderType() {
		return -1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
    }

    public static int determineOrientation(int x, int y, int z, EntityLivingBase entity)
    {
        if (MathHelper.abs((float)entity.posX - (float)x) < 2.0F && MathHelper.abs((float)entity.posZ - (float)z) < 2.0F)
        {
            double d0 = entity.posY + 1.82D - (double)entity.yOffset;

            if (d0 - (double)y > 2.0D)
            {
                return 1;
            }

            if ((double)y - d0 > 0.0D)
            {
                return 1;
            }
        }

        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
    
    @Override
    //TODO
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);

        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileCraftingInjector) {
            ((TileCraftingInjector) tile).facing.value = (byte) determineOrientation(x, y, z, placer);
            tile.markDirty();
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCraftingInjector();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(x, y, z);

        if (!(tile instanceof TileCraftingInjector)) {
            return false;
        }

        TileCraftingInjector craftingPedestal = (TileCraftingInjector)tile;

        if (craftingPedestal.getStackInSlot(0) != null){
            if (player.getHeldItem() == null){
                player.inventory.setInventorySlotContents(player.inventory.currentItem, craftingPedestal.getStackInSlot(0));
                craftingPedestal.setInventorySlotContents(0, null);
                world.markBlockForUpdate(x, y, z);
            }
            else {
                world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, craftingPedestal.getStackInSlot(0)));
                craftingPedestal.setInventorySlotContents(0, null);
                world.markBlockForUpdate(x, y, z);
            }

        }else {
            ItemStack stack = player.getHeldItem();
            craftingPedestal.setInventorySlotContents(0, stack);
            player.destroyCurrentEquippedItem();
            world.markBlockForUpdate(x, y, z);
        }

        return true;
    }
    
    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        EnumFacing facing;
        
        TileEntity tile = world.getTileEntity(x, y, z);

        if (!(tile instanceof TileCraftingInjector))
        	return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        
        facing = EnumFacing.getFront(((TileCraftingInjector) tile).facing.value);

        switch (facing){
            case DOWN:  return AxisAlignedBB.getBoundingBox(0.0625, 0.375, 0.0625, 0.9375, 1, 0.9375);
            case UP:    return AxisAlignedBB.getBoundingBox(0.0625, 0, 0.0625, 0.9375, 0.625, 0.9375);
            case NORTH: return AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.375, 0.9375, 0.9375, 1);
            case SOUTH: return AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.625);
            case WEST:  return AxisAlignedBB.getBoundingBox(0.375, 0.0625, 0.0625, 1, 0.9375, 0.9375);
            case EAST:  return AxisAlignedBB.getBoundingBox(0, 0.0625, 0.0625, 0.625, 0.9375, 0.9375);
        }
		return null;
    }

}