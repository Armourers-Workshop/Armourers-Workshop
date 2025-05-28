package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractSimpleTexture;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

import java.util.IdentityHashMap;

@Environment(EnvType.CLIENT)
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

    public synchronized IRenderType register(SkinTextureData provider, ISkinGeometryType type) {
        var texture = textures.get(provider);
        if (texture == null) {
            texture = new SmartTexture(provider);
            textures.put(provider, texture);
        }
        return texture.getRenderType(type);
    }

    public TextureManager getTextureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    protected void uploadTexture(SmartTexture texture) {
        var location = texture.getLocation();
        getTextureManager().register(location.toLocation(), AbstractSimpleTexture.create(location));
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Registering Texture '{}'", location);
        }
    }

    protected void releaseTexture(SmartTexture texture) {
        var location = texture.getLocation();
        getTextureManager().unregister(location.toLocation());
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Unregistering Texture '{}'", location);
        }
    }
}
