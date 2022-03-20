package ru.nanolive.draconicplus.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;
import ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileCraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		DraconicBlocks.initBlocks();
		
		registerTileEntities();
        RecipeManager.initialize();
	}
	
	public void Init(FMLInitializationEvent event) {
		
	}

	public void postInit(FMLPostInitializationEvent event) {}	
	
    public void registerTileEntities() {        
        GameRegistry.registerTileEntity(TileCraftingInjector.class, "TileCraftingInjector");
        GameRegistry.registerTileEntity(TileFusionCraftingCore.class, "TileFusionCraftingCore");
    }
	
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (EntityPlayer)(ctx.getServerHandler()).playerEntity;
	}

	public void registerRendering() {}
	
}
