package moe.plushie.armourers_workshop.client.handler;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.EntityTextureInfo;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles replacing the players texture with the painted version.
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {

    public static PlayerTextureHandler INSTANCE;
    
    // TODO Change this map to a cache.
    private HashMap<EntityPlayer, EntityTextureInfo> playerTextureMap = new HashMap<EntityPlayer, EntityTextureInfo>();
    private final Profiler profiler;
    private boolean useTexturePainting;
    
    public PlayerTextureHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        profiler = Minecraft.getMinecraft().profiler;
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderPlayerEvent.Pre event) {
        useTexturePainting = ClientProxy.useTexturePainting();
        if(!useTexturePainting) {
            return;
        }
        if (!(event.getEntityPlayer() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();
        /*if (player instanceof MannequinFakePlayer) {
            return;
        }*/
        
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability == null) {
            return;
        }
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap == null) {
            return;
        }
        profiler.startSection("textureBuild");
        if (playerTextureMap.containsKey(player)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(player);
            textureInfo.updateTexture(player.getLocationSkin());
            
            textureInfo.updateExtraColours(wardrobeCap.getExtraColours());
            
            ISkinType[] skinTypes = new ISkinType[] {SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest,
                    SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet, SkinTypeRegistry.skinOutfit};
            
            Skin[] skins = new Skin[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];
            ISkinDye[] dyes = new ISkinDye[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];
            
            for (int skinIndex = 0; skinIndex < EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                Skin[] skin = new Skin[skinTypes.length];
                ISkinDye[] dye = new ISkinDye[skinTypes.length];
                
                for (int i = 0; i < skinTypes.length; i++) {
                    ISkinDescriptor descriptor = skinCapability.getSkinDescriptor(skinTypes[i], skinIndex);
                    if (descriptor != null) {
                        skin[i] = ClientSkinCache.INSTANCE.getSkin(descriptor);
                        dye[i] = descriptor.getSkinDye();
                    }
                }
                
                skins[0 + skinIndex * skinTypes.length] = skin[0];
                skins[1 + skinIndex * skinTypes.length] = skin[1];
                skins[2 + skinIndex * skinTypes.length] = skin[2];
                skins[3 + skinIndex * skinTypes.length] = skin[3];
                skins[4 + skinIndex * skinTypes.length] = skin[4];
                
                dyes[0 + skinIndex * skinTypes.length] = dye[0];
                dyes[1 + skinIndex * skinTypes.length] = dye[1];
                dyes[2 + skinIndex * skinTypes.length] = dye[2];
                dyes[3 + skinIndex * skinTypes.length] = dye[3];
                dyes[4 + skinIndex * skinTypes.length] = dye[4];
            }
            
            textureInfo.updateSkins(skins);
            textureInfo.updateDyes(dyes);
            
            ResourceLocation replacmentTexture = textureInfo.preRender();
            
            NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
            if (playerInfo != null) {
                Map<Type, ResourceLocation> playerTextures = ReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a", "playerTextures");
                if (playerTextures != null) {
                    playerTextures.put(Type.SKIN, replacmentTexture);
                }
            }
        }
        profiler.endSection();
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.Post event) {
        if(!useTexturePainting) {
            return;
        }
        if (!(event.getEntityPlayer() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();
        /*if (player instanceof MannequinFakePlayer) {
            return;
        }*/
        if (player.getGameProfile() == null) {
            return;
        }
        
        profiler.startSection("textureReset");
        if (playerTextureMap.containsKey(player)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(player);
            ResourceLocation replacmentTexture = textureInfo.getReplacementTexture();
            ResourceLocation normalTexture = textureInfo.postRender();
            NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
            if (playerInfo != null) {
                Map<Type, ResourceLocation> playerTextures = ReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a", "playerTextures");
                if (playerTextures != null) {
                    ResourceLocation curTexture = player.getLocationSkin();
                    if (curTexture == replacmentTexture) {
                        playerTextures.put(Type.SKIN, normalTexture);
                    }
                }
            }
        } else {
            playerTextureMap.put(player, new EntityTextureInfo());
        }
        profiler.endSection();
    }
}
