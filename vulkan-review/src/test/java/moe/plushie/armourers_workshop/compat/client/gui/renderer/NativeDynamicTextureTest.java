package moe.plushie.armourers_workshop.compat.client.gui.renderer;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import io.netty.buffer.*;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractSimpleTexture;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
public class NativeDynamicTextureTest {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        var key=OpenResourceKey.create("armourers_workshop","textures/dynamic/regression.png");
        var manager=SmartResourceManager.getInstance();
        var field=SmartResourceManager.class.getDeclaredField("resources");field.setAccessible(true);
        Map<OpenResourceKey,ByteBuf> resources=(Map<OpenResourceKey,ByteBuf>)field.get(manager);
        // A single opaque pixel registered by the real dynamic pack, absent from disk resources.
        byte[] png=Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIHWP4z8DwHwAFgAI/ScLbtAAAAABJRU5ErkJggg==");
        var bytes=Unpooled.wrappedBuffer(png);resources.put(key,bytes);
        var metaKey=key.withPath(key.path()+".mcmeta");
        var meta=Unpooled.copiedBuffer("{\"texture\":{\"blur\":true,\"clamp\":true}}",java.nio.charset.StandardCharsets.UTF_8);
        resources.put(metaKey,meta);
        ResourceManager disk=(ResourceManager)Proxy.newProxyInstance(ResourceManager.class.getClassLoader(),new Class[]{ResourceManager.class},(p,m,a)->{
            if(m.getName().equals("getResource"))return Optional.empty();
            if(m.getName().equals("getResourceOrThrow"))throw new FileNotFoundException("dynamic bytes are not a disk resource");
            return null;
        });
        try(var contents=AbstractSimpleTexture.create(key).loadContents(disk)) {
            NativeGuiClipTest.check(contents.image().getWidth()==1,"registered in-memory PNG decoded");
            NativeGuiClipTest.check(contents.blur() && contents.clamp(),"dynamic filter and clamp metadata retained");
            System.out.println("PASS: dynamic PNG and metadata load from registered memory bytes");
        } finally { resources.remove(key);bytes.release();resources.remove(metaKey);meta.release(); }
    }
}
