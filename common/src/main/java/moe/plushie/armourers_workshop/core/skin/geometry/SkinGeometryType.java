package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.level.block.Block;

public class SkinGeometryType implements ISkinGeometryType {

    protected final int id;
    protected final IRegistryHolder<Block> block;

    protected OpenResourceLocation registryName;

    public SkinGeometryType(int id, IRegistryHolder<Block> block) {
        this.id = id;
        this.block = block;
    }

    @Override
    public OpenResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(OpenResourceLocation registryName) {
        this.registryName = registryName;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinGeometryType that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
