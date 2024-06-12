package moe.plushie.armourers_workshop.api.core;

import java.io.IOException;
import java.io.InputStream;

public interface IResource {

    String getName();

    String getSource();

    InputStream getInputStream() throws IOException;
}
