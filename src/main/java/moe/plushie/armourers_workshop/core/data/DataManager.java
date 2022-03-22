package moe.plushie.armourers_workshop.core.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWLog;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    public static DataManager getInstance() {
        return INSTANCE;
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(1);


    public Optional<ByteBuf> loadSkinData(SkinDescriptor descriptor) {
        AWLog.debug("Load skin data: {} ", descriptor);
        String identifier = descriptor.getIdentifier();
        InputStream stream = null;
        if (identifier.startsWith("db:")) {
            stream = LocalDataService.getInstance().getFile(identifier.substring(3));
        } else {
            stream = loadStreamFromPath(identifier);
        }
        if (stream == null) {
            return Optional.empty();
        }
        try {
            int size = stream.available();
            ByteBuf buf = Unpooled.buffer(size);
            buf.writeBytes(stream, size);
            buf.resetReaderIndex();
            return Optional.of(buf);

        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public void loadSkinData(SkinDescriptor descriptor, Consumer<Optional<ByteBuf>> consumer) {
        executorService.submit(() -> consumer.accept(loadSkinData(descriptor)));
    }

    @Nullable
    private InputStream loadStreamFromPath(String identifier) {
        String ext = ".armour";
        if (!identifier.endsWith(ext)) {
            identifier = identifier + ext;
        }
        File rootFile = new File(AWCore.getRootDirectory(), "skin-library");
        File file = new File(rootFile, identifier);
        if (!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}


