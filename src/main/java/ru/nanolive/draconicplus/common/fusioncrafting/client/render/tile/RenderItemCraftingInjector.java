package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import ru.nanolive.draconicplus.proxy.ClientProxy;

public class RenderItemCraftingInjector implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack is, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack is, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack is, Object... data) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0.55F, 0.45F, 0.55F);
		switch(is.getItem().getDamage(is)) {
		case (0): {
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderTileCraftingInjector.textureBasic);
			break;
			}
		case (1): {
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderTileCraftingInjector.textureWyvern);
			break;
			}
		case (2): {
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderTileCraftingInjector.textureDraconic);
			break;
			}
		case (3): {
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderTileCraftingInjector.textureChaotic);
			break;
			}
		}
		GL11.glCallList(ClientProxy.displayInjectorList[0]);
		GL11.glPopMatrix();
	}
}
