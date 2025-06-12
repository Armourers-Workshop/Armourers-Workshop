package moe.plushie.armourers_workshop.api.core;

import java.io.IOException;
import java.io.InputStream;

public interface IResource {

    String name();

    String source();

    InputStream inputStream() throws IOException;
}
