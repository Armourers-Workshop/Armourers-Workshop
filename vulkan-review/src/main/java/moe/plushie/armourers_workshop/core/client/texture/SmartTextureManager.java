package moe.plushie.armourers_workshop.core.client.texture;

import java.util.IdentityHashMap;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractSimpleTexture;
import moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.renderer.texture.TextureManager.ABI;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModConfig.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

public class SmartTextureManager {
   private static final SmartTextureManager INSTANCE = new SmartTextureManager();
   protected final IdentityHashMap<Object, SmartTexture> textures = new IdentityHashMap<>();
   private final IdentityHashMap<SmartTexture, Boolean> pinned = new IdentityHashMap<>();

   public static SmartTextureManager getInstance() {
      return INSTANCE;
   }

   public static void start() {
   }

   public static void stop() {
      INSTANCE.textures.values().forEach(SmartTexture::close);
      INSTANCE.textures.clear();
      INSTANCE.pinned.clear();
   }

   public void pin(IRenderType renderType) {
      if (!(renderType instanceof moe.plushie.armourers_workshop.api.data.IAssociatedContainer)) return;
      SmartTexture texture = SmartTexture.of(renderType);
      if (texture != null && !pinned.containsKey(texture)) {
         // Pin only a texture actually submitted for drawing, until manager stop.
         // GPU binding happens after vertex callbacks; per-mesh release is too early.
         pinned.put(texture, Boolean.TRUE);
         texture.retain();
      }
   }

   public void open(IRenderType renderType) {
      SmartTexture texture = SmartTexture.of(renderType);
      if (texture != null) {
         texture.retain();
      }
   }

   public void close(IRenderType renderType) {
      SmartTexture texture = SmartTexture.of(renderType);
      if (texture != null) {
         texture.release();
      }
   }

   public SmartTexture register(SkinTextureData textureData) {
      SmartTexture texture = this.textures.get(textureData);
      if (texture == null) {
         texture = new SmartTexture(textureData);
         this.textures.put(textureData, texture);

      }

      return texture;
   }

   public TextureManager getTextureManager() {
      return Minecraft.getInstance().getTextureManager();
   }

   protected void uploadTexture(SmartTexture texture) {
      OpenResourceKey key = texture.location();
      ABI.register(this.getTextureManager(), key, AbstractSimpleTexture.create(key));
      if (Client.enableResourceDebug) {
         ModLog.debug("Registering Texture '{}'", new Object[]{key});
      }
   }

   protected void releaseTexture(SmartTexture texture) {
      OpenResourceKey key = texture.location();
      ABI.release(this.getTextureManager(), key);
      if (Client.enableResourceDebug) {
         ModLog.debug("Unregistering Texture '{}'", new Object[]{key});
      }
   }
}
