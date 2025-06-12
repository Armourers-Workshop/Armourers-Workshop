package moe.plushie.armourers_workshop.core.client.other;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractPackResources;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.server.packs.PackType;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SmartResourceManager {

    private static final SmartResourceManager INSTANCE = new SmartResourceManager();

    protected final String id;
    protected final Set<String> namespaces = Collections.immutableSet(builder -> builder.add(ModConstants.MOD_ID));
    protected final Map<OpenResourceLocation, ByteBuf> resources = new ConcurrentHashMap<>();

    protected SmartResourceManager() {
        this.id = String.format("dynamic/%s", ModConstants.MOD_ID);
    }

    public static SmartResourceManager getInstance() {
        return INSTANCE;
    }

    public void register(OpenResourceLocation location, ByteBuf buffer) {
        resources.put(location, buffer);
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Registering Resource '{}'", location);
        }
    }

    public void unregister(OpenResourceLocation location) {
        resources.remove(location);
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Unregistering Resource '{}'", location);
        }
    }

    public Supplier<InputStream> getResource(PackType packType, OpenResourceLocation location) {
        var buf = resources.get(location);
        if (buf != null) {
            return () -> new ByteBufInputStream(buf.slice());
        }
        return null;
    }

    public AbstractPackResources getResources(PackType packType) {
        return new AbstractPackResources(this, packType);
    }

    public Set<String> getNamespaces(PackType packType) {
        return namespaces;
    }

    public String id() {
        return id;
    }
}
