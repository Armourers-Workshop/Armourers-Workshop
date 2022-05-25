package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class SkinCube implements ISkinCube {

    protected ResourceLocation registryName;

    protected final int id;
    protected final boolean glass;
    protected final boolean glowing;

    protected final Block block;

    public SkinCube(int id, boolean glass, boolean glowing, Block block) {
        this.id = id;
        this.glass = glass;
        this.glowing = glowing;
        this.block = block;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(ResourceLocation registryName) {
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

    public Block getBlock() {
        return block;
    }
}
