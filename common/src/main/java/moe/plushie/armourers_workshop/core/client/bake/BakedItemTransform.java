package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinModelManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.PoseStackWrapper;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

public class BakedItemTransform implements ISkinTransform {

    private Entity entity;
    private ItemStack itemStack = ItemStack.EMPTY;
    private AbstractItemTransformType transformType = AbstractItemTransformType.NONE;

    private final ISkinPartType partType;
    private final BakedItemModel itemModel;

    public BakedItemTransform(BakedSkinPart bakedSkinPart, BakedSkin bakedSkin) {
        this.partType = bakedSkinPart.getType();
        this.itemModel = bakedSkin.getItemModel();
    }

    public void setup(Entity entity, SkinItemSource itemSource) {
        this.entity = entity;
        this.transformType = itemSource.getTransformType();
        this.itemStack = itemSource.getItem();
    }

    @Override
    public void apply(IPoseStack poseStack) {
        PoseStackWrapper wrapper = ObjectUtils.safeCast(poseStack, PoseStackWrapper.class);
        if (entity == null || wrapper == null) {
            return;
        }
        auto model = SkinModelManager.getInstance().getModel(partType, itemModel, itemStack, entity);
        float f1 = 16f;
        float f2 = 1 / 16f;
        boolean flag = transformType.isLeftHand();
        poseStack.scale(f1, f1, f1);
        model.applyTransform(transformType, flag, wrapper.pose());
        poseStack.scale(f2, f2, f2);
        if (flag) {
            // we need mirror the skin of drawing,
            // because the poseStack.scale have a bug,
            // it will cause the x, y, z change as same time.
            poseStack.multiply(OpenMatrix4f.createScaleMatrix(-1, 1, 1));
            poseStack.multiply(OpenMatrix3f.createScaleMatrix(-1, 1, 1));
        }
    }
}
