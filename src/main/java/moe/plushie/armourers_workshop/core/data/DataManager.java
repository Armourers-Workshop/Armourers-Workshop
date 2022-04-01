package moe.plushie.armourers_workshop.core.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();

    public static DataManager getInstance() {
        return INSTANCE;
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(1);


    public Optional<ByteBuf> loadSkinData(String identifier) {
        ModLog.debug("Load skin data: {} ", identifier);
        InputStream stream;
        if (identifier.startsWith(AWConstants.Namespace.DATABASE + ":")) {
            stream = LocalDataService.getInstance().getFile(identifier.substring(3));
        } else {
            String path = identifier;
            if (path.startsWith(AWConstants.Namespace.SERVER + ":") || path.startsWith(AWConstants.Namespace.LOCAL + ":")) {
                path = FilenameUtils.normalize(path.substring(3));
            }
            stream = loadStreamFromPath(path);
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

    public Optional<ByteBuf> loadCompressedSkinData(String identifier) {
        ModLog.debug("Load skin data: {} ", identifier);
        InputStream inputStream;
        if (identifier.startsWith(AWConstants.Namespace.DATABASE + ":")) {
            inputStream = LocalDataService.getInstance().getFile(identifier.substring(3));
        } else {
            String path = identifier;
            if (path.startsWith(AWConstants.Namespace.SERVER + ":") || path.startsWith(AWConstants.Namespace.LOCAL + ":")) {
                path = FilenameUtils.normalize(path.substring(3));
            }
            inputStream = loadStreamFromPath(path);
        }
        if (inputStream == null) {
            return Optional.empty();
        }
        try {
            ByteBuf buf = Unpooled.buffer(10 * 1024);
            ByteBufOutputStream bo = new ByteBufOutputStream(buf);
            GZIPOutputStream outputStream = new GZIPOutputStream(bo);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            return Optional.of(buf);
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public void loadCompressedSkinData(String identifier, Consumer<Optional<ByteBuf>> consumer) {
        executorService.submit(() -> consumer.accept(loadCompressedSkinData(identifier)));
    }

    @Nullable
    private InputStream loadStreamFromPath(String identifier) {
        String ext = ".armour";
        if (!identifier.toLowerCase().endsWith(ext)) {
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


