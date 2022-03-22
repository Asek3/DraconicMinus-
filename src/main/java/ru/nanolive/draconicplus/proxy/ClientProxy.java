package ru.nanolive.draconicplus.proxy;

import org.lwjgl.opengl.GL11;

import codechicken.nei.api.API;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.DPEffectHandler;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.DPEffectRenderer;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile.RenderItemCraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile.RenderItemFusionCraftingCore;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile.RenderTileCraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile.RenderTileFusionCraftingCore;
import ru.nanolive.draconicplus.common.fusioncrafting.nei.FusionCraftingNEI;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileCraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

public class ClientProxy extends CommonProxy {
	
	private final Minecraft mc = Minecraft.getMinecraft();
	
	public static int[] displayList = new int[1];
	public static int[] displayInjectorList = new int[1];
		
	@Override
    public void Init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new DPEffectHandler());
        FMLCommonHandler.instance().bus().register(new DPEffectHandler());
        DPEffectHandler.effectRenderer = new DPEffectRenderer(Minecraft.getMinecraft().theWorld);
        
        registerRendering();
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        
    	if(event.getSide().isClient()) {
	    	if(Loader.isModLoaded("NotEnoughItems")) {
	    		FusionCraftingNEI nei = new FusionCraftingNEI();
	    		API.registerRecipeHandler(nei);
	    		API.registerUsageHandler(nei);
	    		}
    	}
        
    }
	
    @Override
    public void registerRendering() {        
        //Tile Entities
		final IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(MoreInfo.MODID, "models/blocks/fusion_crafting_core.obj"));
		
		displayList[0] = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayList[0], GL11.GL_COMPILE);
		model.renderAll();
		GL11.glEndList();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileFusionCraftingCore.class, new RenderTileFusionCraftingCore());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(DraconicBlocks.fusionCraftingCore), new RenderItemFusionCraftingCore());
		
		final IModelCustom modelInjector = AdvancedModelLoader.loadModel(new ResourceLocation(MoreInfo.MODID, "models/blocks/crafting_injector.obj"));
		
		displayInjectorList[0] = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayInjectorList[0], GL11.GL_COMPILE);
		modelInjector.renderAll();
		GL11.glEndList();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingInjector.class, new RenderTileCraftingInjector());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(DraconicBlocks.craftingInjector), new RenderItemCraftingInjector());
		
    }
    
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.side.isClient() ? (EntityPlayer)this.mc.thePlayer : super.getPlayerEntity(ctx);
    }
        
    	
}
