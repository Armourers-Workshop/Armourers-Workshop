package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class SkinPaintType implements ISkinPaintType {

    private final int id;
    private final int index;

    private SkinDyeType dyeType;
    private OpenResourceKey registryName;
    private SkinTexturePos texturePos = SkinTexturePos.DEFAULT;

    public SkinPaintType(int index, int id) {
        this.id = id;
        this.index = index;
    }

    public void setRegistryName(OpenResourceKey registryName) {
        this.registryName = registryName;
    }

    @Override
    public OpenResourceKey registryName() {
        return registryName;
    }

    public SkinPaintType setTexturePos(float u, float v) {
        this.texturePos = new SkinTexturePos(u, v, 1, 1, 256, 256);
        return this;
    }

    @Override
    public SkinTexturePos texturePos() {
        return texturePos;
    }

    public SkinPaintType setDyeType(SkinDyeType dyeType) {
        this.dyeType = dyeType;
        return this;
    }

    @Override
    public SkinDyeType dyeType() {
        return dyeType;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int ordinal() {
        return index;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinPaintType that)) return false;
        return id == that.id;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
