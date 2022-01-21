package moe.plushie.armourers_workshop.core.render.other;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.common.item.SkinItem;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.render.model.ModelTransformer;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class BakedSkin implements IBakedSkin {


    public static List<BakedSkin> skins;

    private final Skin skin;
    private final SkinDye skinDye;
    private final Map<SkinCache.Key, Rectangle3f> cachedBounds = new HashMap<>();

    private final List<BakedSkinPart> skinParts;


    public BakedSkin(Skin skin, SkinDye skinDye) {
        this.skin = skin;
        this.skinDye = skinDye;
        this.skinParts = skin.getRenderParts().stream().map(BakedSkinPart::new).collect(Collectors.toList());
    }

    public static BakedSkin by(SkinDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        return by(descriptor.getIdentifier());
    }

    public static BakedSkin by(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        int iq = Integer.parseInt(identifier);
        if (iq < BakedSkin.skins.size()) {
            return BakedSkin.skins.get(iq);
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static BakedSkin of(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SkinItem) {
            return by(SkinDescriptor.of(itemStack));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Skin getSkin() {
        return skin;
    }

    public List<BakedSkinPart> getSkinParts() {
        return skinParts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SkinDye getSkinDye() {
        return skinDye;
    }

    public boolean isOverride(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        ISkinType skinType = skin.getType();
        if (skinType instanceof ISkinToolType) {
            return itemStack.getItem().is(((ISkinToolType) skinType).getTag());
        }
        return false;
    }

    public Rectangle3f getRenderBounds(Entity entity, Model model, @Nullable Vector3f rotation) {
        //
        return cachedBounds.computeIfAbsent(new SkinCache.Key(model, rotation), (key) -> {
            //
            Matrix4f matrix = Matrix4f.createScaleMatrix(1, 1, 1);
            SkinRenderShape shape = getRenderShape(model, ItemCameraTransforms.TransformType.NONE);
            if (rotation != null) {
                matrix.multiply(new Quaternion(-rotation.x(), -rotation.y(), rotation.z(), true));
                shape.mul(matrix);
            }
            //
            Rectangle3f bounds = shape.bounds().copy();
            if (rotation != null) {
                Vector4f center = new Vector4f(bounds.getCenter());
                matrix.invert();
                center.transform(matrix);
                bounds.setX(center.x() - bounds.getWidth() / 2);
                bounds.setY(center.y() - bounds.getHeight() / 2);
                bounds.setZ(center.z() - bounds.getDepth() / 2);
            }
            return bounds;
        });
    }

    public SkinRenderShape getRenderShape(Model model, ItemCameraTransforms.TransformType transformType) {
        SkinRenderShape shape = SkinRenderShape.empty();
        MatrixStack matrixStack = new MatrixStack();
        for (SkinPart part : skin.getRenderParts()) {
            SkinRenderShape shape1 = part.getRenderShape().copy();
            ModelRenderer modelRenderer = ModelTransformer.getTransform(part.getType(), model, transformType);
            if (modelRenderer != null) {
                matrixStack.pushPose();
                ModelTransformer.apply(matrixStack, modelRenderer);
                SkinUtils.apply(matrixStack, null, part, 0);
                shape1.mul(matrixStack.last().pose());
                matrixStack.popPose();
            }
            shape.add(shape1);
        }
        return shape;
    }

}
