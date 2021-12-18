package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    private SkinItemRenderer() {}

//    public static void renderSkinAsItem(ItemStack stack, boolean showSkinPaint, int targetWidth, int targetHeight) {
//        if (SkinNBTHelper.stackHasSkinData(stack)) {
//            SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//            renderSkinAsItem(skinPointer, showSkinPaint, false, targetWidth, targetHeight);
//        }
//    }
    
    public static void renderSkinAsItem(ISkinDescriptor skinPointer, boolean showSkinPaint, boolean doLodLoading, int targetWidth, int targetHeight) {
//        renderSkinAsItem(ClientSkinCache.INSTANCE.getSkin(skinPointer), skinPointer, showSkinPaint, doLodLoading, targetWidth, targetHeight);
    }

    public static int mp1 = 0;
    private static BipedModel<LivingEntity> playerModel = new BipedModel<>(0);
    private static DumpLivingEntity dumpLivingEntity = new DumpLivingEntity();
    private static class DumpLivingEntity extends LivingEntity {
        DumpLivingEntity() {
            super(EntityType.ARMOR_STAND, null);
        }

        @Override
        public Iterable<ItemStack> getArmorSlots() {
            return null;
        }

        @Override
        public ItemStack getItemBySlot(EquipmentSlotType slotType) {
            return null;
        }

        @Override
        public void setItemSlot(EquipmentSlotType slotType, ItemStack itemStack) {

        }

        @Override
        public HandSide getMainArm() {
            return null;
        }
    }

    private static int lastI = 0;
    
    public static void renderSkinAsItem(MatrixStack matrixStack, IRenderTypeBuffer renderer, BakedSkin bakedSkin, boolean showSkinPaint, boolean doLodLoading, int targetWidth, int targetHeight) {
        if (bakedSkin == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
//
//        if (playerModel == null) {
//            playerModel = new BipedModel(0);
//            Minecraft minecraft = Minecraft.getInstance();
//            ClientPlayerEntity player = minecraft.player;
////            VillagerEntity
//           ZombieEntity entity = new ZombieEntity(minecraft.player.level);
//            playerModel.prepareMobModel(entity, 0, 0, 0);
//            playerModel.setupAnim(entity, 0, 0, 0, 0, 0);
////            playerModel = new ClientPlayerEntity(minecraft, minecraft.level, player.connection, player.getStats(), player.getRecipeBook(), false, false);
//
//        }

//        Minecraft p_i232461_1_, ClientWorld p_i232461_2_, ClientPlayNetHandler p_i232461_3_, StatisticsManager
//        p_i232461_4_, ClientRecipeBook p_i232461_5_, boolean p_i232461_6_, boolean p_i232461_7_


//
//        float blockScale = 16F;
//        float mcScale = 1F / blockScale;
//        matrixStack.scale(mcScale, mcScale, mcScale);

//        Rectangle3D maxBounds = new Rectangle3D(bakedSkin.getSkin().getRenderShape(playerModel).bounds());
//        float newScaleW = (float)targetWidth / Math.max((float)bounds.getSize(), (float)bounds.getZsize());
//        float newScaleH = (float)targetHeight / (float)bounds.getYsize();
//        float newScale = Math.min(newScaleW, newScaleH);
//        matrixStack.scale(newScale, newScale, newScale);
//        matrixStack.translate(-maxBounds.getMidX(), -maxBounds.getMidY(), -maxBounds.getMidZ());

        if (lastI != mp1) {
            playerModel.young = (mp1 == 1);
            playerModel.crouching = (mp1 == 2);
            playerModel.riding = (mp1 == 3);
            playerModel.setupAnim(dumpLivingEntity, 0, 0, 0, 0, 0);
            lastI = mp1;
        }

        SkinModelRenderer.INSTANCE.renderSkin(bakedSkin, playerModel, matrixStack, renderer);

        matrixStack.popPose();
    }

    
//    public static void renderSkinWithHelper(Skin skin, ISkinDescriptor skinPointer, boolean showSkinPaint, boolean doLodLoading, MatrixStack matrixStack, IVertexBuilder builder) {
//        ISkinType skinType = skinPointer.getIdentifier().getType();
//        if (skinType == null) {
//            skinType = skin.getType();
//        }
//        skinType = skin.getType();
//
//        IEquipmentModel targetModel = SkinModelRenderer.INSTANCE.getTypeHelperForModel(SkinModelRenderer.ModelType.MODEL_BIPED, skinType);
////        targetModel.render(null, skin, matrix, renderer);
//        targetModel.render(null, skin, null, showSkinPaint, skinPointer.getSkinDye(), null, true, 0, doLodLoading, matrixStack, builder);
//
//    }
//
//    public static void renderSkinWithoutHelper(ISkinDescriptor skinPointer, boolean doLodLoading) {
//        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
//        if (skin == null) {
//            return;
//        }
//        SkinModelRenderHelper.INSTANCE.modelHelperDummy.render(null, skin, null, true, skinPointer.getSkinDye(), null, true, 0, doLodLoading);
//    }
}
