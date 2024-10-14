package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinModelManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    public void setup(@Nullable Entity entity, SkinItemSource itemSource) {
        this.entity = entity;
        this.transformType = itemSource.getTransformType();
        this.itemStack = itemSource.getItem();
    }

    @Override
    public void apply(IPoseStack poseStack) {
        if (entity == null) {
            return;
        }
        var model = SkinModelManager.getInstance().getModel(partType, itemModel, itemStack, entity);
        var f1 = 16f;
        var f2 = 1 / 16f;
        var flag = transformType.isLeftHand();
        poseStack.scale(f1, f1, f1);
        model.applyTransform(poseStack, flag, transformType);
        poseStack.scale(f2, f2, f2);
        if (flag) {
            poseStack.scale(-1, 1, 1);
        }
    }
}
