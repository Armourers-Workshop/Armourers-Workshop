package moe.plushie.armourers_workshop.core.utils;

import java.io.IOException;
import java.io.InputStream;

public interface OpenResource {

    String name();

    String source();

    InputStream inputStream() throws IOException;
}
