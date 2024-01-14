package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SkinWingsTransform implements ISkinTransform {

    private float partialTicks = 0;
    private boolean isFallFlying = false;

    private final boolean isMirror;

    private final SkinMarker marker;
    private final SkinProperties properties;

    public SkinWingsTransform(ISkinPartType partType, SkinProperties properties, SkinMarker marker) {
        this.marker = marker;
        this.properties = properties;
        this.isMirror = ((ICanRotation) partType).isMirror();
    }

    public void setup(@Nullable Entity entity, float partialTicks) {
        this.partialTicks = partialTicks;
        this.isFallFlying = entity instanceof LivingEntity && ((LivingEntity) entity).isFallFlying();
    }

    @Override
    public void apply(IPoseStack poseStack) {
        IVector3i point = marker.getPosition();
        float angle = (float) getRotationDegrees();
        Vector3f offset = new Vector3f(point.getX() + 0.5f, point.getY() + 0.5f, point.getZ() + 0.5f);
        if (!isMirror) {
            angle = -angle;
        }

        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        poseStack.rotate(getRotationMatrix().rotationDegrees(angle));
        poseStack.translate(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    private double getRotationDegrees() {
        if (properties == null) {
            return 0;
        }

        double maxAngle = properties.get(SkinProperty.WINGS_MAX_ANGLE);
        double minAngle = properties.get(SkinProperty.WINGS_MIN_ANGLE);
        String movementTypeName = properties.get(SkinProperty.WINGS_MOVMENT_TYPE);
        SkinProperty.MovementType movementType = SkinProperty.MovementType.valueOf(movementTypeName);

        double flapTime = properties.get(SkinProperty.WINGS_IDLE_SPEED);
        if (isFallFlying) {
            flapTime = properties.get(SkinProperty.WINGS_FLYING_SPEED);
        }

        double angle = partialTicks % flapTime;

        if (movementType == SkinProperty.MovementType.EASE) {
            angle = Math.sin(angle / flapTime * Math.PI * 2);
        }
        if (movementType == SkinProperty.MovementType.LINEAR) {
            angle = angle / flapTime;
        }

        double fullAngle = maxAngle - minAngle;
        if (movementType == SkinProperty.MovementType.LINEAR) {
            return fullAngle * angle;
        }

        return -minAngle - fullAngle * ((angle + 1D) / 2);
    }

    private Vector3f getRotationMatrix() {
        switch (marker.getDirection()) {
            case UP:
                return Vector3f.YP;
            case DOWN:
                return Vector3f.YN;
            case SOUTH:
                return Vector3f.ZN;
            case NORTH:
                return Vector3f.ZP;
            case EAST:
                return Vector3f.XP;
            case WEST:
                return Vector3f.XN;
        }
        return Vector3f.YP;
    }
}
