package moe.plushie.armourers_workshop.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class FirstPersonPlayerModel<T extends LivingEntity> extends EntityModel<T> {

    private static FirstPersonPlayerModel<?> INSTANCE;

    public static FirstPersonPlayerModel<?> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirstPersonPlayerModel<>();
        }
        return INSTANCE;
    }

    @Override
    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
    }

    @Override
    public void renderToBuffer(PoseStack p_225598_1_, VertexConsumer p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
    }
}
