package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.projectile.AbstractArrow;

@Environment(EnvType.CLIENT)
public class ArrowSkinRenderer<T extends AbstractArrow, M extends IModel> extends SkinRenderer<T, M> {

    public ArrowSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        return bakedPart.getType() == SkinPartTypes.ITEM_ARROW;
    }
}
