package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public class ArrowSkinRenderer<T extends AbstractArrow, M extends Model> extends SkinRenderer<T, M> {

    public ArrowSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        return bakedPart.getType() == SkinPartTypes.ITEM_ARROW;
    }
}
