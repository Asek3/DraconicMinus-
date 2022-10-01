package ru.nanolive.draconicplus.common.fusioncrafting.client.render;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.renderer.OpenGlHelper;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormat;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement.EnumUsage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;

public class ForgeHooksClient {
    public static void preDraw(EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
        VertexFormatElement attr = format.getElement(element);
        int count = attr.getElementCount();
        int constant = attr.getType().getGlConstant();
        buffer.position(format.getOffset(element));
        switch(attrType) {
            case POSITION:
                GL11.glVertexPointer(count, constant, stride, buffer);
                GL11.glEnableClientState(32884);
                break;
            case NORMAL:
                if (count != 3) {
                    throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
                }

                GL11.glNormalPointer(constant, stride, buffer);
                GL11.glEnableClientState(32885);
                break;
            case COLOR:
                GL11.glColorPointer(count, constant, stride, buffer);
                GL11.glEnableClientState(32886);
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                GL11.glTexCoordPointer(count, constant, stride, buffer);
                GL11.glEnableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            case PADDING:
                break;
            case GENERIC:
                GL20.glEnableVertexAttribArray(attr.getIndex());
                GL20.glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, buffer);
            default:
                FMLLog.severe("Unimplemented vanilla attribute upload: %s", new Object[]{attrType.getDisplayName()});
        }

    }

    public static void postDraw(EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
        VertexFormatElement attr = format.getElement(element);
        switch(attrType) {
            case POSITION:
                GL11.glDisableClientState(32884);
                break;
            case NORMAL:
                GL11.glDisableClientState(32885);
                break;
            case COLOR:
                GL11.glDisableClientState(32886);
                GL11.glColor4f(-1, -1, -1, -1);
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                GL11.glDisableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            case PADDING:
                break;
            case GENERIC:
                GL20.glDisableVertexAttribArray(attr.getIndex());
            default:
                FMLLog.severe("Unimplemented vanilla attribute upload: %s", new Object[]{attrType.getDisplayName()});
        }

    }
}