package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.ISkinBabyModel;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, V extends HumanoidModel<T>, M extends IHumanoidModelHolder<V>> extends ExtendedSkinRenderer<T, V, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void setHeadPart(PoseStack matrixStack, M model) {
        super.setHeadPart(matrixStack, model);
        if (model.isBaby()) {
            float scale = model.getBabyScale();
            IVector3f offset = model.getBabyOffset();
            if (offset == null) {
                return;
            }
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(offset.getX() / 16f, offset.getY() / 16f, offset.getZ() / 16f);
        }
    }
}

