package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.level.block.Block;

public class SkinCubeType implements ISkinCubeType {

    protected final int id;
    protected final boolean glass;
    protected final boolean glowing;
    protected final IRegistryHolder<Block> block;

    protected IResourceLocation registryName;

    public SkinCubeType(int id, boolean glass, boolean glowing, IRegistryHolder<Block> block) {
        this.id = id;
        this.glass = glass;
        this.glowing = glowing;
        this.block = block;
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(IResourceLocation registryName) {
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

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "id", id, "name", registryName, "glowing", glowing, "glass", glass);
    }
}
