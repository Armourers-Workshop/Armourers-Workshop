package moe.plushie.armourers_workshop.core.client.texture;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractPackResources;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
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
        super(ModConstants.key("skin"));
    }

    public static SmartResourceManager getInstance() {
        return INSTANCE;
    }

    public void register(IResourceLocation location, ByteBuffer buffer) {
        resources.put(resolve(location), buffer);
    }

    public void unregister(IResourceLocation location) {
        resources.remove(resolve(location));
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return namespaces;
    }

    @Override
    public Supplier<InputStream> getResource(PackType packType, IResourceLocation location) {
        var buf = resources.get(resolve(location));
        if (buf != null) {
            return () -> new ByteBufInputStream(Unpooled.wrappedBuffer(buf.duplicate()));
        }
        return null;
    }

    @Override
    public void close() {
        // reload or quit.
    }

    private IResourceLocation resolve(IResourceLocation location) {
        var extension = SkinFileUtils.getExtension(location.getPath());
        if (extension.isEmpty()) {
            // when no specified extension, we assume it is a png file.
            var path = location.getPath() + ".png";
            return OpenResourceLocation.create(location.getNamespace(), path);
        }
        return location;
    }
}
