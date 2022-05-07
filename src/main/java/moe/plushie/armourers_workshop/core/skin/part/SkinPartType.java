package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Objects;

public abstract class SkinPartType implements ISkinPartType {

    protected ResourceLocation registryName;

    protected Rectangle3i buildingSpace;
    protected Rectangle3i guideSpace;
    protected Vector3i offset;

    public SkinPartType() {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public SkinPartType setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    @Override
    public Rectangle3i getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public Rectangle3i getGuideSpace() {
        return this.guideSpace;
    }

    @Override
    public Vector3i getOffset() {
        return this.offset;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 0;
    }

    @Override
    public boolean isPartRequired() {
        return false;
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 0);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return false;
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinPartType that = (SkinPartType) o;
        return Objects.equals(registryName, that.registryName);
    }

    @Override
    public int hashCode() {
        return registryName.hashCode();
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
