package ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class VertexFormat {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<VertexFormatElement> elements;
    private final List<Integer> offsets;
    private int nextOffset;
    private int colorElementOffset;
    private List<Integer> uvOffsetsById;
    private int normalElementOffset;

    public VertexFormat(VertexFormat p_i46097_1_) {
        this();

        for(int lvt_2_1_ = 0; lvt_2_1_ < p_i46097_1_.getElementCount(); ++lvt_2_1_) {
            this.addElement(p_i46097_1_.getElement(lvt_2_1_));
        }

        this.nextOffset = p_i46097_1_.getNextOffset();
    }

    public VertexFormat() {
        this.elements = Lists.newArrayList();
        this.offsets = Lists.newArrayList();
        this.nextOffset = 0;
        this.colorElementOffset = -1;
        this.uvOffsetsById = Lists.newArrayList();
        this.normalElementOffset = -1;
    }

    public void clear() {
        this.elements.clear();
        this.offsets.clear();
        this.colorElementOffset = -1;
        this.uvOffsetsById.clear();
        this.normalElementOffset = -1;
        this.nextOffset = 0;
    }

    public VertexFormat addElement(VertexFormatElement p_addElement_1_) {
        if (p_addElement_1_.isPositionElement() && this.hasPosition()) {
            LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        } else {
            this.elements.add(p_addElement_1_);
            this.offsets.add(this.nextOffset);
            switch(VertexFormat.SwitchEnumUseage.field_177382_a[p_addElement_1_.getUsage().ordinal()]){
            //switch(null.field_177382_a[p_addElement_1_.getUsage().ordinal()]) {
                case 1:
                    this.normalElementOffset = this.nextOffset;
                    break;
                case 2:
                    this.colorElementOffset = this.nextOffset;
                    break;
                case 3:
                    this.uvOffsetsById.add(p_addElement_1_.getIndex(), this.nextOffset);
            }

            this.nextOffset += p_addElement_1_.getSize();
            return this;
        }
    }

    public boolean hasNormal() {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset() {
        return this.normalElementOffset;
    }

    public boolean hasColor() {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset() {
        return this.colorElementOffset;
    }

    public boolean hasUvOffset(int p_hasUvOffset_1_) {
        return this.uvOffsetsById.size() - 1 >= p_hasUvOffset_1_;
    }

    public int getUvOffsetById(int p_getUvOffsetById_1_) {
        return (Integer)this.uvOffsetsById.get(p_getUvOffsetById_1_);
    }

    public String toString() {
        String lvt_1_1_ = "format: " + this.elements.size() + " elements: ";

        for(int lvt_2_1_ = 0; lvt_2_1_ < this.elements.size(); ++lvt_2_1_) {
            lvt_1_1_ = lvt_1_1_ + ((VertexFormatElement)this.elements.get(lvt_2_1_)).toString();
            if (lvt_2_1_ != this.elements.size() - 1) {
                lvt_1_1_ = lvt_1_1_ + " ";
            }
        }

        return lvt_1_1_;
    }

    private boolean hasPosition() {
        int lvt_1_1_ = 0;

        for(int lvt_2_1_ = this.elements.size(); lvt_1_1_ < lvt_2_1_; ++lvt_1_1_) {
            VertexFormatElement lvt_3_1_ = (VertexFormatElement)this.elements.get(lvt_1_1_);
            if (lvt_3_1_.isPositionElement()) {
                return true;
            }
        }

        return false;
    }

    public int getIntegerSize() {
        return this.getNextOffset() / 4;
    }

    public int getNextOffset() {
        return this.nextOffset;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public VertexFormatElement getElement(int p_getElement_1_) {
        return (VertexFormatElement)this.elements.get(p_getElement_1_);
    }

    public int getOffset(int p_getOffset_1_) {
        return (Integer)this.offsets.get(p_getOffset_1_);
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            VertexFormat lvt_2_1_ = (VertexFormat)p_equals_1_;
            if (this.nextOffset != lvt_2_1_.nextOffset) {
                return false;
            } else if (!this.elements.equals(lvt_2_1_.elements)) {
                return false;
            } else {
                return this.offsets.equals(lvt_2_1_.offsets);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int lvt_1_1_ = this.elements.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.offsets.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.nextOffset;
        return lvt_1_1_;
    }
    static final class SwitchEnumUseage {

        // $FF: synthetic field
        static final int[] field_177382_a = new int[VertexFormatElement.EnumUsage.values().length];
        private static final String __OBFID = "CL_00002400";

        static {
            try {
                field_177382_a[VertexFormatElement.EnumUsage.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                field_177382_a[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 2;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_177382_a[VertexFormatElement.EnumUsage.UV.ordinal()] = 3;
            } catch (NoSuchFieldError var1) {
                ;
            }

        }
    }
}
