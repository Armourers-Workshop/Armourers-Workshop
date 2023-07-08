package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
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

@Environment(EnvType.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final int id = ThreadUtils.BAKED_SKIN_COUNTER.incrementAndGet();

    private final String identifier;
    private final Skin skin;
    private final HashMap<Object, Rectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<BlockPos, Rectangle3i> cachedBlockBounds = new HashMap<>();

    private final Range<Integer> useTickRange;
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
        this.useTickRange = getUseTickRange(bakedParts);
        this.loadBlockBounds();
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

    public int getId() {
        return id;
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
        OpenMatrix4f matrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        OpenVoxelShape shape = getRenderShape(entity, model, itemStack, AbstractItemTransformType.NONE);
        if (rotation != null) {
            matrix.rotate(new OpenQuaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true));
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

    public OpenVoxelShape getRenderShape(Entity entity, Model model, ItemStack itemStack, AbstractItemTransformType transformType) {
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer != null) {
            return getRenderShape(entity, ModelHolder.ofNullable(model), itemStack, transformType, renderer);
        }
        return OpenVoxelShape.empty();
    }

    public <T extends Entity, V extends Model, M extends IModelHolder<V>> OpenVoxelShape getRenderShape(T entity, M model, ItemStack itemStack, AbstractItemTransformType transformType, SkinRenderer<T, V, M> renderer) {
        SkinRenderContext context = new SkinRenderContext(new PoseStack());
        context.setTransformType(transformType);
        context.setReference(0, itemStack);
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
        OpenVoxelShape shape1 = part.getRenderShape().copy();
        context.pushPose();
        renderer.apply(entity, model, part, this, context);
        shape1.mul(context.pose().lastPose());
        shape.add(shape1);
        for (BakedSkinPart childPart : part.getChildren()) {
            addRenderShape(shape, entity, model, childPart, renderer, context);
        }
        context.popPose();
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
            if (context.getTransformType() == AbstractItemTransformType.NONE) {
                return skinParts.size() == 1;
            }
            return false;
        }
        if (isArrowEntity(entity)) {
            return false; // arrow entity only render arrow part
        }
        if (partType instanceof ICanUse && entity instanceof LivingEntity) {
            int useTick = getUseTick((LivingEntity) entity, context.getReference());
            Range<Integer> useRange = ((ICanUse) partType).getUseRange();
            return useRange.contains(MathUtils.clamp(useTick, useTickRange.lowerEndpoint(), useTickRange.upperEndpoint()));
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
        // the item is using.
        if (entity.getUseItem() == itemStack) {
            return entity.getTicksUsingItem();
        }
        // this item is charged (only crossbow).
        if (CrossbowItem.isCharged(itemStack)) {
            return 100;
        }
        return 0;
    }

    private Range<Integer> getUseTickRange(ArrayList<BakedSkinPart> bakedParts) {
        int count = 0;
        int maxUseTick = Integer.MIN_VALUE;
        int minUseTick = Integer.MAX_VALUE;
        for (BakedSkinPart bakedPart : bakedParts) {
            ISkinPartType partType = bakedPart.getType();
            if (partType instanceof ICanUse) {
                Range<Integer> range = ((ICanUse) partType).getUseRange();
                maxUseTick = Math.max(maxUseTick, range.upperEndpoint());
                minUseTick = Math.min(minUseTick, range.lowerEndpoint());
                count += 1;
            }
        }
        if (count == 0) {
            return Range.closed(0, 0);
        }
        return Range.closed(minUseTick, maxUseTick);
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
