package ru.nanolive.draconicplus.common.fusioncrafting.network;

import java.io.IOException;

import com.brandon3055.brandonscore.BrandonsCore;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileInventoryBase;
import ru.nanolive.draconicplus.network.AbstractMessage;

public class PacketTileMessage extends AbstractMessage.AbstractServerMessage<PacketTileMessage>{

    public static final byte BOOLEAN_INDEX = 0;
    public static final byte BYTE_INDEX = 1;
    public static final byte INT_INDEX = 2;
    public static final byte DOUBLE_INDEX = 3;
    public static final byte FLOAT_INDEX = 4;
    public static final byte STRING_INDEX = 5;
    public static final byte TAG_INDEX = 6;

    public BlockPos tilePos;
    private byte pktIndex;
    public String stringValue = "";
    public float floatValue = 0F;
    public double doubleValue = 0;
    public int intValue = 0;
    public byte byteValue = 0;
    public boolean booleanValue = false;
    public NBTTagCompound compound;
    public byte dataType;

    public PacketTileMessage() {
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, boolean booleanValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.booleanValue = booleanValue;
        this.dataType = BOOLEAN_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, byte byteValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.byteValue = byteValue;
        this.dataType = BYTE_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, int intValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.intValue = intValue;
        this.dataType = INT_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, double doubleValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.doubleValue = doubleValue;
        this.dataType = DOUBLE_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, float floatValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.floatValue = floatValue;
        this.dataType = FLOAT_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, String stringValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.stringValue = stringValue;
        this.dataType = STRING_INDEX;
    }

    public PacketTileMessage(TileInventoryBase tile, byte pktIndex, NBTTagCompound compound, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.pktIndex = pktIndex;
        this.compound = compound;
        this.dataType = TAG_INDEX;
    }

	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
        dataType = buffer.readByte();
        pktIndex = buffer.readByte();

        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        tilePos = new BlockPos(x, y, z);


        switch (dataType) {
            case BOOLEAN_INDEX:
                booleanValue = buffer.readBoolean();
                break;
            case BYTE_INDEX:
                byteValue = buffer.readByte();
                break;
            case INT_INDEX:
                intValue = buffer.readInt();
                break;
            case DOUBLE_INDEX:
                doubleValue = buffer.readDouble();
                break;
            case FLOAT_INDEX:
                floatValue = buffer.readFloat();
                break;
            case STRING_INDEX:
                stringValue = ByteBufUtils.readUTF8String(buffer);
                break;
            case TAG_INDEX:
                compound = ByteBufUtils.readTag(buffer);
                break;
        }
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeByte(dataType);
        buffer.writeByte(pktIndex);

        buffer.writeInt(tilePos.x);
        buffer.writeInt(tilePos.y);
        buffer.writeInt(tilePos.z);


        switch (dataType) {
            case BOOLEAN_INDEX:
                buffer.writeBoolean(booleanValue);
                break;
            case BYTE_INDEX:
                buffer.writeByte(byteValue);
                break;
            case INT_INDEX:
                buffer.writeInt(intValue);
                break;
            case DOUBLE_INDEX:
                buffer.writeDouble(doubleValue);
                break;
            case FLOAT_INDEX:
                buffer.writeFloat(floatValue);
                break;
            case STRING_INDEX:
                ByteBufUtils.writeUTF8String(buffer, stringValue);
                break;
            case TAG_INDEX:
                ByteBufUtils.writeTag(buffer, compound);
                break;
        }
	}
	
    public boolean isBool() {
        return dataType == BOOLEAN_INDEX;
    }

    public boolean isByte() {
        return dataType == BYTE_INDEX;
    }

    public boolean isInt() {
        return dataType == INT_INDEX;
    }

    public boolean isDouble() {
        return dataType == DOUBLE_INDEX;
    }

    public boolean isFload() {
        return dataType == FLOAT_INDEX;
    }

    public boolean isString() {
        return dataType == STRING_INDEX;
    }

    public boolean isNBT() {
        return dataType == TAG_INDEX;
    }

    public byte getIndex() {
        return pktIndex;
    }

	@Override
	protected void process(EntityPlayer player, Side side) {
        TileEntity tile = BrandonsCore.proxy.getMCServer().getEntityWorld().getTileEntity(tilePos.x, tilePos.y, tilePos.z);
        if (tile instanceof TileInventoryBase) {
        	((TileInventoryBase) tile).receivePacketFromClient(this, (EntityPlayerMP) player);
        }
	}

}
