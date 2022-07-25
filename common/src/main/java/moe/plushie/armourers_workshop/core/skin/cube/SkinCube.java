package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class SkinCube implements ISkinCube {

    protected final int id;
    protected final boolean glass;
    protected final boolean glowing;
    protected final IRegistryObject<Block> block;
    protected ResourceLocation registryName;

    public SkinCube(int id, boolean glass, boolean glowing, IRegistryObject<Block> block) {
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

    @Override
    public Block getBlock() {
        return block.get();
    }
}
