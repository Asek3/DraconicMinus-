package ru.nanolive.draconicplus.common.fusioncrafting.network;

import java.io.IOException;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileInventoryBase;
import ru.nanolive.draconicplus.network.AbstractMessage;

public class PacketSyncableObject extends AbstractMessage.AbstractClientMessage<PacketSyncableObject> {

    public static final byte BOOLEAN_INDEX = 0;
    public static final byte BYTE_INDEX = 1;
    public static final byte INT_INDEX = 2;
    public static final byte DOUBLE_INDEX = 3;
    public static final byte FLOAT_INDEX = 4;
    public static final byte STRING_INDEX = 5;
    public static final byte TAG_INDEX = 6;
    public static final byte VEC3I_INDEX = 7;
    public static final byte LONG_INDEX = 8;
    public static final byte SHORT_INDEX = 9;
    public static final byte VEC3D_INDEX = 10;


    public BlockPos tilePos;
    public byte index;
    public String stringValue = "";
    public float floatValue = 0F;
    public double doubleValue = 0;
    public int intValue = 0;
    public short shortValue = 0;
    public byte byteValue = 0;
    public boolean booleanValue = false;
    public NBTTagCompound compound;
    public Vec3D vec3D;
    public long longValue;
    public boolean updateOnReceived;
    public byte dataType;

    public PacketSyncableObject() {
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, boolean booleanValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.booleanValue = booleanValue;
        this.dataType = BOOLEAN_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, byte byteValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.byteValue = byteValue;
        this.dataType = BYTE_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, short shortValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.shortValue = shortValue;
        this.dataType = SHORT_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, int intValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.intValue = intValue;
        this.dataType = INT_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, double doubleValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.doubleValue = doubleValue;
        this.dataType = DOUBLE_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, float floatValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.floatValue = floatValue;
        this.dataType = FLOAT_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, String stringValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.stringValue = stringValue;
        this.dataType = STRING_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, NBTTagCompound compound, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.compound = compound;
        this.dataType = TAG_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, long longValue, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.longValue = longValue;
        this.dataType = LONG_INDEX;
    }

    public PacketSyncableObject(TileInventoryBase tile, byte syncableIndex, Vec3D vec3D, boolean updateOnReceived) {
        this.tilePos = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        this.index = syncableIndex;
        this.vec3D = vec3D;
        this.dataType = VEC3D_INDEX;
    }
	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
        dataType = buffer.readByte();
        index = buffer.readByte();

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
            case VEC3D_INDEX:
                vec3D = new Vec3D(0, 0, 0);
                vec3D.x = (int) buffer.readDouble();
                vec3D.y = (int) buffer.readDouble();
                vec3D.z = (int) buffer.readDouble();
                break;
            case LONG_INDEX:
                longValue = buffer.readLong();
                break;
            case SHORT_INDEX:
                shortValue = buffer.readShort();
                break;
        }
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeByte(dataType);
        buffer.writeByte(index);

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
            case VEC3D_INDEX:
                buffer.writeDouble(vec3D.x);
                buffer.writeDouble(vec3D.y);
                buffer.writeDouble(vec3D.z);
                break;
            case LONG_INDEX:
                buffer.writeLong(longValue);
                break;
            case SHORT_INDEX:
                buffer.writeShort(shortValue);
                break;
        }
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if(side.isClient()) {
            TileEntity tile = FMLClientHandler.instance().getWorldClient().getTileEntity(tilePos.x, tilePos.y, tilePos.z);
            if (tile instanceof TileInventoryBase) {
                ((TileInventoryBase) tile).receiveSyncPacketFromServer(this);
            }
		}
	}

}
