package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.model.geom.ModelPart;

import java.util.LinkedHashMap;
import java.util.Map;

@Available("[16, )")
public interface AbstractModelCollector {

    void aw2$collect(Builder builder);

    class Builder {

        private final LinkedHashMap<String, ModelPart> results = new LinkedHashMap<>();

        public void put(String name, ModelPart part) {
            results.put(name, part);
            var collector = Objects.safeCast(part, AbstractModelCollector.class);
            if (collector == null) {
                return;
            }
            var childContainer = new Builder();
            collector.aw2$collect(childContainer);
            childContainer.results.forEach((key, value) -> results.put(name + "." + key, value));
        }

        public void put(String root, Iterable<ModelPart> parts) {
            int index = 0;
            for (var part : parts) {
                var name = String.format("%s[%d]", root, index++);
                put(name, part);
            }
        }

        public void putAll(Map<String, ModelPart> parts) {
            for (var entry : parts.entrySet()) {
                var name = entry.getKey();
                put(name, entry.getValue());
            }
        }

        public Map<String, ModelPart> build() {
            return results;
        }
    }
}
