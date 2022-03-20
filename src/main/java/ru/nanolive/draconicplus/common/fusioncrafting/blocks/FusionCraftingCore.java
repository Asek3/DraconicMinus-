package ru.nanolive.draconicplus.common.fusioncrafting.blocks;

import com.brandon3055.draconicevolution.DraconicEvolution;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.DraconicPlus;
import ru.nanolive.draconicplus.common.blocks.BlockDP;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.client.gui.DPGuiHandler;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

import javax.annotation.Nullable;

/**
 * Created by brandon3055 on 11/06/2016.
 */
public class FusionCraftingCore extends BlockDP implements ITileEntityProvider {

    public FusionCraftingCore(){
        super(Material.iron);
        this.setHardness(5F);
        this.setResistance(10F);
        this.setCreativeTab(DraconicPlus.draconicTab);
        this.setBlockName("fusionCraftingCore");
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(soundTypeMetal);
        DraconicBlocks.register(this);
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFusionCraftingCore();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileFusionCraftingCore){
            ((TileFusionCraftingCore) tile).updateInjectors(world, new BlockPos(x, y, z));
        }

        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, DraconicPlus.instance, DPGuiHandler.GUIID_FUSION_CRAFTING, world, x, y, z);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockIn) {
        if (!world.isRemote)
        {
            if (world.isBlockIndirectlyGettingPowered(x, y, z))
            {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileFusionCraftingCore){
                    ((TileFusionCraftingCore) tile).attemptStartCrafting();
                }
            }
        }
    }
}