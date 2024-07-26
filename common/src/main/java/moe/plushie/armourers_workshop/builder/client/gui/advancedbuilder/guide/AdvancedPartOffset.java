package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.part.other.PartitionPartType;
import moe.plushie.armourers_workshop.core.skin.part.wings.LeftWingPartType;
import moe.plushie.armourers_workshop.core.skin.part.wings.RightWingPartType;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class AdvancedPartOffset {

//    public static final Builder<MannequinEntity> MANNEQUIN_ENTITY = new Builder<MannequinEntity>()
//            .put(SkinPartTypes.BIPPED_HAT, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_HEAD, 0, 0, 0)
//
//            .put(SkinPartTypes.BIPPED_CHEST, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_TORSO, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_ARM, 5, 2, 0, -10, 0, -10)
//            .put(SkinPartTypes.BIPPED_RIGHT_ARM, -5, 2, 0, -15, 0, 10)
//
//            .put(SkinPartTypes.BIPPED_LEFT_HAND, 5, 2, 0, -10, 0, -10, 0, 6, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_HAND, -5, 2, 0, -15, 0, 10, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_SKIRT, 0, 12, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_THIGH, 2, 12, 0, -1, 0, -1)
//            .put(SkinPartTypes.BIPPED_RIGHT_THIGH, -2, 12, 0, 1, 0, 1)
//
//            .put(SkinPartTypes.BIPPED_LEFT_LEG, 2, 12, 0, -1, 0, -1, 0, 6, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_LEG, -2, 12, 0, 1, 0, 1, 0, 6, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_FOOT, 2, 12, 0, -1, 0, -1, 0, 8, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_FOOT, -2, 12, 0, 1, 0, 1, 0, 8, 0)
//
//            .put(SkinPartTypes.BIPPED_LEFT_WING, 0, 0, 2)
//            .put(SkinPartTypes.BIPPED_RIGHT_WING, 0, 0, 2)
//            .put(SkinPartTypes.BIPPED_LEFT_PHALANX, 0, 0, 0)
//            .put(SkinPartTypes.BIPPED_RIGHT_PHALANX, 0, 0, 0);


    public static class Builder<T> {

        private final HashMap<ISkinPartType, Function<T, ITransformf>> poses = new HashMap<>();

//        public Builder<T> put(ISkinPartType partType) {
//            return put(partType, partType.getRenderOffset());
//        }
//
//        public Builder<T> put(ISkinPartType partType, Function<T, Rotations> provider) {
//            return put(partType, partType.getRenderOffset(), provider);
//        }
//
//        public Builder<T> put(ISkinPartType partType, IVector3i off) {
//            ModelPartPose pose = new ModelPartPose(off.getX(), off.getY(), off.getZ(), 0, 0, 0);
//            return add(partType, it -> pose);
//        }

        public Builder<T> put(ISkinPartType partType, float tx, float ty, float tz) {
            return put(partType, tx, ty, tz, 0, 0, 0, 0, 0, 0);
        }

        public Builder<T> put(ISkinPartType partType, float tx, float ty, float tz, float xRot, float yRot, float zRot) {
            return put(partType, tx, ty, tz, xRot, yRot, zRot, 0, 0, 0);
        }

        public Builder<T> put(ISkinPartType partType, float tx, float ty, float tz, float xRot, float yRot, float zRot, float ax, float ay, float az) {
            Vector3f translate = new Vector3f(tx, ty, tz);
            Vector3f rotation = new Vector3f(xRot, yRot, zRot);
            Vector3f afterTranslate = new Vector3f(ax, ay, az);
            SkinTransform transform = SkinTransform.create(translate, rotation, Vector3f.ONE, Vector3f.ZERO, afterTranslate);
            return add(partType, it -> transform);
        }


        @Nullable
        public ITransformf get(T entity, ISkinPartType partType) {
            Function<T, ITransformf> provider = poses.get(partType);
            if (provider != null) {
                return provider.apply(entity);
            }
            return null;
        }

        private Builder<T> add(ISkinPartType partType, Function<T, ITransformf> provider) {
            poses.put(partType, provider);
            return this;
        }
    }
}
