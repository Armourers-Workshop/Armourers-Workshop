package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.projectile.ThrownTrident;

@Environment(EnvType.CLIENT)
public class TridentSkinRenderer<T extends ThrownTrident, V extends Model, M extends IModelHolder<V>> extends SkinRenderer<T, V, M> {

    public TridentSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        return bakedPart.getType() == SkinPartTypes.ITEM_TRIDENT;
    }
}
