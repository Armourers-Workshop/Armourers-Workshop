package moe.plushie.armourers_workshop.core.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWContributors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"deprecation", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class SkinItemStackRenderer extends ItemStackTileEntityRenderer {

    private ItemStack playerMannequinItem;

    private MannequinEntity entity;
    private BipedModel<MannequinEntity> model;

    private static SkinItemStackRenderer INSTANCE;

    public static SkinItemStackRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinItemStackRenderer();
        }
        return INSTANCE;
    }


    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        ItemTransformVec3f transform = bakedModel.getTransforms().getTransform(transformType);

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f); // reset to center

        if (item == AWItems.SKIN) {
            BakedSkin bakedSkin = BakedSkin.of(itemStack);
            if (bakedSkin != null) {
                Vector3f rotation = new Vector3f(-transform.rotation.x(), -transform.rotation.y(), transform.rotation.z());
                SkinItemRenderer.renderSkin(bakedSkin, rotation, transform.scale, 1, 1, 1, 0, light, matrixStack, renderTypeBuffer);
            }
        }

        if (item == AWItems.MANNEQUIN) {
            PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.of(itemStack);
            SkinItemRenderer.renderMannequin(descriptor, transform.rotation, transform.scale, 1, 1, 1, 0, light, matrixStack, renderTypeBuffer);
        }

        matrixStack.popPose();
    }

    public MannequinEntity getMannequinEntity() {
        ClientWorld level = Minecraft.getInstance().level;
        if (entity == null) {
            entity = new MannequinEntity(AWEntities.MANNEQUIN, level);
            entity.setExtraRenderer(false); // never magic cir
        }
        if (entity.level != level) {
            entity.level = level;
        }
        return entity;
    }

    public BipedModel<?> getMannequinModel() {
        MannequinEntity entity = getMannequinEntity();
        if (model == null && entity != null) {
            model = new BipedModel<>(0);
            model.young = false;
            model.crouching = false;
            model.riding = false;
            model.prepareMobModel(entity, 0, 0, 0);
            model.setupAnim(entity, 0, 0, 0, 0, 0);
        }
        return model;
    }

    public ItemStack getPlayerMannequinItem() {
        if (playerMannequinItem == null) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null) {
                return ItemStack.EMPTY;
            }
            GameProfile profile = player.getGameProfile();
            AWContributors.Contributor contributor = AWContributors.getCurrentContributor();
            if (contributor != null) {
                profile = new GameProfile(contributor.uuid, contributor.username);
            }
            PlayerTextureDescriptor descriptor = new PlayerTextureDescriptor(profile);
            CompoundNBT entityTag = new CompoundNBT();
            entityTag.put(AWConstants.NBT.ENTITY_TEXTURE, descriptor.serializeNBT());
            playerMannequinItem = new ItemStack(AWItems.MANNEQUIN);
            playerMannequinItem.getOrCreateTag().put(AWConstants.NBT.ENTITY, entityTag);
        }
        return playerMannequinItem;
    }
}