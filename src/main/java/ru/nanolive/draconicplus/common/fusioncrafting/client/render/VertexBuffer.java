package ru.nanolive.draconicplus.common.fusioncrafting.client.render;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.util.MathHelper;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormat;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.VertexFormatElement.EnumUsage;
//import net.minecraft.util.math.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class VertexBuffer {
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer byteBuffer;
    private IntBuffer rawIntBuffer;
    private ShortBuffer rawShortBuffer;
    private FloatBuffer rawFloatBuffer;
    private int vertexCount;
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;
    private boolean noColor;
    private int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private VertexFormat vertexFormat;
    private boolean isDrawing;

    public VertexBuffer(int p_i46275_1_) {
        this.byteBuffer = ByteBuffer.allocateDirect(p_i46275_1_ * 4).order(ByteOrder.nativeOrder());
        //this.byteBuffer = GLAllocation.createDirectByteBuffer(p_i46275_1_ * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.rawShortBuffer = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
    }

    private void growBuffer(int p_growBuffer_1_) {
        int i = (this.vertexCount + 1) * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        if (p_growBuffer_1_ > this.rawIntBuffer.remaining() || i >= this.byteBuffer.capacity()) {
            int j = this.byteBuffer.capacity();
            int k = j % 2097152;
            int l = k + (((this.rawIntBuffer.position() + p_growBuffer_1_) * 4 - k) / 2097152 + 1) * 2097152;
            LOGGER.debug("Needed to grow BufferBuilder buffer: Old size " + j + " bytes, new size " + l + " bytes.");
            int i1 = this.rawIntBuffer.position();
            ByteBuffer bytebuffer = ByteBuffer.allocateDirect(1).order(ByteOrder.nativeOrder());
            //ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(l);
            this.byteBuffer.position(0);
            bytebuffer.put(this.byteBuffer);
            bytebuffer.rewind();
            this.byteBuffer = bytebuffer;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawIntBuffer.position(i1);
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawShortBuffer.position(i1 << 1);
        }

    }

    public void sortVertexData(float p_sortVertexData_1_, float p_sortVertexData_2_, float p_sortVertexData_3_) {
        int i = this.vertexCount / 4;
        final float[] afloat = new float[i];

        for(int j = 0; j < i; ++j) {
            afloat[j] = getDistanceSq(this.rawFloatBuffer, (float)((double)p_sortVertexData_1_ + this.xOffset), (float)((double)p_sortVertexData_2_ + this.yOffset), (float)((double)p_sortVertexData_3_ + this.zOffset), this.vertexFormat.getIntegerSize(), j * this.vertexFormat.getNextOffset());
        }

        Integer[] ainteger = new Integer[i];

        for(int k = 0; k < ainteger.length; ++k) {
            ainteger[k] = k;
        }

        Arrays.sort(ainteger, new Comparator<Integer>() {
            public int compare(Integer p_compare_1_, Integer p_compare_2_) {
                return Floats.compare(afloat[p_compare_2_], afloat[p_compare_1_]);
            }
        });
        BitSet bitset = new BitSet();
        int l = this.vertexFormat.getNextOffset();
        int[] aint = new int[l];

        for(int l1 = 0; (l1 = bitset.nextClearBit(l1)) < ainteger.length; ++l1) {
            int i1 = ainteger[l1];
            if (i1 != l1) {
                this.rawIntBuffer.limit(i1 * l + l);
                this.rawIntBuffer.position(i1 * l);
                this.rawIntBuffer.get(aint);
                int j1 = i1;

                for(int k1 = ainteger[i1]; j1 != l1; k1 = ainteger[k1]) {
                    this.rawIntBuffer.limit(k1 * l + l);
                    this.rawIntBuffer.position(k1 * l);
                    IntBuffer intbuffer = this.rawIntBuffer.slice();
                    this.rawIntBuffer.limit(j1 * l + l);
                    this.rawIntBuffer.position(j1 * l);
                    this.rawIntBuffer.put(intbuffer);
                    bitset.set(j1);
                    j1 = k1;
                }

                this.rawIntBuffer.limit(l1 * l + l);
                this.rawIntBuffer.position(l1 * l);
                this.rawIntBuffer.put(aint);
            }

            bitset.set(l1);
        }

        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(this.getBufferSize());
    }

    public VertexBuffer.State getVertexState() {
        this.rawIntBuffer.rewind();
        int i = this.getBufferSize();
        this.rawIntBuffer.limit(i);
        int[] aint = new int[i];
        this.rawIntBuffer.get(aint);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(i);
        return new VertexBuffer.State(aint, new VertexFormat(this.vertexFormat));
    }

    private int getBufferSize() {
        if (this.vertexFormat==null)
            return 0;
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    private static float getDistanceSq(FloatBuffer p_getDistanceSq_0_, float p_getDistanceSq_1_, float p_getDistanceSq_2_, float p_getDistanceSq_3_, int p_getDistanceSq_4_, int p_getDistanceSq_5_) {
        float f = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 0 + 0);
        float f1 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 0 + 1);
        float f2 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 0 + 2);
        float f3 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 1 + 0);
        float f4 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 1 + 1);
        float f5 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 1 + 2);
        float f6 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 2 + 0);
        float f7 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 2 + 1);
        float f8 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 2 + 2);
        float f9 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 3 + 0);
        float f10 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 3 + 1);
        float f11 = p_getDistanceSq_0_.get(p_getDistanceSq_5_ + p_getDistanceSq_4_ * 3 + 2);
        float f12 = (f + f3 + f6 + f9) * 0.25F - p_getDistanceSq_1_;
        float f13 = (f1 + f4 + f7 + f10) * 0.25F - p_getDistanceSq_2_;
        float f14 = (f2 + f5 + f8 + f11) * 0.25F - p_getDistanceSq_3_;
        return f12 * f12 + f13 * f13 + f14 * f14;
    }

    public void setVertexState(VertexBuffer.State p_setVertexState_1_) {
        this.rawIntBuffer.clear();
        this.growBuffer(p_setVertexState_1_.getRawBuffer().length);
        this.rawIntBuffer.put(p_setVertexState_1_.getRawBuffer());
        this.vertexCount = p_setVertexState_1_.getVertexCount();
        this.vertexFormat = new VertexFormat(p_setVertexState_1_.getVertexFormat());
    }

    public void reset() {
        this.vertexCount = 0;
        this.vertexFormatElement = null;
        this.vertexFormatIndex = 0;
    }

    public void begin(int p_begin_1_, VertexFormat p_begin_2_) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already building!");
        } else {
            this.isDrawing = true;
            this.reset();
            this.drawMode = p_begin_1_;
            this.vertexFormat = p_begin_2_;
            this.vertexFormatElement = p_begin_2_.getElement(this.vertexFormatIndex);
            this.noColor = false;
            this.byteBuffer.limit(this.byteBuffer.capacity());
        }
    }

    public VertexBuffer tex(double p_tex_1_, double p_tex_3_) {
        int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch(this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float)p_tex_1_);
                this.byteBuffer.putFloat(i + 4, (float)p_tex_3_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, (int)p_tex_1_);
                this.byteBuffer.putInt(i + 4, (int)p_tex_3_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short)((int)p_tex_3_));
                this.byteBuffer.putShort(i + 2, (short)((int)p_tex_1_));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte)((int)p_tex_3_));
                this.byteBuffer.put(i + 1, (byte)((int)p_tex_1_));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public VertexBuffer lightmap(int p_lightmap_1_, int p_lightmap_2_) {
        int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch(this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float)p_lightmap_1_);
                this.byteBuffer.putFloat(i + 4, (float)p_lightmap_2_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, p_lightmap_1_);
                this.byteBuffer.putInt(i + 4, p_lightmap_2_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short)p_lightmap_2_);
                this.byteBuffer.putShort(i + 2, (short)p_lightmap_1_);
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte)p_lightmap_2_);
                this.byteBuffer.put(i + 1, (byte)p_lightmap_1_);
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putBrightness4(int p_putBrightness4_1_, int p_putBrightness4_2_, int p_putBrightness4_3_, int p_putBrightness4_4_) {
        int i = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
        int j = this.vertexFormat.getNextOffset() >> 2;
        this.rawIntBuffer.put(i, p_putBrightness4_1_);
        this.rawIntBuffer.put(i + j, p_putBrightness4_2_);
        this.rawIntBuffer.put(i + j * 2, p_putBrightness4_3_);
        this.rawIntBuffer.put(i + j * 3, p_putBrightness4_4_);
    }

    public void putPosition(double p_putPosition_1_, double p_putPosition_3_, double p_putPosition_5_) {
        int i = this.vertexFormat.getIntegerSize();
        int j = (this.vertexCount - 4) * i;

        for(int k = 0; k < 4; ++k) {
            int l = j + k * i;
            int i1 = l + 1;
            int j1 = i1 + 1;
            this.rawIntBuffer.put(l, Float.floatToRawIntBits((float)(p_putPosition_1_ + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l))));
            this.rawIntBuffer.put(i1, Float.floatToRawIntBits((float)(p_putPosition_3_ + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i1))));
            this.rawIntBuffer.put(j1, Float.floatToRawIntBits((float)(p_putPosition_5_ + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(j1))));
        }

    }

    public int getColorIndex(int p_getColorIndex_1_) {
        return ((this.vertexCount - p_getColorIndex_1_) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float p_putColorMultiplier_1_, float p_putColorMultiplier_2_, float p_putColorMultiplier_3_, int p_putColorMultiplier_4_) {
        int i = this.getColorIndex(p_putColorMultiplier_4_);
        int j = -1;
        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);
            int k;
            int l;
            int i1;
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                k = (int)((float)(j & 255) * p_putColorMultiplier_1_);
                l = (int)((float)(j >> 8 & 255) * p_putColorMultiplier_2_);
                i1 = (int)((float)(j >> 16 & 255) * p_putColorMultiplier_3_);
                j &= -16777216;
                j = j | i1 << 16 | l << 8 | k;
            } else {
                k = (int)((float)(j >> 24 & 255) * p_putColorMultiplier_1_);
                l = (int)((float)(j >> 16 & 255) * p_putColorMultiplier_2_);
                i1 = (int)((float)(j >> 8 & 255) * p_putColorMultiplier_3_);
                j &= 255;
                j = j | k << 24 | l << 16 | i1 << 8;
            }
        }

        this.rawIntBuffer.put(i, j);
    }

    private void putColor(int p_putColor_1_, int p_putColor_2_) {
        int i = this.getColorIndex(p_putColor_2_);
        int j = p_putColor_1_ >> 16 & 255;
        int k = p_putColor_1_ >> 8 & 255;
        int l = p_putColor_1_ & 255;
        int i1 = p_putColor_1_ >> 24 & 255;
        this.putColorRGBA(i, j, k, l, i1);
    }

    public void putColorRGB_F(float p_putColorRGB_F_1_, float p_putColorRGB_F_2_, float p_putColorRGB_F_3_, int p_putColorRGB_F_4_) {
        int i = this.getColorIndex(p_putColorRGB_F_4_);
        int j = MathHelper.clamp_int((int)(p_putColorRGB_F_1_ * 255.0F), 0, 255);
        int k = MathHelper.clamp_int((int)(p_putColorRGB_F_2_ * 255.0F), 0, 255);
        int l = MathHelper.clamp_int((int)(p_putColorRGB_F_3_ * 255.0F), 0, 255);
        this.putColorRGBA(i, j, k, l, 255);
    }

    public void putColorRGBA(int p_putColorRGBA_1_, int p_putColorRGBA_2_, int p_putColorRGBA_3_, int p_putColorRGBA_4_, int p_putColorRGBA_5_) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.rawIntBuffer.put(p_putColorRGBA_1_, p_putColorRGBA_5_ << 24 | p_putColorRGBA_4_ << 16 | p_putColorRGBA_3_ << 8 | p_putColorRGBA_2_);
        } else {
            this.rawIntBuffer.put(p_putColorRGBA_1_, p_putColorRGBA_2_ << 24 | p_putColorRGBA_3_ << 16 | p_putColorRGBA_4_ << 8 | p_putColorRGBA_5_);
        }

    }

    public void noColor() {
        this.noColor = true;
    }

    public VertexBuffer color(float p_color_1_, float p_color_2_, float p_color_3_, float p_color_4_) {
        return this.color((int)(p_color_1_ * 255.0F), (int)(p_color_2_ * 255.0F), (int)(p_color_3_ * 255.0F), (int)(p_color_4_ * 255.0F));
    }

    public VertexBuffer color(int p_color_1_, int p_color_2_, int p_color_3_, int p_color_4_) {
        if (this.noColor) {
            return this;
        } else {
            int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
            switch(this.vertexFormatElement.getType()) {
                case FLOAT:
                    this.byteBuffer.putFloat(i, (float)p_color_1_ / 255.0F);
                    this.byteBuffer.putFloat(i + 4, (float)p_color_2_ / 255.0F);
                    this.byteBuffer.putFloat(i + 8, (float)p_color_3_ / 255.0F);
                    this.byteBuffer.putFloat(i + 12, (float)p_color_4_ / 255.0F);
                    break;
                case UINT:
                case INT:
                    this.byteBuffer.putFloat(i, (float)p_color_1_);
                    this.byteBuffer.putFloat(i + 4, (float)p_color_2_);
                    this.byteBuffer.putFloat(i + 8, (float)p_color_3_);
                    this.byteBuffer.putFloat(i + 12, (float)p_color_4_);
                    break;
                case USHORT:
                case SHORT:
                    this.byteBuffer.putShort(i, (short)p_color_1_);
                    this.byteBuffer.putShort(i + 2, (short)p_color_2_);
                    this.byteBuffer.putShort(i + 4, (short)p_color_3_);
                    this.byteBuffer.putShort(i + 6, (short)p_color_4_);
                    break;
                case UBYTE:
                case BYTE:
                    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                        this.byteBuffer.put(i, (byte)p_color_1_);
                        this.byteBuffer.put(i + 1, (byte)p_color_2_);
                        this.byteBuffer.put(i + 2, (byte)p_color_3_);
                        this.byteBuffer.put(i + 3, (byte)p_color_4_);
                    } else {
                        this.byteBuffer.put(i, (byte)p_color_4_);
                        this.byteBuffer.put(i + 1, (byte)p_color_3_);
                        this.byteBuffer.put(i + 2, (byte)p_color_2_);
                        this.byteBuffer.put(i + 3, (byte)p_color_1_);
                    }
            }

            this.nextVertexFormatIndex();
            return this;
        }
    }

    public void addVertexData(int[] p_addVertexData_1_) {
        this.growBuffer(p_addVertexData_1_.length);
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(p_addVertexData_1_);
        this.vertexCount += p_addVertexData_1_.length / this.vertexFormat.getIntegerSize();
    }

    public void endVertex() {
        ++this.vertexCount;
        this.growBuffer(this.vertexFormat.getIntegerSize());
    }

    public VertexBuffer pos(double p_pos_1_, double p_pos_3_, double p_pos_5_) {
        int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch(this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, (float)(p_pos_1_ + this.xOffset));
                this.byteBuffer.putFloat(i + 4, (float)(p_pos_3_ + this.yOffset));
                this.byteBuffer.putFloat(i + 8, (float)(p_pos_5_ + this.zOffset));
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, Float.floatToRawIntBits((float)(p_pos_1_ + this.xOffset)));
                this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float)(p_pos_3_ + this.yOffset)));
                this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float)(p_pos_5_ + this.zOffset)));
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short)((int)(p_pos_1_ + this.xOffset)));
                this.byteBuffer.putShort(i + 2, (short)((int)(p_pos_3_ + this.yOffset)));
                this.byteBuffer.putShort(i + 4, (short)((int)(p_pos_5_ + this.zOffset)));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte)((int)(p_pos_1_ + this.xOffset)));
                this.byteBuffer.put(i + 1, (byte)((int)(p_pos_3_ + this.yOffset)));
                this.byteBuffer.put(i + 2, (byte)((int)(p_pos_5_ + this.zOffset)));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putNormal(float p_putNormal_1_, float p_putNormal_2_, float p_putNormal_3_) {
        int i = (byte)((int)(p_putNormal_1_ * 127.0F)) & 255;
        int j = (byte)((int)(p_putNormal_2_ * 127.0F)) & 255;
        int k = (byte)((int)(p_putNormal_3_ * 127.0F)) & 255;
        int l = i | j << 8 | k << 16;
        int i1 = this.vertexFormat.getNextOffset() >> 2;
        int j1 = (this.vertexCount - 4) * i1 + this.vertexFormat.getNormalOffset() / 4;
        this.rawIntBuffer.put(j1, l);
        this.rawIntBuffer.put(j1 + i1, l);
        this.rawIntBuffer.put(j1 + i1 * 2, l);
        this.rawIntBuffer.put(j1 + i1 * 3, l);
    }

    private void nextVertexFormatIndex() {
        ++this.vertexFormatIndex;
        this.vertexFormatIndex %= this.vertexFormat.getElementCount();
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        if (this.vertexFormatElement.getUsage() == EnumUsage.PADDING) {
            this.nextVertexFormatIndex();
        }

    }

    public VertexBuffer normal(float p_normal_1_, float p_normal_2_, float p_normal_3_) {
        int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch(this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(i, p_normal_1_);
                this.byteBuffer.putFloat(i + 4, p_normal_2_);
                this.byteBuffer.putFloat(i + 8, p_normal_3_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(i, (int)p_normal_1_);
                this.byteBuffer.putInt(i + 4, (int)p_normal_2_);
                this.byteBuffer.putInt(i + 8, (int)p_normal_3_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(i, (short)((int)(p_normal_1_ * 32767.0F) & '\uffff'));
                this.byteBuffer.putShort(i + 2, (short)((int)(p_normal_2_ * 32767.0F) & '\uffff'));
                this.byteBuffer.putShort(i + 4, (short)((int)(p_normal_3_ * 32767.0F) & '\uffff'));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(i, (byte)((int)(p_normal_1_ * 127.0F) & 255));
                this.byteBuffer.put(i + 1, (byte)((int)(p_normal_2_ * 127.0F) & 255));
                this.byteBuffer.put(i + 2, (byte)((int)(p_normal_3_ * 127.0F) & 255));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void setTranslation(double p_setTranslation_1_, double p_setTranslation_3_, double p_setTranslation_5_) {
        this.xOffset = p_setTranslation_1_;
        this.yOffset = p_setTranslation_3_;
        this.zOffset = p_setTranslation_5_;
    }

    public void finishDrawing() {
            this.isDrawing = false;
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.getBufferSize() * 4);
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getDrawMode() {
        return this.drawMode;
    }

    public void putColor4(int p_putColor4_1_) {
        for(int i = 0; i < 4; ++i) {
            this.putColor(p_putColor4_1_, i + 1);
        }

    }

    public void putColorRGB_F4(float p_putColorRGB_F4_1_, float p_putColorRGB_F4_2_, float p_putColorRGB_F4_3_) {
        for(int i = 0; i < 4; ++i) {
            this.putColorRGB_F(p_putColorRGB_F4_1_, p_putColorRGB_F4_2_, p_putColorRGB_F4_3_, i + 1);
        }

    }

    public boolean isColorDisabled() {
        return this.noColor;
    }

    @SideOnly(Side.CLIENT)
    public class State {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;

        public State(int[] p_i46453_2_, VertexFormat p_i46453_3_) {
            this.stateRawBuffer = p_i46453_2_;
            this.stateVertexFormat = p_i46453_3_;
        }

        public int[] getRawBuffer() {
            return this.stateRawBuffer;
        }

        public int getVertexCount() {
            return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
        }

        public VertexFormat getVertexFormat() {
            return this.stateVertexFormat;
        }
    }
}
