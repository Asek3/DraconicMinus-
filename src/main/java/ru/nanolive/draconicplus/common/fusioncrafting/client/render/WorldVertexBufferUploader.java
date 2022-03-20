package ru.nanolive.draconicplus.common.fusioncrafting.client.render;

import java.nio.ByteBuffer;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormat;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement.EnumUsage;

@SideOnly(Side.CLIENT)
public class WorldVertexBufferUploader {
    public WorldVertexBufferUploader() {
    }

    public void draw(VertexBuffer p_draw_1_) {
        if (p_draw_1_.getVertexCount() > 0) {
            VertexFormat vertexformat = p_draw_1_.getVertexFormat();
            int i = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = p_draw_1_.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();

            int i1;
            int k1;
            for(i1 = 0; i1 < list.size(); ++i1) {
                VertexFormatElement vertexformatelement = (VertexFormatElement)list.get(i1);
                EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                int k = vertexformatelement.getType().getGlConstant();
                k1 = vertexformatelement.getIndex();
                bytebuffer.position(vertexformat.getOffset(i1));
                vertexformatelement.getUsage().preDraw(vertexformat, i1, i, bytebuffer);
            }

            GlStateManager.glDrawArrays(p_draw_1_.getDrawMode(), 0, p_draw_1_.getVertexCount());
            i1 = 0;

            for(int j1 = list.size(); i1 < j1; ++i1) {
                VertexFormatElement vertexformatelement1 = (VertexFormatElement)list.get(i1);
                EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
                k1 = vertexformatelement1.getIndex();
                vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
            }
        }

        p_draw_1_.reset();
    }
}