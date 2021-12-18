package moe.plushie.armourers_workshop.core.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
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


    public static <T extends Model> ModelRenderer getModelRenderer(SkinPart skinPart, T model, ItemCameraTransforms.TransformType transformType) {
        //
        if (skinPart.getType() instanceof ICanHeld) {
            if (transformType == null) {
                transformType = ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
            }
            ModelRenderer modelRenderer = HAND.transform(transformType, model, skinPart);
            if (modelRenderer != null) {
                return modelRenderer;
            }
        }
        return ARMOR.transform(skinPart.getType(), model, skinPart);
    }

    public static void apply(MatrixStack matrixStack, ModelRenderer modelRenderer) {
        if (modelRenderer == EMPTY) {
            return;
        }
        //
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

    public static VoxelShape apply(VoxelShape shape, ModelRenderer modelRenderer) {
        if (modelRenderer == EMPTY) {
            return shape;
        }
        MatrixStack matrixStack = new MatrixStack();
        apply(matrixStack, modelRenderer);
//
//        Matrix4f matrix = getMatrix(modelRenderer);
//        if (modelRenderer instanceof OffsetModelRenderer) {
//            OffsetModelRenderer offsetModelRenderer = ((OffsetModelRenderer) modelRenderer);
//            if (offsetModelRenderer.modelRenderer != null) {
//                Matrix4f matrix1 = getMatrix(offsetModelRenderer.modelRenderer);
//                matrix1.multiply(matrix);
//                matrix = matrix1;
//            }
//            if (offsetModelRenderer.scale != null) {
//                Vector3f scale = offsetModelRenderer.scale;
//                matrix.multiply(Matrix4f.createScaleMatrix(scale.x(), scale.y(), scale.z()));
//            }
//        }
        return SkinUtils.apply(shape, matrixStack.last().pose());
    }

//    private static Matrix4f getMatrix(ModelRenderer modelRenderer) {
//        Matrix4f matrix = new Matrix4f();
//        matrix.setIdentity();
//        matrix.setTranslation(modelRenderer.x, modelRenderer.y, modelRenderer.z);
//        if (modelRenderer.zRot != 0) {
//            matrix.multiply(Vector3f.ZP.rotation(modelRenderer.zRot));
//        }
//        if (modelRenderer.yRot != 0) {
//            matrix.multiply(Vector3f.YP.rotation(modelRenderer.yRot));
//        }
//        if (modelRenderer.xRot != 0) {
//            matrix.multiply(Vector3f.XP.rotation(modelRenderer.xRot));
//        }
//        return matrix;
//    }


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
        <M extends Model> ModelRenderer transform(K key, M model, SkinPart part, Map<K, List<Object>> list) {
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

        <M extends Model> ModelRenderer transform(K key, M model, SkinPart part) {
            if (model == null) {
                return EMPTY;
            }
            // fast path
            ModelRenderer modelRenderer0 = transform(key, model, part, registerList.get(model.getClass()));
            if (modelRenderer0 != null) {
                return modelRenderer0;
            }
            // slow path
            for (Map.Entry<Class<?>, Map<K, List<Object>>> entry : registerList.entrySet()) {
                if (!entry.getKey().isInstance(model)) {
                    continue;
                }
                ModelRenderer modelRenderer1 = transform(key, model, part, entry.getValue());
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
