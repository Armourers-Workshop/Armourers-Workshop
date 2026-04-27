package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class SkinItemModel {

    private final OpenResourceKey name;
    private final Map<OpenItemDisplayContext, OpenItemTransform> transforms;

    private final SkinItemProperty[] properties;
    private final List<Matcher> matchers = new ArrayList<>();

    public SkinItemModel(OpenResourceKey name, List<SkinItemOverride> overrides, Map<OpenItemDisplayContext, OpenItemTransform> transforms) {
        this.name = name;
        this.transforms = transforms;
        // bake
        var indexedProperties = new ArrayList<SkinItemProperty>();
        for (var override : overrides) {
            var childTester = new ArrayList<Predicate<float[]>>();
            var childProperties = override.properties();
            var childValues = override.values();
            for (int i = 0; i < childProperties.length; ++i) {
                var childProperty = childProperties[i];
                var childValue = childValues[i];
                int idx = indexedProperties.indexOf(childProperty);
                if (idx == -1) {
                    idx = indexedProperties.size();
                    indexedProperties.add(childProperty);
                }
                var index = idx;
                childTester.add(result -> result[index] >= childValue);
            }
            this.matchers.add(new Matcher(override, childTester));
        }
        this.properties = indexedProperties.toArray(new SkinItemProperty[0]);
    }

    public SkinItemModel resolve(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags, OpenItemDisplayContext displayContext) {
        int length = properties.length;
        if (length == 0) {
            return this;
        }
        // evaluate all properties.
        var results = new float[length];
        for (int i = 0; i < length; ++i) {
            results[i] = properties[i].call(itemStack, entity, level, flags, displayContext);
        }
        // test all properties
        for (var matcher : matchers) {
            if (matcher.test(results)) {
                var model = matcher.override.model();
                if (model != null) {
                    return model;
                }
                return this;
            }
        }
        return this;
    }

    public OpenItemTransform getTransform(OpenItemDisplayContext transformType) {
        return transforms.getOrDefault(transformType, OpenItemTransform.NO_TRANSFORM);
    }

    public OpenResourceKey name() {
        return name;
    }

    private static class Matcher {

        private final List<Predicate<float[]>> testers;
        private final SkinItemOverride override;

        public Matcher(SkinItemOverride override, List<Predicate<float[]>> tester) {
            this.testers = tester;
            this.override = override;
        }

        public boolean test(float[] results) {
            for (var tester : testers) {
                if (!tester.test(results)) {
                    return false;
                }
            }
            return true;
        }
    }
}
