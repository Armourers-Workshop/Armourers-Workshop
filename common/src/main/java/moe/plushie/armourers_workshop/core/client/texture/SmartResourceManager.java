package moe.plushie.armourers_workshop.core.client.texture;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractPackResources;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.server.packs.PackType;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;

public class SmartResourceManager {

    private static final SmartResourceManager INSTANCE = new SmartResourceManager();

    protected final String id;
    protected final Set<String> namespaces = Sets.newHashSet(ModConstants.MOD_ID);
    protected final HashMap<IResourceLocation, ByteBuffer> resources = new HashMap<>();

    protected SmartResourceManager() {
        this.id = String.format("dynamic/%s", ModConstants.MOD_ID);
    }

    public static SmartResourceManager getInstance() {
        return INSTANCE;
    }

    public void register(IResourceLocation location, ByteBuffer buffer) {
        ModLog.debug("Registering Resource '{}'", location);
        resources.put(location, buffer);
    }

    public void unregister(IResourceLocation location) {
        ModLog.debug("Unregistering Resource '{}'", location);
        resources.remove(location);
    }

    public Supplier<InputStream> getResource(PackType packType, IResourceLocation location) {
        var buf = resources.get(location);
        if (buf != null) {
            return () -> new ByteBufInputStream(Unpooled.wrappedBuffer(buf.duplicate()));
        }
        return null;
    }

    public AbstractPackResources getResources(PackType packType) {
        return new AbstractPackResources(this, packType);
    }

    public Set<String> getNamespaces(PackType packType) {
        return namespaces;
    }

    public String getId() {
        return id;
    }
}
