package moe.plushie.armourers_workshop.core.client.texture;

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

public class SmartResourceManager extends AbstractPackResources {

    private static final SmartResourceManager INSTANCE = new SmartResourceManager();

    private final Set<String> namespaces = Set.of(ModConstants.MOD_ID);
    private final HashMap<IResourceLocation, ByteBuffer> resources = new HashMap<>();

    protected SmartResourceManager() {
        super(String.format("dynamic/%s", ModConstants.MOD_ID));
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

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return namespaces;
    }

    @Override
    public Supplier<InputStream> getResource(PackType packType, IResourceLocation location) {
        var buf = resources.get(location);
        if (buf != null) {
            return () -> new ByteBufInputStream(Unpooled.wrappedBuffer(buf.duplicate()));
        }
        return null;
    }

    @Override
    public void close() {
        // reload or quit.
    }
}
