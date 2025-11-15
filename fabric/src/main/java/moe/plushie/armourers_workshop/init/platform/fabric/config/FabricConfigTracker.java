package moe.plushie.armourers_workshop.init.platform.fabric.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moe.plushie.armourers_workshop.init.ModLog;

import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FabricConfigTracker {
    public static final FabricConfigTracker INSTANCE = new FabricConfigTracker();
    private final ConcurrentHashMap<String, FabricConfig> fileMap;
    private final EnumMap<FabricConfig.Type, Set<FabricConfig>> configSets;
    private final ConcurrentHashMap<String, Map<FabricConfig.Type, FabricConfig>> configsByMod;

    private FabricConfigTracker() {
        this.fileMap = new ConcurrentHashMap<>();
        this.configSets = new EnumMap<>(FabricConfig.Type.class);
        this.configsByMod = new ConcurrentHashMap<>();
        this.configSets.put(FabricConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(FabricConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
//        this.configSets.put(FabricConfig.Type.PLAYER, new ConcurrentSkipListSet<>());
        this.configSets.put(FabricConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    void trackConfig(final FabricConfig config) {
        if (this.fileMap.containsKey(config.getFileName())) {
            ModLog.error("Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k) -> new EnumMap<>(FabricConfig.Type.class)).put(config.getType(), config);
        ModLog.debug("Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public void loadConfigs(FabricConfig.Type type, Path configBasePath) {
        ModLog.debug("Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
    }

    public void unloadConfigs(FabricConfig.Type type, Path configBasePath) {
        ModLog.debug("Unloading configs type {}", type);
        this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }
//
//    public List<Pair<String, FMLHandshakeMessages.S2CConfigData>> syncConfigs(boolean isLocal) {
//        final Map<String, byte[]> configData = configSets.get(FabricConfig.Type.SERVER).stream().collect(Collectors.toMap(FabricConfig::getFileName, mc -> { //TODO: Test cpw's LambdaExceptionUtils on Oracle javac.
//            try {
//                return Files.readAllBytes(mc.getFullPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }));
//        return configData.entrySet().stream().map(e->Pair.of("Config "+e.getKey(), new FMLHandshakeMessages.S2CConfigData(e.getKey(), e.getValue()))).collect(Collectors.toList());
//    }

    private void openConfig(final FabricConfig config, final Path configBasePath) {
        ModLog.debug("Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        FabricConfigEvents.LOADING.invoker().config(config);
        config.save();
    }

    private void closeConfig(final FabricConfig config, final Path configBasePath) {
        if (config.getConfigData() != null) {
            ModLog.debug("Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
            config.save();
            config.getHandler().unload(configBasePath, config);
            config.setConfigData(null);
        }
    }

//    public void receiveSyncedConfig(final FMLHandshakeMessages.S2CConfigData s2CConfigData, final Supplier<NetworkEvent.Context> contextSupplier) {
//        if (!Minecraft.getInstance().isLocalServer()) {
//            Optional.ofNullable(fileMap.get(s2CConfigData.getFileName())).ifPresent(mc-> {
//                mc.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(s2CConfigData.getBytes())));
//                mc.fireEvent(new FabricConfig.Reloading(mc));
//            });
//        }
//    }
//
//    public void loadDefaultServerConfigs() {
//        configSets.get(FabricConfig.Type.SERVER).forEach(FabricConfig -> {
//            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
//            FabricConfig.getSpec().correct(commentedConfig);
//            FabricConfig.setConfigData(commentedConfig);
//            FabricConfig.fireEvent(new FabricConfig.Loading(FabricConfig));
//        });
//    }
//
//    public String getConfigFileName(String modId, FabricConfig.Type type) {
//        return Optional.ofNullable(configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).
//                map(FabricConfig::getFullPath).map(Object::toString).orElse(null);
//    }
}
