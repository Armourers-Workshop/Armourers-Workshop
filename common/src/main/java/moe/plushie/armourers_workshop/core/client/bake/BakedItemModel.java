package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BuiltInModel;

import java.util.Map;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class BakedItemModel extends BuiltInModel {

    public BakedItemModel(SkinItemTransforms itemTransforms, boolean usesBlockLight) {
        super(convert(itemTransforms), ItemOverrides.EMPTY, null, usesBlockLight);
    }

    private static ItemTransforms convert(Map<String, ITransformf> itemTransforms) {
        auto thirdPersonLeftHand = ItemTransform.from(itemTransforms.get("thirdperson_lefthand"));
        auto thirdPersonRightHand = ItemTransform.from(itemTransforms.get("thirdperson_righthand"));
        auto firstPersonLeftHand = ItemTransform.from(itemTransforms.get("firstperson_lefthand"));
        auto firstPersonRightHand = ItemTransform.from(itemTransforms.get("firstperson_righthand"));
        auto head = ItemTransform.from(itemTransforms.get("head"));
        auto gui = ItemTransform.from(itemTransforms.get("gui"));
        auto ground = ItemTransform.from(itemTransforms.get("ground"));
        auto fixed = ItemTransform.from(itemTransforms.get("fixed"));
        return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed);
    }
}
