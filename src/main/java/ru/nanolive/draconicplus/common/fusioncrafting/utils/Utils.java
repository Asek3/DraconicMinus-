package ru.nanolive.draconicplus.common.fusioncrafting.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.common.fusioncrafting.BlockPos;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;

import java.text.DecimalFormat;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Created by Brandon on 25/07/2014.
 */
public class Utils {

    private static DecimalFormat energyValue = new DecimalFormat("###,###,###,###,###");

    public static String formatNumber(double value) {
        if (value < 1000D) return String.valueOf(value);
        else if (value < 1000000D) return String.valueOf(Math.round(value) / 1000D) + "K";
        else if (value < 1000000000D) return String.valueOf(Math.round(value / 1000D) / 1000D) + "M";
        else if (value < 1000000000000D) return String.valueOf(Math.round(value / 1000000D) / 1000D) + "B";
        else return String.valueOf(Math.round(value / 1000000000D) / 1000D) + "T";
    }

    public static String formatNumber(long value) {
        if (value < 1000L) return String.valueOf(value);
        else if (value < 1000000L) return String.valueOf(Math.round(value) / 1000D) + "K";
        else if (value < 1000000000L) return String.valueOf(Math.round(value / 1000L) / 1000D) + "M";
        else if (value < 1000000000000L) return String.valueOf(Math.round(value / 1000000L) / 1000D) + "B";
        else if (value < 1000000000000000L) return String.valueOf(Math.round(value / 1000000000L) / 1000D) + "T";
        else if (value < 1000000000000000000L) return String.valueOf(Math.round(value / 1000000000000L) / 1000D) + "Quad";
        else if (value <= Long.MAX_VALUE) return String.valueOf(Math.round(value / 1000000000000000L) / 1000D) + "Quin";
        else return "Something is very broken!!!!";
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(int value) {
        return energyValue.format(value);
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(long value) {
        return energyValue.format(value);
    }

    /**
     * Calculates the exact distance between two points in 3D space
     *
     * @param x1 point A x
     * @param y1 point A y
     * @param z1 point A z
     * @param x2 point B x
     * @param y2 point B y
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
        return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    public static boolean inRangeSphere(BlockPos posA, BlockPos posB, int range) {
        if (Math.abs(posA.x - posB.x) > range || Math.abs(posA.y - posB.y) > range || Math.abs(posA.z - posB.z) > range) {
            return false;
        }

        else return getDistanceSq(posA.x, posA.y, posA.z, posB.x, posB.y, posB.z) <= range * range;
    }

    /**
     * Calculates the exact distance between two points in 2D space
     *
     * @param x1 point A x
     * @param z1 point A z
     * @param x2 point B x
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistanceAtoB(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }

    public static double getDistanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public static double getDistanceSq(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return dx * dx + dz * dz;
    }

    /**
     * Returns true if this is a client connected to a remote server.
     */
    public static boolean isConnectedToDedicatedServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
    }

    /**
     * Update the blocks an all 6 sides of a blocks.
     */
    public static void updateNeabourBlocks(World world, BlockPos pos) {
		world.notifyBlocksOfNeighborChange(pos.x, pos.y, pos.z, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x - 1, pos.y, pos.z, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x + 1, pos.y, pos.z, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x, pos.y - 1, pos.z, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x, pos.y + 1, pos.z, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x, pos.y, pos.z - 1, world.getBlock(pos.x, pos.y, pos.z));
		world.notifyBlocksOfNeighborChange(pos.x, pos.y, pos.z + 1, world.getBlock(pos.x, pos.y, pos.z));
    }

    /**
     * Determine the orientation of a blocks based on the position of the entity that placed it.
     */
    public static int determineOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + 1.82D - (double) entity.getYOffset();

            if (d0 - (double) y > 2.0D) return 0;

            if ((double) y - d0 > 0.0D) return 1;
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
    }

    public static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    public static int getNearestMultiple(int number, int multiple) {
        int result = number;

        if (number < 0) result *= -1;

        if (result % multiple == 0) return number;
        else if (result % multiple < multiple / 2) result = result - result % multiple;
        else result = result + (multiple - result % multiple);

        if (number < 0) result *= -1;

        return result;
    }

    /**
     * Simple method to convert a Double object to a primitive int
     */
    public static int toInt(double d) {
        return (int) d;
    }

    public static int parseInt(String s) {
        return parseInt(s, true);
    }

    public static int parseInt(String s, boolean catchException) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        if (catchException) {
            try {
                return Integer.parseInt(s);
            }
            catch (Exception e) {
                return 0;
            }
        }
        else {
            return Integer.parseInt(s);
        }
    }

    public static int parseHex(String s) {
        return parseHex(s, true);
    }

    public static int parseHex(String s, boolean catchException) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        if (catchException) {
            try {
                return (int) Long.parseLong(s, 16);
            }
            catch (Exception e) {
                return 0;
            }
        }
        else {
            return (int) Long.parseLong(s, 16);
        }
    }

    public static double map(double valueIn, double inMin, double inMax, double outMin, double outMax) {
        return (valueIn - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}