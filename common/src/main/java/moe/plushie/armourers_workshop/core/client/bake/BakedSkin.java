package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.math.Matrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Environment(value = EnvType.CLIENT)
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
        // we can't bind textures to skin when the item stack rendering.
        if (entity.getId() != MannequinEntity.PLACEHOLDER_ENTITY_ID) {
            ResourceLocation resolvedTexture = PlayerTextureLoader.getInstance().getTextureLocation(entity);
            if (!Objects.equals(resolvedColorScheme.getTexture(), resolvedTexture)) {
                resolvedColorScheme.setTexture(resolvedTexture);
            }
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

    public Rectangle3f getRenderBounds(@Nullable Entity entity, @Nullable Model model, @Nullable Vector3f rotation, ItemStack itemStack) {
        if (entity == null) {
            entity = SkinItemRenderer.getInstance().getMannequinEntity();
        }
        if (model == null) {
            model = SkinItemRenderer.getInstance().getMannequinModel();
        }
        Object key = SkinCache.borrowKey(model, rotation);
        Rectangle3f bounds = cachedBounds.get(key);
        if (bounds != null) {
            SkinCache.returnKey(key);
            return bounds;
        }
        Matrix4f matrix = Matrix4f.createScaleMatrix(1, 1, 1);
        OpenVoxelShape shape = getRenderShape(entity, model, itemStack, ItemTransforms.TransformType.NONE);
        if (rotation != null) {
            matrix.rotate(new Quaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true));
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

    public OpenVoxelShape getRenderShape(Entity entity, Model model, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer != null) {
            return getRenderShape(entity, ModelHolder.ofNullable(model), itemStack, transformType, renderer);
        }
        return OpenVoxelShape.empty();
    }

    public <T extends Entity, V extends Model, M extends IModelHolder<V>> OpenVoxelShape getRenderShape(T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, SkinRenderer<T, V, M> renderer) {
        SkinRenderContext context = new SkinRenderContext();
        context.init(null, 0, 0, transformType, MatrixUtils.stack(), null);
        context.setItem(itemStack, 0);
        context.setTransforms(entity, model);
        OpenVoxelShape shape = OpenVoxelShape.empty();
        for (BakedSkinPart bakedPart : skinParts) {
            addRenderShape(shape, entity, model, bakedPart, renderer, context);
        }
        return shape;
    }

    private <T extends Entity, V extends Model, M extends IModelHolder<V>> void addRenderShape(OpenVoxelShape shape, T entity, M model, BakedSkinPart part, SkinRenderer<T, V, M> renderer, SkinRenderContext context) {
        if (!renderer.prepare(entity, model, part, this, context)) {
            return;
        }
        IPoseStack poseStack = context.poseStack;
        OpenVoxelShape shape1 = part.getRenderShape().copy();
        poseStack.pushPose();
        renderer.apply(entity, model, part, this, context);
        shape1.mul(poseStack.lastPose());
        shape.add(shape1);
        for (BakedSkinPart childPart : part.getChildren()) {
            addRenderShape(shape, entity, model, childPart, renderer, context);
        }
        poseStack.popPose();
    }

    public <T extends Entity, V extends Model, M extends IModelHolder<V>> boolean shouldRenderPart(T entity, M model, BakedSkinPart bakedPart, SkinRenderContext context) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.ITEM_ARROW) {
            // arrow part only render in arrow entity
            if (isArrowEntity(entity)) {
                return true;
            }
            // we have some old skin that only contain arrow part,
            // so when it happens, we need to be compatible rendering it.
            // we use `NONE` to rendering the GUI/Ground/ItemFrame.
            if (context.transformType == ItemTransforms.TransformType.NONE) {
                return skinParts.size() == 1;
            }
            return false;
        }
        if (isArrowEntity(entity)) {
            return false; // arrow entity only render arrow part
        }
        if (partType instanceof ICanUse && entity instanceof LivingEntity) {
            int useTick = getUseTick((LivingEntity) entity, context.itemStack);
            Range<Integer> useRange = ((ICanUse) partType).getUseRange();
            return useRange.contains(Math.min(useTick, maxUseTick));
        }
        return true;
    }

    private boolean isArrowEntity(Entity entity) {
        // in vanilla considers trident to be a special arrow,
        // but this no fits we definition of arrow skin.
        if (entity instanceof ThrownTrident) {
            return false;
        }
        return entity instanceof AbstractArrow;
    }

    private void loadBlockBounds() {
        if (skin.getType() != SkinTypes.BLOCK) {
            return;
        }
        for (BakedSkinPart skinPart : skinParts) {
            HashMap<BlockPos, Rectangle3i> bm = skinPart.getPart().getBlockBounds();
            if (bm != null) {
                cachedBlockBounds.putAll(bm);
            }
        }
    }

    private int getUseTick(LivingEntity entity, ItemStack itemStack) {
        if (entity.getUseItem() == itemStack) {
            return entity.getTicksUsingItem();
        }
        return 0;
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
