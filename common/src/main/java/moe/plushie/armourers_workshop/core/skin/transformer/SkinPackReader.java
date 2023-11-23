package moe.plushie.armourers_workshop.core.skin.transformer;

import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

public abstract class SkinPackReader {

    protected final String name;
    protected final Collection<IResource> resources;

    protected SkinPackReader(String name, Collection<IResource> resources) {
        this.name = name;
        this.resources = resources;
    }

    public abstract void loadEntityModel(IOConsumer<SkinPackModelReader> consumer) throws IOException;


    @Nullable
    public IResource findResource(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (IResource resource : resources) {
            if (pattern.matcher(resource.getName()).find()) {
                return resource;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public IResource getResource(String name) {
        for (IResource resource : resources) {
            if (resource.getName().equalsIgnoreCase(name)) {
                return resource;
            }
        }
        return null;
    }

    public Collection<IResource> getResources() {
        return resources;
    }

    public interface Factory {

        @Nullable
        SkinPackReader create(String name, Collection<IResource> resources) throws IOException;
    }
}
