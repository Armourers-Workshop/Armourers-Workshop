package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import net.minecraft.block.Block;

public class SkinCube implements ISkinCube {

    protected String registryName;
    protected final int id;
    protected final boolean glass;
    protected final boolean glowing;

    public SkinCube(int id, boolean glass, boolean glowing) {
        this.id = id;
        this.glass = glass;
        this.glowing = glowing;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public boolean isGlass() {
        return glass;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Block getMinecraftBlock() {
//        return ModBlocks.SKIN_CUBE;
        return null;
    }
}
