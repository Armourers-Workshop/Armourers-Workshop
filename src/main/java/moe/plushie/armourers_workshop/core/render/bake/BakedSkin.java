package moe.plushie.armourers_workshop.core.render.bake;

import com.google.common.collect.Range;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.color.ColorDescriptor;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.extened.AWVoxelShape;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final String identifier;
    private final Skin skin;
    private final HashMap<Object, Rectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<BlockPos, Rectangle3i> cachedBlockBounds = new HashMap<>();

    private final int maxUseTick;
    private final List<BakedSkinPart> skinParts;

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;

    private final ColorScheme preference;
    private final HashMap<Integer, ColorScheme> resolvedColorSchemes = new HashMap<>();

    public BakedSkin(String identifier, Skin skin, ColorScheme preference, SkinUsedCounter usedCounter, ColorDescriptor colorDescriptor, ArrayList<BakedSkinPart> bakedParts) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinParts = bakedParts;
        this.preference = preference;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.maxUseTick = getMaxUseTick(bakedParts);
        this.loadBlockBounds();
    }

    @Nullable
    public static BakedSkin of(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            return of(SkinDescriptor.of(itemStack));
        }
        return null;
    }

    @Nullable
    public static BakedSkin of(SkinDescriptor descriptor) {
        if (!descriptor.isEmpty()) {
            return SkinBakery.getInstance().loadSkin(descriptor.getIdentifier());
        }
        return null;
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

    public ISkinType getType() {
        return skin.getType();
    }

    public List<BakedSkinPart> getSkinParts() {
        return skinParts;
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        return cachedBlockBounds;
    }

    public Rectangle3f getRenderBounds(@Nullable Entity entity, @Nullable Model model, @Nullable Vector3f rotation, @Nullable ItemStack itemStack) {
        if (entity == null) {
            entity = SkinItemStackRenderer.getInstance().getMannequinEntity();
        }
        if (model == null) {
            model = SkinItemStackRenderer.getInstance().getMannequinModel();
        }
        Object key = SkinCache.borrowKey(model, rotation);
        Rectangle3f bounds = cachedBounds.get(key);
        if (bounds != null) {
            SkinCache.returnKey(key);
            return bounds;
        }
        Matrix4f matrix = Matrix4f.createScaleMatrix(1, 1, 1);
        AWVoxelShape shape = getRenderShape(entity, model, itemStack, ItemCameraTransforms.TransformType.NONE);
        if (rotation != null) {
            matrix.multiply(TrigUtils.rotate(rotation.x(), rotation.y(), rotation.z(), true));
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

    public AWVoxelShape getRenderShape(Entity entity, Model model, @Nullable ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null) {
            return AWVoxelShape.empty();
        }
        AWVoxelShape shape = AWVoxelShape.empty();
        MatrixStack matrixStack = new MatrixStack();
        for (BakedSkinPart part : skinParts) {
            if (renderer.prepare(entity, model, this, part, itemStack, transformType)) {
                AWVoxelShape shape1 = part.getRenderShape().copy();
                matrixStack.pushPose();
                renderer.apply(entity, model, itemStack, transformType, part, this, 0, matrixStack);
                shape1.mul(matrixStack.last().pose());
                matrixStack.popPose();
                shape.add(shape1);
            }
        }
        return shape;
    }

    public boolean shouldRenderPart(BakedSkinPart bakedPart, Entity entity, ItemCameraTransforms.TransformType transformType) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.ITEM_ARROW) {
            // arrow part only render in arrow entity
            if (entity instanceof ArrowEntity) {
                return true;
            }
            // we have some old skin that only contain arrow part,
            // so when it happens, we need to be compatible rendering it.
            // we use `NONE` to rendering the GUI/Ground/ItemFrame.
            if (transformType == ItemCameraTransforms.TransformType.NONE) {
                return skinParts.size() == 1;
            }
            return false;
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

    private void loadBlockBounds() {
        if (skin.getType() != SkinTypes.BLOCK) {
            return;
        }
        for (BakedSkinPart skinPart: skinParts) {
            HashMap<BlockPos, Rectangle3i> bm = skinPart.getPart().getBlockBounds();
            if (bm != null) {
                cachedBlockBounds.putAll(bm);
            }
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BakedSkin bakedSkin = (BakedSkin) o;
        return identifier.equals(bakedSkin.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
