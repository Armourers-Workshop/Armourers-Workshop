package moe.plushie.armourers_workshop.init.config;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class FabricConfigFileTypeHandler {
    static FabricConfigFileTypeHandler TOML = new FabricConfigFileTypeHandler();
    private static final Path defaultConfigPath = FabricLoader.getInstance().getGameDir().resolve("defaultconfigs");

    public Function<FabricConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                    preserveInsertionOrder().
                    autosave().
                    onFileNotFound((newFile, configFormat) -> setupConfigFile(c, newFile, configFormat)).
                    writingMode(WritingMode.REPLACE).
                    build();
            ModLog.debug("Built TOML config for {}", configPath.toString());
            try {
                configData.load();
            } catch (ParsingException ex) {
                throw new ConfigLoadingException(c, ex);
            }
            ModLog.debug("Loaded TOML config file {}", configPath.toString());
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
                ModLog.debug("Watching TOML config file {} for changes", configPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Couldn't watch config file", e);
            }
            return configData;
        };
    }

    public void unload(Path configBasePath, FabricConfig config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(config.getFileName()));
        } catch (RuntimeException e) {
            ModLog.error("Failed to remove config {} from tracker!", configPath.toString(), e);
        }
    }

    private boolean setupConfigFile(final FabricConfig FabricConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        Path p = defaultConfigPath.resolve(FabricConfig.getFileName());
        if (Files.exists(p)) {
            ModLog.info("Loading default config file from path {}", p);
            Files.copy(p, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig) {
        backUpConfig(commentedFileConfig, 5); //TODO: Think of a way for mods to set their own preference (include a sanity check as well, no disk stuffing)
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups) {
        Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
        String bakFileName = SkinFileUtils.removeExtension(commentedFileConfig.getFile().getName());
        String bakFileExtension = SkinFileUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups)
                        Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig.getNioPath(), bakFile);
        } catch (IOException exception) {
            ModLog.warn("Failed to back up config file {}", commentedFileConfig.getNioPath(), exception);
        }
    }

    private static class ConfigWatcher implements Runnable {
        private final FabricConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final FabricConfig FabricConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = FabricConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try {
                    this.commentedFileConfig.load();
                    if (!this.modConfig.getSpec().isCorrect(commentedFileConfig)) {
                        ModLog.warn("Configuration file {} is not correct. Correcting", commentedFileConfig.getFile().getAbsolutePath());
                        FabricConfigFileTypeHandler.backUpConfig(commentedFileConfig);
                        this.modConfig.getSpec().correct(commentedFileConfig);
                        commentedFileConfig.save();
                    }
                } catch (ParsingException ex) {
                    throw new ConfigLoadingException(modConfig, ex);
                }
                ModLog.debug("Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                FabricConfigEvents.RELOADING.invoker().config(this.modConfig);
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException {
        public ConfigLoadingException(FabricConfig config, Exception cause) {
            super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
        }
    }
}
