package moe.plushie.armourers_workshop.utils;

import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModelPartCacher {

    private final HashMap<String, ModelPart> parts = new HashMap<>();

    public ModelPartCacher(Consumer<Map<String, ModelPart>> provider) {
        provider.accept(parts);
    }

    public ModelPart get(String name) {
        return parts.get(name);
    }

}
