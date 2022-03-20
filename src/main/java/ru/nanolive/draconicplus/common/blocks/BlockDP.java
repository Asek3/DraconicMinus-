package ru.nanolive.draconicplus.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockDP extends Block {

    public BlockDP(final Material material) {
        super(material);
        this.setHardness(5F);
        this.setResistance(10.0F);
    }

    public BlockDP() {
        super(Material.iron);
        this.setHardness(5F);
        this.setResistance(10.0F);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", "draconicplus" + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    public String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("draconicplus:" + getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

}