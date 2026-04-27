package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.Util;

import java.io.File;
import java.net.URI;
import java.util.concurrent.Executor;

@Available("[16, 26)")
public class AbstractPlatform {

    public static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;

    public static Executor backgroundExecutor() {
        return Util.backgroundExecutor();
    }

    public static void openFile(File file) {
        Util.getPlatform().openFile(file);
    }

    public static void openUri(URI uri) {
        Util.getPlatform().openUri(uri);
    }
}
