package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SkinPaintScheme implements IDataSerializable.Immutable {

    public final static SkinPaintScheme EMPTY = new SkinPaintScheme();

    public static final IDataCodec<SkinPaintScheme> CODEC = IDataCodec.COMPOUND_TAG.serializer(SkinPaintScheme::new);

    private final HashMap<SkinPaintType, SkinPaintColor> colors = new HashMap<>();
    private HashMap<SkinPaintType, SkinPaintColor> resolvedColors;

    private SkinPaintScheme reference;
    private OpenResourceLocation texture;

    private int hashCode;

    public SkinPaintScheme() {
    }

    public SkinPaintScheme(IDataSerializer serializer) {
        for (var entry : CodingKeys.KEYS.entrySet()) {
            var value = serializer.read(entry.getValue());
            if (value != null) {
                colors.put(entry.getKey(), value);
            }
        }
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        for (var entry : CodingKeys.KEYS.entrySet()) {
            var value = colors.get(entry.getKey());
            if (value != null) {
                serializer.write(entry.getValue(), value);
            }
        }
    }

    public SkinPaintScheme copy() {
        var scheme = new SkinPaintScheme();
        scheme.colors.putAll(colors);
        scheme.reference = reference;
        scheme.texture = texture;
        return scheme;
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (reference != null && !reference.isEmpty()) {
            return false;
        }
        if (texture != null) {
            return false;
        }
        return colors.isEmpty();
    }

    @Nullable
    public SkinPaintColor getColor(SkinPaintType paintType) {
        var color = colors.get(paintType);
        if (color != null) {
            return color;
        }
        if (reference != null) {
            return reference.getColor(paintType);
        }
        return null;
    }

    public void setColor(SkinPaintType paintType, SkinPaintColor color) {
        colors.put(paintType, color);
        resolvedColors = null;
        hashCode = 0;
    }

    public SkinPaintColor getResolvedColor(SkinPaintType paintType) {
        if (resolvedColors == null) {
            resolvedColors = resolvedColors();
        }
        return resolvedColors.get(paintType);
    }

    public OpenResourceLocation texture() {
        return texture;
    }

    public void setTexture(OpenResourceLocation texture) {
        this.texture = texture;
    }

    public SkinPaintScheme reference() {
        if (reference != null) {
            return reference;
        }
        return SkinPaintScheme.EMPTY;
    }

    public void setReference(SkinPaintScheme reference) {
        // referring empty scheme not have any effect.
        if (reference != null && reference.isEmpty()) {
            reference = null;
        }
        if (!Objects.equals(this.reference, reference)) {
            this.reference = reference;
            this.resolvedColors = null;
            this.hashCode = 0;
        }
    }

    private HashMap<SkinPaintType, SkinPaintColor> resolvedColors() {
        var resolvedColors = new HashMap<SkinPaintType, SkinPaintColor>();
        var dependencies = new HashMap<SkinPaintType, ArrayList<SkinPaintType>>();
        // build all reference dependencies
        if (reference != null) {
            resolvedColors.putAll(reference.resolvedColors());
        }
        // build all item dependencies
        Collections.concat(colors.entrySet(), reference().colors.entrySet()).forEach(e -> {
            var paintType = e.getKey();
            var color = e.getValue();
            if (color.paintType().dyeType() != null) {
                dependencies.computeIfAbsent(color.paintType(), k -> new ArrayList<>()).add(paintType);
            } else {
                resolvedColors.put(paintType, color);
            }
        });
        if (resolvedColors.isEmpty()) {
            return resolvedColors;
        }
        // merge all items whens dependencies
        dependencies.forEach((key, value) -> find(dependencies.values(), v -> v.contains(key)).ifPresent(target -> {
            if (target != value) {
                target.addAll(value);
            }
            value.clear(); // clear to prevent infinite loop occurs
        }));
        dependencies.forEach((key, value) -> value.forEach(paintType -> resolvedColors.put(paintType, resolvedColors.get(key))));
        return resolvedColors;
    }

    private <T> Optional<T> find(Collection<T> values, Predicate<T> predicate) {
        for (var value : values) {
            if (predicate.test(value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinPaintScheme that)) return false;
        return colors.equals(that.colors) && Objects.equals(texture, that.texture) && Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(colors, texture, reference);
            if (hashCode == 0) {
                hashCode = ~hashCode;
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "[" + resolvedColors() + "]";
    }

    private static class CodingKeys {
        public static final Map<SkinPaintType, IDataSerializerKey<SkinPaintColor>> KEYS = Collections.immutableMap(it -> {
            for (var paintType : SkinPaintTypes.values()) {
                if (paintType != SkinPaintTypes.NONE) {
                    var name = paintType.registryName().toString();
                    var key = IDataSerializerKey.create(name, SkinPaintColor.CODEC, null);
                    it.put(paintType, key);
                }
            }
        });
    }
}
