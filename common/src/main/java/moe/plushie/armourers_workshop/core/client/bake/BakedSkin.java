package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.skin.part.features.ICanUse;
import moe.plushie.armourers_workshop.core.client.animation.AnimatedTransform;
import moe.plushie.armourers_workshop.core.client.animation.AnimationController;
import moe.plushie.armourers_workshop.core.client.model.SkinItemTransform;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.texture.EntityTextureLoader;
import moe.plushie.armourers_workshop.core.data.cache.PrimaryKey;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.part.wings.WingPartTransform;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BakedSkin {

    private final int id = OpenRandomSource.nextInt(BakedSkin.class);

    private final String identifier;
    private final Skin skin;
    private final SkinType skinType;
    private final HashMap<Object, OpenRectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<OpenVector3i, OpenRectangle3f> cachedBlockBounds = new HashMap<>();

    private final Range<Integer> useTickRange;
    private final List<BakedSkinPart> skinParts;

    private final List<AnimationController> animationControllers;

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;
    private final BakedRenderInfo renderInfo;

    private final BakedItemTransform itemTransform;

    private final SkinPaintScheme paintScheme;
    private final Int2ObjectMap<SkinPaintScheme> resolvedColorSchemes = new Int2ObjectOpenHashMap<>();

    private final BakedSkinAnimationHandler animationHandler = new BakedSkinAnimationHandler();

    public BakedSkin(String identifier, SkinType skinType, ArrayList<BakedSkinPart> bakedParts, Skin skin, SkinPaintScheme paintScheme, ColorDescriptor colorDescriptor, BakedRenderInfo renderInfo, SkinUsedCounter usedCounter) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinType = skinType;
        this.animationControllers = resolveAnimationControllers(bakedParts, skin.animations(), skin.properties());
        this.skinParts = BakedSkinPartCombiner.apply(bakedParts); // depends `resolveAnimationControllers`
        this.paintScheme = paintScheme;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.renderInfo = renderInfo;
        this.useTickRange = getUseTickRange(skinParts);
        this.itemTransform = resolvedItemTransform(skinParts, skin);
        this.loadBlockBounds(skinParts);
        this.loadPartTransforms(skinParts);
    }

    public void setupAnim(Entity entity, BakedArmature bakedArmature, SkinRenderContext context) {
        animationHandler.apply(this, entity, bakedArmature, context);
    }

    public SkinPaintScheme resolve(Entity entity, SkinPaintScheme scheme) {
        if (colorDescriptor.isEmpty()) {
            return SkinPaintScheme.EMPTY;
        }
        var resolvedColorScheme = resolvedColorSchemes.computeIfAbsent(entity.getId(), k -> paintScheme.copy());
        // we can't bind textures to skin when the item stack rendering.
        if (PlaceholderManager.isPlaceholder(entity)) {
            var resolvedTexture = EntityTextureLoader.getInstance().getTextureLocation(entity);
            if (!Objects.equals(resolvedColorScheme.texture(), resolvedTexture)) {
                resolvedColorScheme.setTexture(resolvedTexture);
            }
        }
        resolvedColorScheme.setReference(scheme);
        return resolvedColorScheme;
    }

    public int id() {
        return id;
    }

    public String identifier() {
        return identifier;
    }

    public Skin skin() {
        return skin;
    }

    public SkinType type() {
        return skinType;
    }

    public List<BakedSkinPart> parts() {
        return skinParts;
    }

    public SkinProperties properties() {
        return skin.properties();
    }

    public List<AnimationController> animationControllers() {
        return animationControllers;
    }

    public SkinPaintScheme paintScheme() {
        return paintScheme;
    }

    public ColorDescriptor colorDescriptor() {
        return colorDescriptor;
    }

    public BakedItemTransform itemTransform() {
        return itemTransform;
    }

    public Range<Integer> useTickRange() {
        return useTickRange;
    }

    public SkinUsedCounter usedCounter() {
        return usedCounter;
    }

    public BakedRenderInfo renderInfo() {
        return renderInfo;
    }

    public Map<OpenVector3i, OpenRectangle3f> blockBounds() {
        return cachedBlockBounds;
    }

    public OpenRectangle3f renderBounds() {
        return getRenderBounds(SkinItemTransform.NO_TRANSFORM, OpenItemDisplayContext.NONE);
    }

    public OpenRectangle3f getRenderBounds(SkinItemTransform itemTransform, OpenItemDisplayContext displayContext) {
        var rotation = itemTransform.rotation();
        var key = PrimaryKey.of(rotation, displayContext);
        var bounds = cachedBounds.get(key);
        if (bounds != null) {
            return bounds;
        }
        var entity = PlaceholderManager.MANNEQUIN.get();
        var matrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        var shape = getRenderShape(entity, BakedArmature.defaultBy(skinType), displayContext);
        if (!rotation.equals(OpenVector3f.ZERO)) {
            matrix.rotate(new OpenQuaternionf(rotation.x(), rotation.y(), rotation.z(), true));
            shape.mul(matrix);
        }
        bounds = shape.bounds().copy();
        if (!rotation.equals(OpenVector3f.ZERO)) {
            var center = new OpenVector4f(bounds.center());
            matrix.invert();
            center.transform(matrix);
            bounds.setX(center.x() - bounds.width() / 2);
            bounds.setY(center.y() - bounds.height() / 2);
            bounds.setZ(center.z() - bounds.depth() / 2);
        }
        cachedBounds.put(key.copy(), bounds);
        return bounds;
    }

    private OpenVoxelShape getRenderShape(Entity entity, BakedArmature armature, OpenItemDisplayContext displayContext) {
        if (armature == null) {
            return OpenVoxelShape.empty();
        }
        var context = new SkinRenderContext();
        context.setItemSource(SkinItemSource.EMPTY);
        context.setDisplayContext(displayContext);
        context.setAnimationTicks(0);
        //context.setTransforms(entity, model);
        setupAnim(entity, armature, context);
        return SkinRenderer.getShape(entity, armature, this, context.pose());
    }

    private void loadPartTransforms(List<BakedSkinPart> skinParts) {
        // search all requires adapt mode parts, and then insert a adapter transform.
        Collections.eachTree(skinParts, BakedSkinPart::children, part -> {
            if (part.properties().get(SkinProperty.USE_ADAPT_MODE)) {
                var adapterTransform = new BakedAdapterJointTransform(part);
                part.setJointTransformModifier(it -> adapterTransform);
                animationHandler.normal((skin, entity, armature, context) -> adapterTransform.setup(entity, armature, context));
            }
        });
        // search all animated transform, we need to reset it before setup.
        Collections.eachTree(skinParts, BakedSkinPart::children, part -> part.transform().children().forEach(transform -> {
            if (transform instanceof AnimatedTransform animatedTransform) {
                animationHandler.lowest((skin, entity, armature, context) -> animatedTransform.reset());
            }
        }));
        // search all wings transform.
        skinParts.forEach(it -> it.transform().children().forEach(transform -> {
            if (transform instanceof WingPartTransform wingTransform) {
                animationHandler.normal((skin, entity, armature, context) -> wingTransform.setup(entity, context.animationTicks()));
            }
        }));
        // search all locator part, and then a attachment transform.
        BakedAttachmentPartTransform.create(skinParts).forEach(attachmentTransform -> {
            animationHandler.normal((skin, entity, armature, context) -> attachmentTransform.setup(entity, armature, context));
        });
        // search all backpack part, and then attach a backpack part transform.
        Collections.filter(skinParts, it -> it.type() == SkinPartTypes.ITEM_BACKPACK).forEach(it -> {
            var backpackTransform = new BakedBackpackPartTransform();
            it.transform().insertChild(backpackTransform, 0);
            animationHandler.highest((skin, entity, armature, context) -> backpackTransform.setup(entity, context.renderData()));
        });
    }

    private void loadBlockBounds(List<BakedSkinPart> skinParts) {
        if (skinType != SkinTypes.BLOCK) {
            return;
        }
        for (var skinPart : skinParts) {
            var bounds = skinPart.part().blockBounds();
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
            if (bakedPart.type() instanceof ICanUse partType) {
                var range = partType.useRange();
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

    private BakedItemTransform resolvedItemTransform(List<BakedSkinPart> skinParts, Skin skin) {
        var itemTransforms = skin.itemTransforms();
        return BakedItemTransform.create(skinParts, itemTransforms, skin.type());
    }

    private List<AnimationController> resolveAnimationControllers(List<BakedSkinPart> skinParts, Collection<SkinAnimation> animations, SkinProperties properties) {
        // create animation controller by animation.
        var animationControllers = new ArrayList<AnimationController>();
        if (animations.isEmpty()) {
            return animationControllers;
        }
        var namedParts = new HashMap<String, SkinPartTransform>();
        Collections.eachTree(skinParts, BakedSkinPart::children, part -> {
            var partName = part.name();
            if (partName.isEmpty()) {
                partName = part.type().name();
            }
            namedParts.put(partName, part.transform());
        });
        animations.forEach(animation -> {
            var controller = new AnimationController(animation, namedParts);
            animationControllers.add(controller);
        });
        animationControllers.removeIf(AnimationController::isEmpty);
        return animationControllers;
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

    @Override
    public String toString() {
        return Objects.toString(this, "id", id, "skin", identifier, "type", skinType.registryName().toString());
    }
}
