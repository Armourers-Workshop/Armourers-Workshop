package moe.plushie.armourers_workshop.core.render.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.render.model.MannequinModel;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.model.DragonHeadModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends LivingRenderer<T, MannequinModel<T>> {

    private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;

    public MannequinEntityRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new MannequinModel<>(0, false), 0.0f);
        this.addLayer(new BipedArmorLayer<>(this, new MannequinArmorModel<>(0.5f), new MannequinArmorModel<>(1.0f)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new SkinWardrobeArmorLayer<>(this));
        // Has two models by mannequin, only deciding which model using when texture specified.
        this.normalModel = this.model;
        this.slimModel = new MannequinModel<>(0, true);
    }

    @Override
    public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        this.model = getResolvedModel(entity);
        this.model.setAllVisible(true);
        super.render(entity, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
    }

    public MannequinModel<T> getResolvedModel(T entity) {
        return normalModel;
//        return slimModel;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return STEVE_SKIN_LOCATION;
//        return ALEX_SKIN_LOCATION;
    }

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
        return false;
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

//    private static final Map<SkullBlock.ISkullType, GenericHeadModel> MODEL_BY_TYPE = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
//        GenericHeadModel genericheadmodel = new GenericHeadModel(0, 0, 64, 32);
//        GenericHeadModel genericheadmodel1 = new HumanoidHeadModel();
//        DragonHeadModel dragonheadmodel = new DragonHeadModel(0.0F);
//        p_209262_0_.put(SkullBlock.Types.SKELETON, genericheadmodel);
//        p_209262_0_.put(SkullBlock.Types.WITHER_SKELETON, genericheadmodel);
//        p_209262_0_.put(SkullBlock.Types.PLAYER, genericheadmodel1);
//        p_209262_0_.put(SkullBlock.Types.ZOMBIE, genericheadmodel1);
//        p_209262_0_.put(SkullBlock.Types.CREEPER, genericheadmodel);
//        p_209262_0_.put(SkullBlock.Types.DRAGON, dragonheadmodel);
//    });
//    private static final Map<SkullBlock.ISkullType, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
//        p_209263_0_.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
//        p_209263_0_.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
//        p_209263_0_.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
//        p_209263_0_.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
//        p_209263_0_.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
//        p_209263_0_.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
//    });
//
//    public SkullTileEntityRenderer(TileEntityRendererDispatcher p_i226015_1_) {
//        super(p_i226015_1_);
//    }
//
//    public void render(SkullTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
//        float f = p_225616_1_.getMouthAnimation(p_225616_2_);
//        BlockState blockstate = p_225616_1_.getBlockState();
//        boolean flag = blockstate.getBlock() instanceof WallSkullBlock;
//        Direction direction = flag ? blockstate.getValue(WallSkullBlock.FACING) : null;
//        float f1 = 22.5F * (float)(flag ? (2 + direction.get2DDataValue()) * 4 : blockstate.getValue(SkullBlock.ROTATION));
//        renderSkull(direction, f1, ((AbstractSkullBlock)blockstate.getBlock()).getType(), p_225616_1_.getOwnerProfile(), f, p_225616_3_, p_225616_4_, p_225616_5_);
//    }
//
//    public static void renderSkull(@Nullable Direction p_228879_0_, float p_228879_1_, SkullBlock.ISkullType p_228879_2_, @Nullable GameProfile p_228879_3_, float p_228879_4_, MatrixStack p_228879_5_, IRenderTypeBuffer p_228879_6_, int p_228879_7_) {
//        GenericHeadModel genericheadmodel = MODEL_BY_TYPE.get(p_228879_2_);
//        p_228879_5_.pushPose();
//        if (p_228879_0_ == null) {
//            p_228879_5_.translate(0.5D, 0.0D, 0.5D);
//        } else {
//            float f = 0.25F;
//            p_228879_5_.translate((double)(0.5F - (float)p_228879_0_.getStepX() * 0.25F), 0.25D, (double)(0.5F - (float)p_228879_0_.getStepZ() * 0.25F));
//        }
//
//        p_228879_5_.scale(-1.0F, -1.0F, 1.0F);
//        IVertexBuilder ivertexbuilder = p_228879_6_.getBuffer(getRenderType(p_228879_2_, p_228879_3_));
//        genericheadmodel.setupAnim(p_228879_4_, p_228879_1_, 0.0F);
//        genericheadmodel.renderToBuffer(p_228879_5_, ivertexbuilder, p_228879_7_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//        p_228879_5_.popPose();
//    }
//

    @Nullable
    @Override
    protected RenderType getRenderType(T entity, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        PlayerTexture texture = PlayerTextureLoader.getInstance().loadTexture(entity.getEntityData().get(MannequinEntity.DATA_TEXTURE));
        if (texture != null && texture.getLocation() != null) {
            return RenderType.entityTranslucent(texture.getLocation());
        }
        return super.getRenderType(entity, p_230496_2_, p_230496_3_, p_230496_4_);
    }
}