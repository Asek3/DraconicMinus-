package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;

/**
 * Created by brandon3055 on 23/4/2016.
 */
public interface IDPParticleFactory {

    EntityFX getEntityFX(int particleID, World world, Vec3D pos, Vec3D speed, int... args);
}