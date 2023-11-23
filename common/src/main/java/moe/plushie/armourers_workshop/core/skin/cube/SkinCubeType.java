package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class SkinCubeType implements ISkinCubeType {

    protected final int id;
    protected final boolean glass;
    protected final boolean glowing;
    protected final IRegistryKey<Block> block;

    protected ResourceLocation registryName;

    public SkinCubeType(int id, boolean glass, boolean glowing, IRegistryKey<Block> block) {
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

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "id", id, "name", registryName, "glowing", glowing, "glass", glass);
    }
}
