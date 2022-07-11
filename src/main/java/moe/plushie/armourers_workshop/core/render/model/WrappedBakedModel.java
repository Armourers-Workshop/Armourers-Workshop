package moe.plushie.armourers_workshop.core.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class WrappedBakedModel implements IBakedModel {

    static IBakedModel skinBakedModel;

    final ItemStack itemStack;
    final World world;
    final LivingEntity entity;
    final IBakedModel bakedModel;

    public WrappedBakedModel(ItemStack itemStack, LivingEntity entity, @Nullable World world, IBakedModel bakedModel) {
        this.itemStack = itemStack;
        this.world = world;
        this.entity = entity;
        this.bakedModel = bakedModel;
    }

    public static IBakedModel wrap(IBakedModel bakedModel, ItemStack itemStack, LivingEntity entity, @Nullable World world) {
        // when the world is empty, this means the model is rendering on the GUI.
        if (world == null) {
            bakedModel = getSkinBakedModel();
        }
        return new WrappedBakedModel(itemStack, entity, world, bakedModel);
    }

    public static IBakedModel getSkinBakedModel() {
        if (skinBakedModel == null) {
            skinBakedModel = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("armourers_workshop:skin#inventory"));
        }
        return skinBakedModel;
    }

    @Override
    public IBakedModel getBakedModel() {
        return bakedModel.getBakedModel();
    }

    @Override
    public boolean doesHandlePerspectives() {
        return getBakedModel().doesHandlePerspectives();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return getBakedModel().handlePerspective(cameraTransformType, mat);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return getBakedModel().getModelData(world, pos, state, tileData);
    }

    @Override
    public boolean isLayered() {
        return getBakedModel().isLayered();
    }

    @Override
    public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous) {
        return getBakedModel().getLayerModels(itemStack, fabulous);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
        return getBakedModel().getQuads(p_200117_1_, p_200117_2_, p_200117_3_);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return getBakedModel().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getBakedModel().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return getBakedModel().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return getBakedModel().isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getBakedModel().getParticleIcon();
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return getBakedModel().getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getBakedModel().getOverrides();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public World getWorld() {
        return world;
    }
}
