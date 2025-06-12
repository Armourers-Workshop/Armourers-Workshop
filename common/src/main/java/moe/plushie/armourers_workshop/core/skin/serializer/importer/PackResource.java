package moe.plushie.armourers_workshop.core.skin.serializer.importer;

import java.io.IOException;
import java.io.InputStream;

public abstract class PackResource {

    public abstract String name();

    public abstract InputStream inputStream() throws IOException;
}
