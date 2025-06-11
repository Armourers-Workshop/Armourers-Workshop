package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class SkinPaintType implements ISkinPaintType {

    private final int id;
    private final int index;

    private SkinDyeType dyeType;
    private OpenResourceLocation registryName;
    private SkinTexturePos texturePos = SkinTexturePos.DEFAULT;

    public SkinPaintType(int index, int id) {
        this.id = id;
        this.index = index;
    }

    public void setRegistryName(OpenResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public OpenResourceLocation getRegistryName() {
        return registryName;
    }

    public SkinPaintType setTexturePos(float u, float v) {
        this.texturePos = new SkinTexturePos(u, v, 1, 1, 256, 256);
        return this;
    }

    @Override
    public SkinTexturePos getTexturePos() {
        return texturePos;
    }

    public SkinPaintType setDyeType(SkinDyeType dyeType) {
        this.dyeType = dyeType;
        return this;
    }

    @Override
    public SkinDyeType getDyeType() {
        return dyeType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getIndex() {
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
