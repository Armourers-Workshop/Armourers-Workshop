package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;


public abstract class SkinPartType implements ISkinPartType {

    protected OpenResourceLocation registryName;

    protected OpenRectangle3i bounds;
    protected OpenRectangle3i buildingSpace;
    protected OpenRectangle3i guideSpace;
    protected OpenVector3i offset;

    protected OpenVector3i renderOffset = OpenVector3i.ZERO;
    protected float renderPolygonOffset = 0;

    public SkinPartType() {
    }

    @Override
    public String getName() {
        return registryName.toString();
    }

    @Override
    public OpenResourceLocation getRegistryName() {
        return registryName;
    }

    public SkinPartType setRegistryName(OpenResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    @Override
    public OpenRectangle3i getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public OpenRectangle3i getGuideSpace() {
        return this.guideSpace;
    }

    public OpenRectangle3i getGuideSpace(EntityTextureDescriptor.Model model) {
        return getGuideSpace();
    }

    @Override
    public OpenVector3i getOffset() {
        return this.offset;
    }

    @Override
    public OpenRectangle3i getBounds() {
        return bounds;
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
    public OpenVector3i getRenderOffset() {
        return renderOffset;
    }

    @Override
    public float getRenderPolygonOffset() {
        return renderPolygonOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinPartType that)) return false;
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
