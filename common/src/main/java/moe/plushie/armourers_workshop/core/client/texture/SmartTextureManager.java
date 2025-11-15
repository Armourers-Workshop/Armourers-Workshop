package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.AbstractSimpleTexture;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

import java.util.IdentityHashMap;

@OnlyIn(Dist.CLIENT)
public class SmartTextureManager {

    private static final SmartTextureManager INSTANCE = new SmartTextureManager();

    protected final IdentityHashMap<Object, SmartTexture> textures = new IdentityHashMap<>();

    public static SmartTextureManager getInstance() {
        return INSTANCE;
    }

    public synchronized void start() {
    }

    public synchronized void stop() {
        // release all registered textures.
        textures.values().forEach(SmartTexture::unbind);
        textures.clear();
    }

    public void open(IRenderType renderType) {
        var texture = SmartTexture.of(renderType);
        if (texture != null) {
            texture.retain();
        }
    }

    public void close(IRenderType renderType) {
        var texture = SmartTexture.of(renderType);
        if (texture != null) {
            texture.release();
        }
    }

    public synchronized IRenderType register(SkinTextureData provider, SkinGeometryType type) {
        var texture = textures.get(provider);
        if (texture == null) {
            texture = new SmartTexture(provider);
            textures.put(provider, texture);
        }
        return texture.getRenderType(type);
    }

    public TextureManager textureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    protected void uploadTexture(SmartTexture texture) {
        var location = texture.location();
        textureManager().register(location, AbstractSimpleTexture.create(location));
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Registering Texture '{}'", location);
        }
    }

    protected void releaseTexture(SmartTexture texture) {
        var location = texture.location();
        textureManager().release(location);
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Unregistering Texture '{}'", location);
        }
    }
}
