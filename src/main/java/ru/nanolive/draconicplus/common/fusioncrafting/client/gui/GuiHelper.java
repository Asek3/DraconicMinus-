package ru.nanolive.draconicplus.common.fusioncrafting.client.gui;

import com.brandon3055.brandonscore.common.utills.InfoHelper;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;
import ru.nanolive.draconicplus.common.fusioncrafting.utils.Utils;

public class GuiHelper {
  public static final double PXL128 = 0.0078125D;
  
  public static final double PXL256 = 0.00390625D;
  
  public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x + xSize && mouseY >= y && mouseY <= y + ySize);
  }
  
  public static void drawTexturedRect(int x, int y, int u, int v, int width, int height) {
    drawTexturedRect(x, y, width, height, u, v, width, height, 0.0D, 0.00390625D);
  }
  
  public static void drawTexturedRect(double x, double y, double width, double height, int u, int v, int uSize, int vSize, double zLevel, double pxl) {
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(x, y + height, zLevel, u * pxl, (v + vSize) * pxl);
    tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + uSize) * pxl, (v + vSize) * pxl);
    tessellator.addVertexWithUV(x + width, y, zLevel, (u + uSize) * pxl, v * pxl);
    tessellator.addVertexWithUV(x, y, zLevel, u * pxl, v * pxl);
    tessellator.draw();
  }
  
  public static void drawHoveringText(List list, int x, int y, FontRenderer font, int guiWidth, int guiHeight) {
    GuiScreen guiScreen = new GuiScreen();
    guiScreen.func_146283_a(list, x, y);
  }
  
  public static void drawHoveringTextScaled(List<String> list, int mouseX, int mouseY, FontRenderer font, float fade, double scale, int guiWidth, int guiHeight) {
    if (!list.isEmpty()) {
      GL11.glPushMatrix();
      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH);
      GL11.glScaled(scale, scale, 1.0D);
      mouseX = (int)(mouseX / scale);
      mouseY = (int)(mouseY / scale);
      int tooltipTextWidth = 0;
      for (Object aList : list) {
        String s = (String)aList;
        int l = font.getStringWidth(s);
        if (l > tooltipTextWidth)
          tooltipTextWidth = l; 
      } 
      int tooltipX = mouseX + 12;
      int tooltipY = mouseY - 12;
      int tooltipHeight = 6;
      if (list.size() > 1)
        tooltipHeight += 2 + (list.size() - 1) * 10; 
      if (tooltipX + tooltipTextWidth > (int)(guiWidth / scale))
        tooltipX -= 28 + tooltipTextWidth; 
      if (tooltipY + tooltipHeight + 6 > (int)(guiHeight / scale))
        tooltipY = (int)(guiHeight / scale) - tooltipHeight - 6; 
      int backgroundColor = -267386864;
      drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor, fade, scale);
      drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor, fade, scale);
      drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale);
      drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale);
      drawGradientRect(tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale);
      int k1 = 1347420415;
      int l1 = (k1 & 0xFEFEFE) >> 1 | k1 & 0xFF000000;
      drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, k1, l1, fade, scale);
      drawGradientRect(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, k1, l1, fade, scale);
      drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, k1, k1, fade, scale);
      drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, l1, l1, fade, scale);
      int i2 = 0;
      while (i2 < list.size()) {
        String s1 = list.get(i2);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        font.drawStringWithShadow(s1, tooltipX, tooltipY, (int)(fade * 240.0F) + 16 << 24 | 0xFFFFFF);
        GL11.glEnable(GL11.GL_ALPHA);
        tooltipY += 10;
        i2++;
      }
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH);
      RenderHelper.enableStandardItemLighting();
      GL11.glEnable(GL12.GL_RESCALE_NORMAL);
      GL11.glPopMatrix();
    } 
  }
  
  public static void drawGradientRect(int left, int top, int right, int bottom, int colour1, int colour2, float fade, double zLevel) {
    float f = (colour1 >> 24 & 0xFF) / 255.0F * fade;
    float f1 = (colour1 >> 16 & 0xFF) / 255.0F;
    float f2 = (colour1 >> 8 & 0xFF) / 255.0F;
    float f3 = (colour1 & 0xFF) / 255.0F;
    float f4 = (colour2 >> 24 & 0xFF) / 255.0F * fade;
    float f5 = (colour2 >> 16 & 0xFF) / 255.0F;
    float f6 = (colour2 >> 8 & 0xFF) / 255.0F;
    float f7 = (colour2 & 0xFF) / 255.0F;
    //GL11.glDisable(GL11.GL_TEXTURE_2D);
    //GL11.glEnable(GL11.GL_BLEND);
    //GL11.glDisable(GL11.GL_ALPHA);
    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    GL11.glShadeModel(GL11.GL_SMOOTH);
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.setColorRGBA_F(f1, f2, f3, f);
    tessellator.addVertex(right, top, zLevel);
    tessellator.addVertex(left, top, zLevel);
    tessellator.setColorRGBA_F(f5, f6, f7, f4);
    tessellator.addVertex(left, bottom, zLevel);
    tessellator.addVertex(right, bottom, zLevel);
    tessellator.draw();
    GL11.glShadeModel(GL11.GL_FLAT);
    //GL11.glDisable(GL11.GL_BLEND);
    //GL11.glEnable(GL11.GL_ALPHA);
    //GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  
  public static void drawEnergyBar(Gui gui, int posX, int posZ, int size, long energy, long maxEnergy) {
    drawEnergyBar(gui, posX, posZ, size, false, energy, maxEnergy, false, 0, 0);
  }
  
  public static void drawEnergyBar(Gui gui, int posX, int posY, int size, boolean horizontal, long energy, long maxEnergy, boolean toolTip, int mouseX, int mouseY) {
    ResourceHelperDP.bindTexture("textures/gui/energy_gui.png");
    int draw = (int)(energy / maxEnergy * (size - 2));
    boolean inRect = isInRect(posX, posY, size, 14, mouseX, mouseY);
    if (horizontal) {
      int x = posY;
      posY = posX;
      posX = x;
      GL11.glPushMatrix();
      GL11.glTranslatef((size + posY * 2), 0.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
    } 
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    gui.drawTexturedModalRect(posX, posY, 0, 0, 14, size);
    gui.drawTexturedModalRect(posX, posY + size - 1, 0, 255, 14, 1);
    gui.drawTexturedModalRect(posX + 1, posY + size - draw - 1, 14, size - draw, 12, draw);
    if (horizontal)
    	GL11.glPopMatrix(); 
    if (toolTip && inRect) {
      List<String> list = new ArrayList<>();
      list.add(InfoHelper.ITC() + StatCollector.translateToLocal("gui.de.energyStorage.txt"));
      list.add(InfoHelper.HITC() + Utils.formatNumber(energy) + " / " + Utils.formatNumber(maxEnergy));
      list.add(EnumChatFormatting.GRAY + "[" + Utils.addCommas(energy) + " RF]");
      drawHoveringText(list, mouseX, mouseY, (Minecraft.getMinecraft()).fontRenderer, (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    } 
  }
  
  public static void drawGuiBaseBackground(Gui gui, int posX, int posY, int xSize, int ySize) {
    ResourceHelperDP.bindTexture("textures/gui/base_gui.png");
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    gui.drawTexturedModalRect(posX, posY, 0, 0, xSize - 3, ySize - 3);
    gui.drawTexturedModalRect(posX + xSize - 3, posY, 253, 0, 3, ySize - 3);
    gui.drawTexturedModalRect(posX, posY + ySize - 3, 0, 253, xSize - 3, 3);
    gui.drawTexturedModalRect(posX + xSize - 3, posY + ySize - 3, 253, 253, 3, 3);
  }
  
  public static void drawPlayerSlots(Gui gui, int posX, int posY, boolean center) {
    ResourceHelperDP.bindTexture("textures/gui/bc_widgets.png");
    if (center)
      posX -= 81; 
    for (int y = 0; y < 3; y++) {
      for (int i = 0; i < 9; i++)
        gui.drawTexturedModalRect(posX + i * 18, posY + y * 18, 138, 0, 18, 18); 
    } 
    for (int x = 0; x < 9; x++)
      gui.drawTexturedModalRect(posX + x * 18, posY + 58, 138, 0, 18, 18); 
  }
  
  public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color, boolean dropShadow) {
    fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color, dropShadow);
  }
  
  public static void drawCenteredSplitString(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, int color, boolean dropShadow) {
    List<String> list = fontRenderer.listFormattedStringToWidth(str, wrapWidth);
    for (String s : list) {
      drawCenteredString(fontRenderer, s, x, y, color, dropShadow);
      y += fontRenderer.FONT_HEIGHT;
    } 
  }
  
  public static void drawStack2D(ItemStack stack, Minecraft mc, int x, int y, float scale) {
    if (stack == null || stack.getItem() == null)
      return; 
    RenderHelper.enableGUIStandardItemLighting();
    GL11.glTranslatef(0.0F, 0.0F, 32.0F);
    (RenderItem.getInstance()).zLevel = 200.0F;
    FontRenderer font = mc.fontRenderer;
    RenderItem.getInstance().renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, x, y);
    String count = (stack.stackSize > 1) ? String.valueOf(stack.stackSize) : "";
    RenderItem.getInstance().renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, x, y, count);
    (RenderItem.getInstance()).zLevel = 0.0F;
  }
  
  public static void drawStack(ItemStack stack, Minecraft mc, int x, int y, float scale) {
    if (stack == null)
      return; 
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, 300.0F);
    GL11.glScalef(scale, scale, scale);
    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
    GL11.glPopMatrix();
  }
  
  public static void drawGradientRect(int posX, int posY, int xSize, int ySize, int colour, int colour2) {
    drawGradientRect(posX, posY, posX + xSize, posY + ySize, colour, colour2, 1.0F, 0.0D);
  }
  
  public static void drawColouredRect(int posX, int posY, int xSize, int ySize, int colour) {
    drawGradientRect(posX, posY, posX + xSize, posY + ySize, colour, colour, 1.0F, 0.0D);
  }
  
  public static void drawBorderedRect(int posX, int posY, int xSize, int ySize, int borderWidth, int fillColour, int borderColour) {
    drawColouredRect(posX, posY, xSize, borderWidth, borderColour);
    drawColouredRect(posX, posY + ySize - borderWidth, xSize, borderWidth, borderColour);
    drawColouredRect(posX, posY + borderWidth, borderWidth, ySize - 2 * borderWidth, borderColour);
    drawColouredRect(posX + xSize - borderWidth, posY + borderWidth, borderWidth, ySize - 2 * borderWidth, borderColour);
    drawColouredRect(posX + borderWidth, posY + borderWidth, xSize - 2 * borderWidth, ySize - 2 * borderWidth, fillColour);
  }
}
