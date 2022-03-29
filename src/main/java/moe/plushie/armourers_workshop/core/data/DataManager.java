package moe.plushie.armourers_workshop.core.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;

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


    public Optional<ByteBuf> loadSkinData(String identifier) {
        ModLog.debug("Load skin data: {} ", identifier);
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

    public void loadSkinData(String identifier, Consumer<Optional<ByteBuf>> consumer) {
        executorService.submit(() -> consumer.accept(loadSkinData(identifier)));
    }

    @Nullable
    private InputStream loadStreamFromPath(String identifier) {
        String ext = ".armour";
        if (!identifier.endsWith(ext)) {
            identifier = identifier + ext;
        }
        File file = new File(AWCore.getSkinLibraryDirectory(), identifier);
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


