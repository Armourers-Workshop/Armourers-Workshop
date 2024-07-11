package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModelPartCollector;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.LinkedHashMap;
import java.util.Map;

@Available("[1.18, )")
public class AbstractModelHolder {

    public static void collect(String root, Iterable<ModelPart> parts, Map<String, ModelPart> collector) {
        int i = 0;
        for (var part : parts) {
            var name = String.format("%s[%d]", root, i++);
            collect(name, part, collector);
        }
    }

    public static void collect(String root, Map<String, ModelPart> parts, Map<String, ModelPart> collector) {
        for (var entry : parts.entrySet()) {
            var name = entry.getKey();
            collect(name, entry.getValue(), collector);
        }
    }

    public static void collect(String name, ModelPart part, Map<String, ModelPart> collector) {
        collector.put(name, part);
        var provider = ObjectUtils.safeCast(part, IModelPartCollector.class);
        if (provider == null) {
            return;
        }
        var child = new LinkedHashMap<String, ModelPart>();
        provider.aw2$collect(child);
        child.forEach((key, value) -> collector.put(name + "." + key, value));
    }


    public static <T extends Model> void register(Class<T> clazz, Map<String, String> mapper) {
        ModelHolder.register(clazz, mapper);
    }
}
