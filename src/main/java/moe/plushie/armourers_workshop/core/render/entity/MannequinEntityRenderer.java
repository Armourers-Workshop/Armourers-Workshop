package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.render.model.MannequinModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends LivingRenderer<T, MannequinModel<T>> {

    private static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");

    public MannequinEntityRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new MannequinModel<>(0), 0.0F);
        this.addLayer(new BipedArmorLayer<>(this, new MannequinArmorModel<>(0.5F), new MannequinArmorModel<>(1.0F)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new SkinWardrobeArmorLayer<>(this));
    }

    @Override
    public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        this.model.setAllVisible(true);
        super.render(entity, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return DEFAULT_SKIN_LOCATION;
    }

//    protected void scale(AbstractClientPlayerEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
//        float f = 0.9375F;
//        p_225620_2_.scale(0.9375F, 0.9375F, 0.9375F);
//    }
//
//    protected void renderNameTag(AbstractClientPlayerEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
//        double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
//        p_225629_3_.pushPose();
//        if (d0 < 100.0D) {
//            Scoreboard scoreboard = p_225629_1_.getScoreboard();
//            ScoreObjective scoreobjective = scoreboard.getDisplayObjective(2);
//            if (scoreobjective != null) {
//                Score score = scoreboard.getOrCreatePlayerScore(p_225629_1_.getScoreboardName(), scoreobjective);
//                super.renderNameTag(p_225629_1_, (new StringTextComponent(Integer.toString(score.getScore()))).append(" ").append(scoreobjective.getDisplayName()), p_225629_3_, p_225629_4_, p_225629_5_);
//                p_225629_3_.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
//            }
//        }
//
//        super.renderNameTag(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
//        p_225629_3_.popPose();
//    }
//
//    protected void setupRotations(ArmorStandEntity entity, MatrixStack matrixStack, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
//        float f = entity.getSwimAmount(p_225621_5_);
//        if (entity.isFallFlying()) {
//            super.setupRotations(entity, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
//            float f1 = (float) entity.getFallFlyingTicks() + p_225621_5_;
//            float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
//            if (!entity.isAutoSpinAttack()) {
//                matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entity.xRot)));
//            }
//
//            Vector3d vector3d = entity.getViewVector(p_225621_5_);
//            Vector3d vector3d1 = entity.getDeltaMovement();
//            double d0 = Entity.getHorizontalDistanceSqr(vector3d1);
//            double d1 = Entity.getHorizontalDistanceSqr(vector3d);
//            if (d0 > 0.0D && d1 > 0.0D) {
//                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
//                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
//                matrixStack.mulPose(Vector3f.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
//            }
//        } else if (f > 0.0F) {
//            super.setupRotations(entity, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
//            float f3 = entity.isInWater() ? -90.0F - entity.xRot : -90.0F;
//            float f4 = MathHelper.lerp(f, 0.0F, f3);
//            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
//            if (entity.isVisuallySwimming()) {
//                matrixStack.translate(0.0D, -1.0D, (double) 0.3F);
//            }
//        } else {
//            super.setupRotations(entity, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
//        }
//    }

    @Override
    protected boolean shouldShowName(T entity) {
        if (!entity.isCustomNameVisible()) {
            return false;
        }
        double d0 = this.entityRenderDispatcher.distanceToSqr(entity);
        float f = entity.isCrouching() ? 32.0F : 64.0F;
        return d0 < (double) (f * f);
    }


//    @Nullable
//    protected RenderType getRenderType(ArmorStandEntity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
//        if (!p_230496_1_.isMarker()) {
//            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
//        } else {
//            ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
//            if (p_230496_3_) {
//                return RenderType.entityTranslucent(resourcelocation, false);
//            } else {
//                return p_230496_2_ ? RenderType.entityCutoutNoCull(resourcelocation, false) : null;
//            }
//        }
//    }
}