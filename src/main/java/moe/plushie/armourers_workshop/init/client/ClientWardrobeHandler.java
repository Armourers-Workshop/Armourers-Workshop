package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.init.common.ArmourersConfig;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.function.Supplier;


@OnlyIn(Dist.CLIENT)
public class ClientWardrobeHandler {

    public final static float SCALE = 1 / 16f;

    public static void init() {
    }

    public static void onRenderArrow(AbstractArrowEntity entity, Model model, float p_225623_2_, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot)));

        float f9 = (float) entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
        }

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
        matrixStack.scale(-SCALE, -SCALE, SCALE);
        matrixStack.translate(0, 0, -1);

        int count = render(entity, model, light, matrixStack, buffers, ItemCameraTransforms.TransformType.NONE, renderData::getItemSkins);
        if (count != 0) {
            callback.cancel();
        }

        matrixStack.popPose();
    }

    public static void onRenderArmorPre(Entity entity, EntityModel<?> entityModel, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        // apply the model baby scale.
        if (entityModel.young && entityModel instanceof BipedModel<?>) {
            BipedModel<?> bipedModel = (BipedModel<?>) entityModel;
            float scale = 1.0f / bipedModel.babyBodyScale;
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0.0f, bipedModel.bodyYOffset / 16.0f, 0.0f);
        }
    }

    public static void onRenderArmor(Entity entity, Model model, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(SCALE, SCALE, SCALE);

        render(entity, model, light, matrixStack, buffers, null, renderData::getArmorSkins);

        matrixStack.popPose();
    }

    public static void onRenderItem(Entity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(-SCALE, -SCALE, SCALE);

        int count = render(entity, null, light, matrixStack, buffers, transformType, () -> renderData.getItemSkins(itemStack));
        if (count != 0) {
            callback.cancel();
        }

        matrixStack.popPose();
    }

    public static void onRenderEntityInInventoryPre(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
        if (!ArmourersConfig.enableEntityInInventoryClip) {
            return;
        }
        int left, top, width, height;
        switch (scale) {
            case 20: // in creative container screen
                width = 32;
                height = 43;
                left = x - width / 2 + 1;
                top = y - height + 4;
                break;

            case 30: // in survival container screen
                width = 49;
                height = 70;
                left = x - width / 2 - 1;
                top = y - height + 3;
                break;

            default:
                return;
        }
        RenderUtils.enableScissor(left, top, width, height);
    }

    public static void onRenderEntityInInventoryPost(LivingEntity entity) {
        if (!ArmourersConfig.enableEntityInInventoryClip) {
            return;
        }
        RenderUtils.disableScissor();
    }

    public static void onRenderEquipment(LivingEntity entity, Model model, EquipmentSlotType slotType, MatrixStack matrixStack, IRenderTypeBuffer buffers, CallbackInfo callback) {
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer == null) {
            return;
        }
        renderer.apply(entity, model, slotType, 0, matrixStack);
    }

    private static int render(Entity entity, Model model, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers, ItemCameraTransforms.TransformType transformType, Supplier<Iterable<SkinRenderData.Entry>> provider) {
        int r = 0;
        float partialTicks = System.currentTimeMillis() % 100000000;
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer == null) {
            return 0;
        }
        for (SkinRenderData.Entry entry : provider.get()) {
            renderer.render(entity, model, entry.getBakedSkin(), entry.getBakedScheme(), transformType, light, partialTicks, matrixStack, buffers);
            r += 1;
        }
        return r;
    }
}
