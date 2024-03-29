package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.data.SkinDyeType;
import net.minecraft.resources.ResourceLocation;

public class SkinPaintType implements ISkinPaintType {

    private final int id;
    private final int index;

    private SkinDyeType dyeType;
    private float textureU;
    private float textureV;
    private ResourceLocation registryName;

    public SkinPaintType(int index, int id) {
        this.id = id;
        this.index = index;
        this.textureU = 0;
        this.textureV = 0;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    //    @Override
//    public ExtraColourType getColourType() {
//        return colourType;
//    }

    public SkinPaintType setTexture(float u, float v) {
        this.textureU = u;
        this.textureV = v;
        return this;
    }

    @Override
    public SkinDyeType getDyeType() {
        return dyeType;
    }

    public SkinPaintType setDyeType(SkinDyeType dyeType) {
        this.dyeType = dyeType;
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public float getU() {
        return textureU;
    }

    @Override
    public float getV() {
        return textureV;
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinPaintType other = (SkinPaintType) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
