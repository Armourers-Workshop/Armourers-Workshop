package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;

public interface DataPackLoader {

    default void begin(OpenResourceManager resourceManager) {
    }

    default void load(OpenResourceManager resourceManager) {
        var target = ModConstants.key(target());
        var extension = String.format(".%s", extension());
        resourceManager.listResources(target, s -> s.endsWith(extension), (key, resource) -> {
            var object = JsonSerializer.readFromResource(resource);
            if (object == null) {
                return;
            }
            var path1 = FileUtils.removeParentPath(key.path(), target() + "/");
            var key1 = key.withPath(FileUtils.removeExtension(path1));
            ModLog.debug("Found a entry '{}' in '{}'", key, resource.source());
            load(key1, object);
        });
    }

    default void end(OpenResourceManager resourceManager) {

    }

    void load(OpenResourceKey key, IODataObject object);

    String target();

    default String extension() {
        return "json";
    }

    default float priority() {
        return 0.0f;
    }
}
