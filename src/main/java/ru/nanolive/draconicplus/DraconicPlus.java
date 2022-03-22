package ru.nanolive.draconicplus;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import minetweaker.MineTweakerAPI;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;
import ru.nanolive.draconicplus.common.fusioncrafting.client.gui.DPGuiHandler;
import ru.nanolive.draconicplus.common.fusioncrafting.minetweaker.FusionCrafting;
import ru.nanolive.draconicplus.common.fusioncrafting.nei.FusionCraftingNEI;
import ru.nanolive.draconicplus.common.handlers.RecipeHandler;
import ru.nanolive.draconicplus.network.PacketDispatcher;
import ru.nanolive.draconicplus.proxy.CommonProxy;

@Mod(modid = MoreInfo.MODID, version = MoreInfo.VERSION, name = MoreInfo.NAME, dependencies = "required-after:DraconicEvolution;after:Baubles;after:IC2;after:Thaumcraft;after:Botania;")
public class DraconicPlus
{
    
	@Mod.Instance(MoreInfo.MODID)
	public static DraconicPlus instance;
	
	@SidedProxy(clientSide = MoreInfo.CLIENTPROXY, serverSide = MoreInfo.SERVERPROXY)
	public static CommonProxy proxy;
	
	public static final CreativeTabs draconicTab = new CreativeTabs("draconicplus") {
		
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(DraconicBlocks.fusionCraftingCore);
		}

		@Override
		public Item getTabIconItem() {
			return getIconItemStack().getItem();
		}
		
	};
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	PacketDispatcher.preInit();
		proxy.preInit(event);
		
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.Init(event);
    	DPGuiHandler.initialize();
		RecipeHandler.init();;
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);
    	if(Loader.isModLoaded("MineTweaker3"))
    		MineTweakerAPI.registerClass(FusionCrafting.class);
    }
    
}
