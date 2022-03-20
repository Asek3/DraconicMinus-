package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import ru.nanolive.draconicplus.proxy.ClientProxy;

public class RenderItemFusionCraftingCore implements IItemRenderer {

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
		Minecraft.getMinecraft().renderEngine.bindTexture(RenderTileFusionCraftingCore.texture);
		GL11.glCallList(ClientProxy.displayList[0]);
		GL11.glPopMatrix();
	}
}
