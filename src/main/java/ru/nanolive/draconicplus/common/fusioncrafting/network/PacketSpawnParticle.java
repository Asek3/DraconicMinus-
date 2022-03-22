package ru.nanolive.draconicplus.common.fusioncrafting.network;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.DPEffectHandler;
import ru.nanolive.draconicplus.network.AbstractMessage;

public class PacketSpawnParticle extends AbstractMessage.AbstractClientMessage<PacketSpawnParticle> {
	  
    private int particleID;
    private double xCoord;
    private double yCoord;
    private double zCoord;
    private double xSpeed;
    private double ySpeed;
    private double zSpeed;
    private double viewRange;
    private int[] args;

    public PacketSpawnParticle() {
    }

    public PacketSpawnParticle(int particleID, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, double viewRange, int... args) {
        this.particleID = particleID;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
        this.viewRange = viewRange;
        this.args = args;
    }
	  
	  protected void read(PacketBuffer buffer) throws IOException {
	      particleID = buffer.readInt();
	      xCoord = buffer.readDouble();
	      yCoord = buffer.readDouble();
	      zCoord = buffer.readDouble();
	      xSpeed = buffer.readDouble();
	      ySpeed = buffer.readDouble();
	      zSpeed = buffer.readDouble();
	      viewRange = buffer.readDouble();
	      int argsL = buffer.readByte();
	      args = new int[argsL];
	      for (int i = 0; i < argsL; i++) {
	          args[i] = buffer.readInt();
	      }
	  }
	  
	  protected void write(PacketBuffer buffer) throws IOException {
		  buffer.writeInt(particleID);
		  buffer.writeDouble(xCoord);
	      buffer.writeDouble(yCoord);
	      buffer.writeDouble(zCoord);
	      buffer.writeDouble(xSpeed);
	      buffer.writeDouble(ySpeed);
	      buffer.writeDouble(zSpeed);
	      buffer.writeDouble(viewRange);
	      buffer.writeByte(args.length);
	        for (int i : args) {
	        	buffer.writeInt(i);
	        }
	  }
	  
	  @SideOnly(Side.CLIENT)
	  protected void process(EntityPlayer player, Side side) {
		if(side.isClient())
		  DPEffectHandler.spawnFX(this.particleID, Minecraft.getMinecraft().theWorld, this.xCoord, this.yCoord, this.zCoord, this.xSpeed, this.ySpeed, this.zSpeed, this.viewRange, this.args);  
	  }
	
}
