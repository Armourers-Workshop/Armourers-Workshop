package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.level.block.Block;

public class SkinGeometryType implements ISkinGeometryType {

    protected final int id;
    protected final IRegistryHolder<Block> block;

    protected OpenResourceKey registryName;

    public SkinGeometryType(int id, IRegistryHolder<Block> block) {
        this.id = id;
        this.block = block;
    }

    @Override
    public OpenResourceKey registryName() {
        return registryName;
    }

    public void setRegistryName(OpenResourceKey registryName) {
        this.registryName = registryName;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Block block() {
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
