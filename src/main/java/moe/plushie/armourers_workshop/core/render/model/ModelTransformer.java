package moe.plushie.armourers_workshop.core.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ModelTransformer {

    private static final ModelRenderer EMPTY = new ModelRenderer(0, 0, 0, 0);

    private static final TransformerRegistry<ISkinPartType> ARMOR = new TransformerRegistry<>();
    private static final TransformerRegistry<ItemCameraTransforms.TransformType> HAND = new TransformerRegistry<>();


    public static <T extends Model> ModelRenderer getTransform(ISkinPartType partType, T model, ItemCameraTransforms.TransformType transformType) {
        if (partType instanceof ICanHeld) {
            ModelRenderer modelRenderer = HAND.transform(transformType, model);
            if (modelRenderer != null) {
                return modelRenderer;
            }
        }
        return ARMOR.transform(partType, model);
    }

    public static void apply(MatrixStack matrixStack, ModelRenderer modelRenderer) {
        if (modelRenderer == EMPTY) {
            return;
        }
        Vector3f scale = null;
        if (modelRenderer instanceof OffsetModelRenderer) {
            OffsetModelRenderer offsetModelRenderer = (OffsetModelRenderer) modelRenderer;
            scale = offsetModelRenderer.scale;
            if (offsetModelRenderer.modelRenderer != null) {
                apply(matrixStack, offsetModelRenderer.modelRenderer);
            }
        }
        matrixStack.translate(modelRenderer.x, modelRenderer.y, modelRenderer.z);
        if (modelRenderer.zRot != 0) {
            matrixStack.mulPose(Vector3f.ZP.rotation(modelRenderer.zRot));
        }
        if (modelRenderer.yRot != 0) {
            matrixStack.mulPose(Vector3f.YP.rotation(modelRenderer.yRot));
        }
        if (modelRenderer.xRot != 0) {
            matrixStack.mulPose(Vector3f.XP.rotation(modelRenderer.xRot));
        }
        if (scale != null) {
            matrixStack.scale(scale.x(), scale.y(), scale.z());
        }
    }

    public static <M extends Model> void registerArmor(Class<M> model, ISkinPartType key, Function<M, ModelRenderer> function) {
        ARMOR.add(model, key, function);
    }

    public static <M extends Model> void registerItem(Class<M> model, ItemCameraTransforms.TransformType key, Function<M, ModelRenderer> function) {
        HAND.add(model, key, function);
    }

    public static class TransformerRegistry<K> {

        protected Map<Class<?>, Map<K, List<Object>>> registerList = new HashMap<>();

        <M extends Model> void add(Class<M> model, K key, Function<M, ModelRenderer> function) {
            registerList.computeIfAbsent(model, k -> new HashMap<>())
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(function);
        }

        @SuppressWarnings("unchecked")
        <M extends Model> ModelRenderer transform(K key, M model, Map<K, List<Object>> list) {
            if (list == null) {
                return null;
            }
            List<Object> transformers = list.get(key);
            if (transformers == null) {
                return null;
            }
            for (Object transformer : transformers) {
                ModelRenderer modelRenderer = ((Function<M, ModelRenderer>) transformer).apply(model);
                if (modelRenderer != null) {
                    return modelRenderer;
                }
            }
            return null;
        }

        <M extends Model> ModelRenderer transform(K key, M model) {
            if (model == null) {
                return EMPTY;
            }
            // fast path
            ModelRenderer modelRenderer0 = transform(key, model, registerList.get(model.getClass()));
            if (modelRenderer0 != null) {
                return modelRenderer0;
            }
            // slow path
            for (Map.Entry<Class<?>, Map<K, List<Object>>> entry : registerList.entrySet()) {
                if (!entry.getKey().isInstance(model)) {
                    continue;
                }
                ModelRenderer modelRenderer1 = transform(key, model, entry.getValue());
                if (modelRenderer1 != null) {
                    return modelRenderer1;
                }
            }
            return EMPTY;
        }
    }

    public static class OffsetModelRenderer extends ModelRenderer {
        public final ModelRenderer modelRenderer;
        public Vector3f scale;

        public OffsetModelRenderer() {
            super(0, 0, 0, 0);
            this.modelRenderer = null;
        }

        public OffsetModelRenderer(ModelRenderer modelRenderer) {
            super(0, 0, 0, 0);
            this.modelRenderer = modelRenderer;
            this.visible = modelRenderer.visible;
        }
    }
}
