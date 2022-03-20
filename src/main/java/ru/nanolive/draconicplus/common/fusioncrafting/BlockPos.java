package ru.nanolive.draconicplus.common.fusioncrafting;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.AbstractIterator;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class BlockPos {

	public int x;
	public int y;
	public int z;
	
	public BlockPos(double x, double y, double z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}
	
	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPos add(double x, double y, double z) {
		 return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.x + x, (double)this.y + y, (double)this.z + z);
	}

	public BlockPos add(int x, int y, int z) {
		 return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.x + x, this.y + y, this.z + z);
	}
	
    public static Iterable<BlockPos> getAllInBox(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        return new Iterable<BlockPos>()
        {
            public Iterator<BlockPos> iterator()
            {
                return new AbstractIterator<BlockPos>()
                {
                    private boolean first = true;
                    private int lastPosX;
                    private int lastPosY;
                    private int lastPosZ;
                    protected BlockPos computeNext()
                    {
                        if (this.first)
                        {
                            this.first = false;
                            this.lastPosX = x1;
                            this.lastPosY = y1;
                            this.lastPosZ = z1;
                            return new BlockPos(x1, y1, z1);
                        }
                        else if (this.lastPosX == x2 && this.lastPosY == y2 && this.lastPosZ == z2)
                        {
                            return (BlockPos)this.endOfData();
                        }
                        else
                        {
                            if (this.lastPosX < x2)
                            {
                                ++this.lastPosX;
                            }
                            else if (this.lastPosY < y2)
                            {
                                this.lastPosX = x1;
                                ++this.lastPosY;
                            }
                            else if (this.lastPosZ < z2)
                            {
                                this.lastPosX = x1;
                                this.lastPosY = y1;
                                ++this.lastPosZ;
                            }

                            return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
                        }
                    }
                };
            }
        };
    }
    
    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to)
    {
        return getAllInBox(Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z), Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z));
    }
	
}
