package ru.nanolive.draconicplus.common.fusioncrafting.client.gui;


import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.DraconicPlus;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.container.ContainerFusionCraftingCore;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

public class DPGuiHandler implements IGuiHandler {

	public static final DPGuiHandler instance = new DPGuiHandler();

    public static final int GUIID_FUSION_CRAFTING = 0;

    public static void initialize(){
		NetworkRegistry.INSTANCE.registerGuiHandler(DraconicPlus.instance, instance);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch (ID) {
            case GUIID_FUSION_CRAFTING:
                if (tileEntity != null && tileEntity instanceof TileFusionCraftingCore) {
                    return new ContainerFusionCraftingCore(player, (TileFusionCraftingCore) tileEntity);
                }
                break;
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch (ID) {
            case GUIID_FUSION_CRAFTING:
                if (tileEntity != null && tileEntity instanceof TileFusionCraftingCore) {
                    return new GuiFusionCraftingCore(player, (TileFusionCraftingCore) tileEntity);
                }
                break;
		}

		return null;
	}

}