package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArrowSkinRenderer<T extends AbstractArrowEntity, M extends Model> extends SkinRenderer<T, M> {

    public ArrowSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        return bakedPart.getType() == SkinPartTypes.ITEM_ARROW;
    }
}
