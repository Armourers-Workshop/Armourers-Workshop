package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TridentSkinRenderer<T extends TridentEntity, M extends Model> extends SkinRenderer<T, M> {

    public TridentSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType) {
        return bakedPart.getType() == SkinPartTypes.ITEM_TRIDENT;
    }
}
