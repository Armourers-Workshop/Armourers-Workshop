package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public class SkinItemRenderer extends AbstractItemStackRenderer {

    private static SkinItemRenderer INSTANCE;
    private ItemStack playerMannequinItem;
    private MannequinEntity entity;
    private MannequinModel<MannequinEntity> model;

    public static SkinItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
        if (bakedSkin == null) {
            return;
        }
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        ItemTransform transform = bakedModel.getTransforms().getTransform(ItemTransforms.ofType(transformType));

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f); // reset to center

        Vector3f rotation = new Vector3f(-transform.rotation.x(), -transform.rotation.y(), transform.rotation.z());
        Vector3f scale = Vector3f.ONE;//new Vector3f(transform.scale.x(), transform.scale.y(), transform.scale.z());
        ColorScheme scheme = descriptor.getColorScheme();
        ExtendedItemRenderer.renderSkinInBox(bakedSkin, scheme, itemStack, rotation, scale, 1, 1, 1, 0, light, poseStack, renderTypeBuffer);

        poseStack.popPose();
    }

    public MannequinEntity getMannequinEntity() {
        ClientLevel level = Minecraft.getInstance().level;
        if (entity == null) {
            entity = new MannequinEntity(ModEntityTypes.MANNEQUIN.get().get(), level);
            entity.setId(MannequinEntity.PLACEHOLDER_ENTITY_ID);
            entity.setExtraRenderer(false); // never magic cir
        }
        if (entity.getLevel() != level) {
            entity.changeLevel(level);
        }
        return entity;
    }

    public MannequinModel<?> getMannequinModel() {
        MannequinEntity entity = getMannequinEntity();
        if (model == null && entity != null) {
            model = new MannequinModel<>();
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
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return ItemStack.EMPTY;
            }
            playerMannequinItem = MannequinItem.of(player, 1.0f);
        }
        return playerMannequinItem;
    }
}
