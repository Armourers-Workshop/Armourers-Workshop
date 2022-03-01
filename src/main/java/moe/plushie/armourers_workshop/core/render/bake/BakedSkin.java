package moe.plushie.armourers_workshop.core.render.bake;

import com.google.common.collect.Range;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.action.ICanUse;
import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.render.model.ModelTransformer;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.utils.TextureUtils;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final Skin skin;
    private final SkinDescriptor descriptor;
    private final Map<Object, Rectangle3f> cachedBounds = new HashMap<>();

    private final int maxUseTick;
    private final List<BakedSkinPart> skinParts;

    private final ColorDescriptor colorDescriptor;

    private final ColorScheme preference;
    private final HashMap<Integer, ColorScheme> resolvedColorSchemes = new HashMap<>();

    public BakedSkin(SkinDescriptor descriptor, Skin skin, ColorScheme preference, ColorDescriptor colorDescriptor, ArrayList<BakedSkinPart> bakedParts) {
        this.descriptor = descriptor;
        this.skin = skin;
        this.skinParts = bakedParts;
        this.preference = preference;
        this.colorDescriptor = colorDescriptor;
        this.maxUseTick = getMaxUseTick(bakedParts);
    }

    public boolean accept(SkinDescriptor other) {
        return descriptor.equals(other);
    }

    public boolean accept(ItemStack itemStack) {
        return descriptor.accept(itemStack);
    }

    public ColorScheme resolve(Entity entity, ColorScheme scheme) {
        if (colorDescriptor.isEmpty()) {
            return ColorScheme.EMPTY;
        }
        ColorScheme resolvedColorScheme = resolvedColorSchemes.computeIfAbsent(entity.getId(), k -> preference.copy());
        ResourceLocation resolvedTexture = PlayerTextureLoader.getInstance().getTextureLocation(entity);
        if (!Objects.equals(resolvedColorScheme.getTexture(), resolvedTexture)) {
            resolvedColorScheme.setTexture(resolvedTexture);
        }
        resolvedColorScheme.setReference(scheme);
        return resolvedColorScheme;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Skin getSkin() {
        return skin;
    }

    public List<BakedSkinPart> getSkinParts() {
        return skinParts;
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    public Rectangle3f getRenderBounds(Entity entity, Model model, @Nullable Vector3f rotation) {
        Object key = SkinCache.borrowKey(model, rotation);
        Rectangle3f bounds = cachedBounds.get(key);
        if (bounds != null) {
            SkinCache.returnKey(key);
            return bounds;
        }
        Matrix4f matrix = Matrix4f.createScaleMatrix(1, 1, 1);
        CustomVoxelShape shape = getRenderShape(model, ItemCameraTransforms.TransformType.NONE);
        if (rotation != null) {
            matrix.multiply(new Quaternion(rotation.x(), rotation.y(), rotation.z(), true));
            shape.mul(matrix);
        }
        bounds = shape.bounds().copy();
        if (rotation != null) {
            Vector4f center = new Vector4f(bounds.getCenter());
            matrix.invert();
            center.transform(matrix);
            bounds.setX(center.x() - bounds.getWidth() / 2);
            bounds.setY(center.y() - bounds.getHeight() / 2);
            bounds.setZ(center.z() - bounds.getDepth() / 2);
        }
        cachedBounds.put(key, bounds);
        return bounds;
    }

    public CustomVoxelShape getRenderShape(Model model, ItemCameraTransforms.TransformType transformType) {
        CustomVoxelShape shape = CustomVoxelShape.empty();
        MatrixStack matrixStack = new MatrixStack();
        for (BakedSkinPart part : skinParts) {
            CustomVoxelShape shape1 = part.getRenderShape().copy();
            ModelRenderer modelRenderer = ModelTransformer.getTransform(part.getType(), model, transformType);
            if (modelRenderer != null) {
                matrixStack.pushPose();
                ModelTransformer.apply(matrixStack, modelRenderer);
                SkinUtils.apply(matrixStack, null, part.getPart(), 0);
                shape1.mul(matrixStack.last().pose());
                matrixStack.popPose();
            }
            shape.add(shape1);
        }
        return shape;
    }

    public boolean shouldRenderPart(BakedSkinPart bakedPart, Entity entity, ItemCameraTransforms.TransformType transformType) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.ITEM_ARROW) {
            return entity instanceof ArrowEntity; // arrow part only render in arrow entity
        }
        if (entity instanceof ArrowEntity) {
            return false; // arrow entity only render arrow part
        }
        if (partType instanceof ICanUse && entity instanceof LivingEntity) {
            int useTick = ((LivingEntity) entity).getTicksUsingItem();
            Range<Integer> useRange = ((ICanUse) partType).getUseRange();
            return useRange.contains(Math.min(useTick, maxUseTick));
        }
        return true;
    }

    private int getMaxUseTick(ArrayList<BakedSkinPart> bakedParts) {
        int maxUseTick = 0;
        for (BakedSkinPart bakedPart : bakedParts) {
            ISkinPartType partType = bakedPart.getType();
            if (partType instanceof ICanUse) {
                maxUseTick = Math.max(maxUseTick, ((ICanUse) partType).getUseRange().upperEndpoint());
            }
        }
        return maxUseTick;
    }
}
