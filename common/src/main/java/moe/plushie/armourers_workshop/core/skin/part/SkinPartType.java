package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public abstract class SkinPartType implements ISkinPartType {

    protected OpenResourceLocation registryName;

    protected OpenRectangle3i bounds = OpenRectangle3i.ZERO;
    protected OpenRectangle3i buildingSpace = OpenRectangle3i.ZERO;
    protected OpenVector3i guideOrigin = OpenVector3i.ZERO;
    protected OpenRectangle3i guideSpace = OpenRectangle3i.ZERO;
    protected OpenVector3i offset = OpenVector3i.ZERO;

    protected OpenVector3i renderOffset = OpenVector3i.ZERO;
    protected float renderPolygonOffset = 0;

    public SkinPartType() {
    }

    @Override
    public String name() {
        return registryName.toString();
    }

    @Override
    public OpenResourceLocation registryName() {
        return registryName;
    }

    public SkinPartType setRegistryName(OpenResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    @Override
    public OpenRectangle3i buildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public OpenRectangle3i guideSpace() {
        return this.guideSpace;
    }

    @Override
    public OpenVector3i offset() {
        return this.offset;
    }

    public OpenVector3i origin() {
        return guideOrigin;
    }

    @Override
    public OpenRectangle3i bounds() {
        return bounds;
    }

    @Override
    public int minimumMarkersNeeded() {
        return 0;
    }

    @Override
    public int maximumMarkersNeeded() {
        return 0;
    }

    @Override
    public boolean isPartRequired() {
        return false;
    }

    @Override
    public OpenVector3i renderOffset() {
        return renderOffset;
    }

    @Override
    public float renderPolygonOffset() {
        return renderPolygonOffset;
    }

    public OpenVector3i guideOriginByModel(PlayerSkinModel model) {
        return guideOrigin;
    }

    public OpenRectangle3i guideSpaceByModel(PlayerSkinModel model) {
        return guideSpace;
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
