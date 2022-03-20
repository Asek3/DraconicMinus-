package ru.nanolive.draconicplus.common.handlers;

import static ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager.addFusion;
import static ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager.RecipeDifficulty.NORMAL;

import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.handler.ConfigHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ru.nanolive.draconicplus.common.blocks.DraconicBlocks;

public class RecipeHandler {
	
	private ItemStack output;
	
	public static void init() {
		addOre(new ItemStack(DraconicBlocks.fusionCraftingCore), new Object [] {"LDL", "DCD", "LDL", 'L', "blockLapis", 'D', "gemDiamond", 'C', ModItems.draconicCore});
		addOre(new ItemStack(DraconicBlocks.craftingInjector, 1, 0), new Object [] {"DCD", "SIS", "SSS", 'I', "blockIron", 'D', "gemDiamond", 'C', ModItems.draconicCore, 'S', "stone"});
		addFusion(NORMAL, new ItemStack(DraconicBlocks.craftingInjector, 1, 1), new ItemStack(DraconicBlocks.craftingInjector, 1, 0), 256000, 0, ModItems.wyvernCore, ModItems.draconicCore, ModItems.draconicCore, "blockDraconium", "gemDiamond", "gemDiamond", "gemDiamond", "gemDiamond");
		addFusion(NORMAL, new ItemStack(DraconicBlocks.craftingInjector, 1, 2), new ItemStack(DraconicBlocks.craftingInjector, 1, 1), 1792000, 1, "gemDiamond", ModItems.wyvernCore, ModItems.wyvernCore, "gemDiamond", "gemDiamond", "blockDraconiumAwakened", "gemDiamond");
		addFusion(NORMAL, new ItemStack(DraconicBlocks.craftingInjector, 1, 3), new ItemStack(DraconicBlocks.craftingInjector, 1, 2), 48000000, 2, "gemDiamond", ModItems.chaoticCore, "gemDiamond", "gemDiamond", Blocks.dragon_egg, "gemDiamond");
	}
    
    private static void addOre(ItemStack result, Object... recipe) {
        if (result == null) return;
        for (Object o : recipe) {
            if (o == null) return;
            String s = o instanceof Item ? ((Item) o).getUnlocalizedName() : o instanceof Block ? ((Block) o).getUnlocalizedName() : null;
            if (s != null && ConfigHandler.disabledNamesList.contains(s)) return;
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
    }
    
}
