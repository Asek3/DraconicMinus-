package ru.nanolive.draconicplus.common.fusioncrafting.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.util.ResourceLocation;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

/**
 * Created by brandon3055 on 24/06/2016.
 */
public class FusionRotationSound extends PositionedSound implements ITickableSound {
    private TileFusionCraftingCore tile;

    public FusionRotationSound(TileFusionCraftingCore tile) {
        super(new ResourceLocation(MoreInfo.MODID, "fusion_rotation"));
        this.tile = tile;
        xPosF = tile.xCoord + 0.5F;
        yPosF = tile.yCoord + 0.5F;
        zPosF = tile.zCoord + 0.5F;
        repeat = true;
        volume = 1.5F;
    }

    @Override
    public boolean isDonePlaying() {
        return tile.isInvalid() || !tile.craftingInProgress();
    }

    @Override
    public void update() {
    	field_147663_c = 0.1F + (((tile.getCraftingStage() - 1000) / 1000F) * 1.9F);
    }
}