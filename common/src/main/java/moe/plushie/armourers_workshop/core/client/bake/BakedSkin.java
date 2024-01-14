package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinWingsTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

import manifold.ext.rt.api.auto;

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

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;

    private final ColorScheme colorScheme;
    private final BakedItemModel resolvedItemModel;
    private final HashMap<Integer, ColorScheme> resolvedColorSchemes = new HashMap<>();

    public BakedSkin(String identifier, ISkinType skinType, ArrayList<BakedSkinPart> bakedParts, Skin skin, ColorScheme colorScheme, ColorDescriptor colorDescriptor, SkinUsedCounter usedCounter) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinType = skinType;
        this.skinParts = bakedParts;
        this.colorScheme = colorScheme;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.useTickRange = getUseTickRange(bakedParts);
        this.resolvedItemModel = resolveItemModel(skin.getItemTransforms());
        this.loadBlockBounds();
        this.loadPartTransforms();
    }

    public void setupAnim(Entity entity, float partialTicks, SkinItemSource itemSource) {
        cachedItemTransforms.forEach(it -> it.setup(entity, itemSource));
        cachedWingsTransforms.forEach(it -> it.setup(entity, partialTicks));
    }

    public ColorScheme resolve(Entity entity, ColorScheme scheme) {
        if (colorDescriptor.isEmpty()) {
            return ColorScheme.EMPTY;
        }
        ColorScheme resolvedColorScheme = resolvedColorSchemes.computeIfAbsent(entity.getId(), k -> colorScheme.copy());
        // we can't bind textures to skin when the item stack rendering.
        if (PlaceholderManager.isPlaceholder(entity)) {
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
        Vector3f rotation = itemSource.getRotation();
        Object key = SkinCache.borrowKey(rotation, itemSource.getTransformType());
        Rectangle3f bounds = cachedBounds.get(rotation);
        if (bounds != null) {
            SkinCache.returnKey(key);
            return bounds;
        }
        Entity entity = PlaceholderManager.MANNEQUIN.get();
        OpenMatrix4f matrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        OpenVoxelShape shape = getRenderShape(entity, BakedArmature.defaultBy(skinType), itemSource);
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

    public OpenVoxelShape getRenderShape(Entity entity, BakedArmature armature, SkinItemSource itemSource) {
        if (armature == null) {
            return OpenVoxelShape.empty();
        }

        SkinRenderContext context = new SkinRenderContext(new PoseStack());
        context.setReferenced(itemSource);
        context.setTransformType(itemSource.getTransformType());
        //context.setTransforms(entity, model);
        setupAnim(entity, context.getPartialTicks(), context.getReferenced());
        return SkinRenderer.getShape(entity, armature, this, context);
    }


    public <T extends Entity> boolean shouldRenderPart(T entity, BakedSkinPart bakedPart, SkinRenderContext context) {
        ISkinPartType partType = bakedPart.getType();
        // hook part only render in hook entity.
        if (partType == SkinPartTypes.ITEM_FISHING_HOOK) {
            return isHookEntity(entity);
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD1) {
            Player player = ObjectUtils.safeCast(entity, Player.class);
            return player != null && player.fishing != null;
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD) {
            if (isHookEntity(entity)) {
                return false;
            }
            Player player = ObjectUtils.safeCast(entity, Player.class);
            return player == null || player.fishing == null;
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
        if (partType instanceof ICanUse && entity instanceof LivingEntity) {
            int useTick = getUseTick((LivingEntity) entity, context.getReferenced().getItem());
            auto useRange = ((ICanUse) partType).getUseRange();
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

    private void loadPartTransforms() {
        // attach item transform
        skinParts.forEach(part -> {
            if (part.getType() instanceof ICanHeld) {
                BakedItemTransform transform = new BakedItemTransform(part, this);
                part.getTransform().addTransform(transform);
            }
        });
        // search all transform
        skinParts.forEach(it -> it.getTransform().forEach(transform -> {
            if (transform instanceof SkinWingsTransform) {
                cachedWingsTransforms.add((SkinWingsTransform) transform);
            }
            if (transform instanceof BakedItemTransform) {
                cachedItemTransforms.add((BakedItemTransform) transform);
            }
        }));
    }

    private void loadBlockBounds() {
        if (skinType != SkinTypes.BLOCK) {
            return;
        }
        for (BakedSkinPart skinPart : skinParts) {
            auto bm = skinPart.getPart().getBlockBounds();
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


    private BakedItemModel resolveItemModel(SkinItemTransforms oldValue) {
        // we only convert transform when override item transforms is enabled.
        if (oldValue != null) {
            return BakedItemModel.from(resolveItemOverrides(), oldValue, false);
        }
        return null;
    }

    private Collection<String> resolveItemOverrides() {
        ArrayList<String> overrides = new ArrayList<>();
        for (BakedSkinPart part : skinParts) {
            overrides.addAll(SkinUtils.getItemOverrides(part.getType()));
            // we need search child part?
        }
        return overrides;
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
