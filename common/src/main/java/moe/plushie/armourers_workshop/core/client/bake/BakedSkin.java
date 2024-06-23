package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
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
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.MathUtils;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final int id = ThreadUtils.BAKED_SKIN_COUNTER.incrementAndGet();

    private final String identifier;
    private final Skin skin;
    private final ISkinType skinType;
    private final HashMap<Object, Rectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<BlockPos, Rectangle3i> cachedBlockBounds = new HashMap<>();

    private final ArrayList<SkinWingsTransform> cachedWingsTransforms = new ArrayList<>();
    private final ArrayList<BakedItemTransform> cachedItemTransforms = new ArrayList<>();

    private final Range<Integer> useTickRange;
    private final List<BakedSkinPart> skinParts;
    private final List<BakedSkinAnimation> skinAnimations;

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;

    private final ColorScheme colorScheme;
    private final BakedItemModel resolvedItemModel;
    private final HashMap<Integer, ColorScheme> resolvedColorSchemes = new HashMap<>();

    public BakedSkin(String identifier, ISkinType skinType, ArrayList<BakedSkinPart> bakedParts, Skin skin, ColorScheme colorScheme, ColorDescriptor colorDescriptor, SkinUsedCounter usedCounter) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinType = skinType;
        this.skinAnimations = resolveAnimations(bakedParts, skin.getAnimations());
        this.skinParts = BakedSkinPartCombiner.apply(bakedParts);
        this.colorScheme = colorScheme;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.useTickRange = getUseTickRange(skinParts);
        this.resolvedItemModel = resolveItemModel(skinParts, skin.getItemTransforms());
        this.loadBlockBounds(skinParts);
        this.loadPartTransforms(skinParts);
    }

    public void setupAnim(Entity entity, SkinRenderContext context) {
        cachedItemTransforms.forEach(it -> it.setup(entity, context.getReferenced()));
        cachedWingsTransforms.forEach(it -> it.setup(entity, context.getAnimationTicks()));
        skinAnimations.forEach(it -> it.setup(entity, context));
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

    public List<BakedSkinAnimation> getAnimations() {
        return skinAnimations;
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
            key.release();
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
        cachedBounds.put(key, bounds);
        return bounds;
    }

    public OpenVoxelShape getRenderShape(Entity entity, BakedArmature armature, SkinItemSource itemSource) {
        if (armature == null) {
            return OpenVoxelShape.empty();
        }
        var context = new SkinRenderContext();
        context.setReferenced(itemSource);
        context.setTransformType(itemSource.getTransformType());
        context.setAnimationTicks(0);
        //context.setTransforms(entity, model);
        setupAnim(entity, context);
        return SkinRenderer.getShape(entity, armature, this, context);
    }


    public <T extends Entity> boolean shouldRenderPart(T entity, BakedSkinPart bakedPart, SkinRenderContext context) {
        var partType = bakedPart.getType();
        // hook part only render in hook entity.
        if (partType == SkinPartTypes.ITEM_FISHING_HOOK) {
            return isHookEntity(entity);
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD1) {
            return entity instanceof Player player && player.fishing != null;
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD) {
            if (isHookEntity(entity)) {
                return false;
            }
            return !(entity instanceof Player player && player.fishing != null);
        }
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
        if (isHookEntity(entity)) {
            return false; // hook entity only render arrow part.
        }
        if (partType instanceof ICanUse canUse && entity instanceof LivingEntity livingEntity) {
            var useTick = getUseTick(livingEntity, context.getReferenced().getItem());
            var useRange = canUse.getUseRange();
            return useRange.contains(MathUtils.clamp(useTick, useTickRange.lowerEndpoint(), useTickRange.upperEndpoint()));
        }
        return true;
    }

    private boolean isHookEntity(Entity entity) {
        return entity instanceof FishingHook;
    }

    private boolean isArrowEntity(Entity entity) {
        // in vanilla considers trident to be a special arrow,
        // but this no fits we definition of arrow skin.
        if (entity instanceof ThrownTrident) {
            return false;
        }
        return entity instanceof AbstractArrow;
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

    private List<BakedSkinAnimation> resolveAnimations(List<BakedSkinPart> skinParts, Collection<SkinAnimation> animations) {
        var results = new ArrayList<BakedSkinAnimation>();
        if (animations.isEmpty()) {
            return results;
        }
        var namedParts = new HashMap<String, List<BakedSkinPart>>();
        ObjectUtils.search(skinParts, BakedSkinPart::getChildren, part -> {
            var partType = part.getType();
            var partName = partType.getName();
            if (partType == SkinPartTypes.ADVANCED) {
                partName = part.getName();
            }
            namedParts.computeIfAbsent(partName, it -> new ArrayList<>()).add(part);
        });
        animations.forEach(animation -> {
            var bakedAnimation = new BakedSkinAnimation(animation);
            bakedAnimation.link(namedParts);
            results.add(bakedAnimation);
        });
        results.removeIf(BakedSkinAnimation::isEmpty);
        return results;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedSkin that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
