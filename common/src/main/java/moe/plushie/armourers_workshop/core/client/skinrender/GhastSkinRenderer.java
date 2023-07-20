package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.monster.Ghast;

@Environment(EnvType.CLIENT)
public class GhastSkinRenderer<T extends Ghast, M extends IModel> extends LivingSkinRenderer<T, M> {

    public GhastSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinModelTransformer<T, M> transformer) {
        transformer.registerArmor(SkinPartTypes.BIPPED_HEAD, this::offset);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> watcher) {
        watcher.modelToParts(SkinPartTypes.BIPPED_HEAD, M::getAllParts);
    }

    private void offset(IPoseStack poseStack, M model) {
        poseStack.translate(0.0f, 24.0f + 1.5f, 0.0f);
        poseStack.scale(2f, 2f, 2f);
    }
}
