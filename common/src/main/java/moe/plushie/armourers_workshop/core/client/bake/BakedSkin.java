package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.animation.AnimationContext;
import moe.plushie.armourers_workshop.core.client.animation.AnimationController;
import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.data.cache.PrimaryKey;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinWingsTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final int id = ThreadUtils.nextId(BakedSkinPart.class);

    private final String identifier;
    private final Skin skin;
    private final ISkinType skinType;
    private final HashMap<Object, Rectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<BlockPos, Rectangle3i> cachedBlockBounds = new HashMap<>();

    private final ArrayList<SkinWingsTransform> cachedWingsTransforms = new ArrayList<>();
    private final ArrayList<BakedItemTransform> cachedItemTransforms = new ArrayList<>();

    private final Range<Integer> useTickRange;
    private final List<BakedSkinPart> skinParts;

    private final AnimationContext skinAnimationContext;
    private final List<AnimationController> skinAnimationControllers;

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;

    private final ColorScheme colorScheme;
    private final BakedItemModel resolvedItemModel;
    private final Int2ObjectMap<ColorScheme> resolvedColorSchemes = new Int2ObjectOpenHashMap<>();

    public BakedSkin(String identifier, ISkinType skinType, ArrayList<BakedSkinPart> bakedParts, Skin skin, ColorScheme colorScheme, ColorDescriptor colorDescriptor, SkinUsedCounter usedCounter) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinType = skinType;
        this.skinAnimationControllers = resolveAnimationControllers(bakedParts, skin.getAnimations());
        this.skinAnimationContext = AnimationContext.from(bakedParts);
        this.skinParts = BakedSkinPartCombiner.apply(bakedParts); // depends `resolveAnimationControllers`
        this.colorScheme = colorScheme;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.useTickRange = getUseTickRange(skinParts);
        this.resolvedItemModel = resolveItemModel(skinParts, skin.getItemTransforms());
        this.loadBlockBounds(skinParts);
        this.loadPartTransforms(skinParts);
    }

    public void setupAnim(Entity entity, BakedArmature bakedArmature, SkinRenderContext context) {
        cachedItemTransforms.forEach(it -> it.setup(entity, context.getItemSource()));
        cachedWingsTransforms.forEach(it -> it.setup(entity, context.getAnimationTicks()));
        AnimationEngine.apply(entity, this, context.getAnimationTicks(), context.getAnimationManager().getAnimationContext(this));
        SkinRenderHelper.apply(entity, this, bakedArmature, context.getItemSource());
    }

    public ColorScheme resolve(Entity entity, ColorScheme scheme) {
        if (colorDescriptor.isEmpty()) {
            return ColorScheme.EMPTY;
        }
        var resolvedColorScheme = resolvedColorSchemes.computeIfAbsent(entity.getId(), k -> colorScheme.copy());
        // we can't bind textures to skin when the item stack rendering.
        if (PlaceholderManager.isPlaceholder(entity)) {
            var resolvedTexture = PlayerTextureLoader.getInstance().getTextureLocation(entity);
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

    public String getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Skin getSkin() {
        return skin;
    }

    public ISkinType getType() {
        return skinType;
    }

    public List<BakedSkinPart> getParts() {
        return skinParts;
    }

    public SkinProperties getProperties() {
        return skin.getProperties();
    }

    public AnimationContext getAnimationContext() {
        return skinAnimationContext;
    }

    public List<AnimationController> getAnimationControllers() {
        return skinAnimationControllers;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public ColorDescriptor getColorDescriptor() {
        return colorDescriptor;
    }

    @Nullable
    public BakedItemModel getItemModel() {
        return resolvedItemModel;
    }

    public Range<Integer> getUseTickRange() {
        return useTickRange;
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        return cachedBlockBounds;
    }

    public Rectangle3f getRenderBounds(SkinItemSource itemSource) {
        var rotation = itemSource.getRotation();
        var key = PrimaryKey.of(rotation, itemSource.getTransformType());
        var bounds = cachedBounds.get(key);
        if (bounds != null) {
            return bounds;
        }
        var entity = PlaceholderManager.MANNEQUIN.get();
        var matrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        var shape = getRenderShape(entity, BakedArmature.defaultBy(skinType), itemSource);
        if (rotation != null) {
            matrix.rotate(new OpenQuaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true));
            shape.mul(matrix);
        }
        bounds = shape.bounds().copy();
        if (rotation != null) {
            var center = new Vector4f(bounds.getCenter());
            matrix.invert();
            center.transform(matrix);
            bounds.setX(center.x() - bounds.getWidth() / 2);
            bounds.setY(center.y() - bounds.getHeight() / 2);
            bounds.setZ(center.z() - bounds.getDepth() / 2);
        }
        cachedBounds.put(key.copy(), bounds);
        return bounds;
    }

    private OpenVoxelShape getRenderShape(Entity entity, BakedArmature armature, SkinItemSource itemSource) {
        if (armature == null) {
            return OpenVoxelShape.empty();
        }
        var context = new SkinRenderContext();
        context.setItemSource(itemSource);
        context.setTransformType(itemSource.getTransformType());
        context.setAnimationTicks(0);
        //context.setTransforms(entity, model);
        setupAnim(entity, armature, context);
        return SkinRenderer.getShape(entity, armature, this, context.pose());
    }

    private void loadPartTransforms(List<BakedSkinPart> skinParts) {
        // attach item transform.
        skinParts.forEach(part -> {
            var transform = part.getTransform();
            if (part.getType() instanceof ICanHeld) {
                transform.insertChild(new BakedItemTransform(part, this), 0);
            }
        });
        // search all transform
        skinParts.forEach(it -> it.getTransform().getChildren().forEach(transform -> {
            if (transform instanceof SkinWingsTransform transform1) {
                cachedWingsTransforms.add(transform1);
            }
            if (transform instanceof BakedItemTransform transform1) {
                cachedItemTransforms.add(transform1);
            }
        }));
    }

    private void loadBlockBounds(List<BakedSkinPart> skinParts) {
        if (skinType != SkinTypes.BLOCK) {
            return;
        }
        for (var skinPart : skinParts) {
            var bounds = skinPart.getPart().getBlockBounds();
            if (bounds != null) {
                cachedBlockBounds.putAll(bounds);
            }
        }
    }

    private Range<Integer> getUseTickRange(List<BakedSkinPart> skinParts) {
        int count = 0;
        int maxUseTick = Integer.MIN_VALUE;
        int minUseTick = Integer.MAX_VALUE;
        for (var bakedPart : skinParts) {
            if (bakedPart.getType() instanceof ICanUse partType) {
                var range = partType.getUseRange();
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

    private BakedItemModel resolveItemModel(List<BakedSkinPart> skinParts, SkinItemTransforms oldValue) {
        // we only convert transform when override item transforms is enabled.
        if (oldValue != null) {
            return BakedItemModel.from(resolveItemOverrides(skinParts), oldValue, false);
        }
        return null;
    }

    private List<String> resolveItemOverrides(List<BakedSkinPart> skinParts) {
        var overrides = new ArrayList<String>();
        for (var part : skinParts) {
            overrides.addAll(SkinUtils.getItemOverrides(part.getType()));
            // we need search child part?
        }
        return overrides;
    }

    private List<AnimationController> resolveAnimationControllers(List<BakedSkinPart> skinParts, Collection<SkinAnimation> animations) {
        var results = new ArrayList<AnimationController>();
        if (animations.isEmpty()) {
            return results;
        }
        var namedParts = new HashMap<String, BakedSkinPart>();
        ObjectUtils.search(skinParts, BakedSkinPart::getChildren, part -> {
            var partType = part.getType();
            var partName = partType.getName();
            if (partType == SkinPartTypes.ADVANCED) {
                partName = part.getName();
            }
            namedParts.put(partName, part);
        });
        animations.forEach(animation -> {
            var controller = new AnimationController(animation, namedParts);
            results.add(controller);
        });
        results.removeIf(AnimationController::isEmpty);
        return results;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedSkin that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
