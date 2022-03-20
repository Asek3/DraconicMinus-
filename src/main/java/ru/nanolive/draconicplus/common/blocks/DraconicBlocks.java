package ru.nanolive.draconicplus.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemBlock;
import ru.nanolive.draconicplus.common.fusioncrafting.blocks.CraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.blocks.FusionCraftingCore;

public class DraconicBlocks {
	
	public static BlockDP fusionCraftingCore;
	
	public static BlockDP craftingInjector;
	
    public static void initBlocks() {
    	fusionCraftingCore = new FusionCraftingCore();
    	craftingInjector = new CraftingInjector();
    }
    
    public static void register(BlockDP block) {
        String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
        GameRegistry.registerBlock(block, name.substring(name.indexOf(":") + 1));
    }
    
    public static void register(BlockDP block, Class<? extends ItemBlock> item) {
        String name = block.getUnwrappedUnlocalizedName(block.getUnlocalizedName());
        GameRegistry.registerBlock(block, item, name.substring(name.indexOf(":") + 1));
    }
    

    
}
