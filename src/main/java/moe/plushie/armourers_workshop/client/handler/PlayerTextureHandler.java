package moe.plushie.armourers_workshop.client.handler;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.EntityTextureInfo;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.TexturePaintType;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
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
 * 
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {

    public static PlayerTextureHandler INSTANCE;

    // TODO Change this map to a cache.
    public HashMap<GameProfile, EntityTextureInfo> playerTextureMap = new HashMap<GameProfile, EntityTextureInfo>();
    private final Profiler profiler;
    private boolean useTexturePainting;

    public PlayerTextureHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        profiler = Minecraft.getMinecraft().profiler;
        INSTANCE = this;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderPlayerEvent.Pre event) {
        if (ClientProxy.getTexturePaintType() != TexturePaintType.TEXTURE_REPLACE) {
            return;
        }
        if (!(event.getEntityPlayer() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();
        /*
         * if (player instanceof MannequinFakePlayer) { return; }
         */

        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability == null) {
            return;
        }
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap == null) {
            return;
        }
        profiler.startSection("textureBuild");
        if (playerTextureMap.containsKey(player.getGameProfile())) {
            EntityTextureInfo textureInfo = playerTextureMap.get(player.getGameProfile());
            textureInfo.updateTexture(player.getLocationSkin());

            textureInfo.updateExtraColours(wardrobeCap.getExtraColours());

            // Array of skin types that are valid for the player.
            ISkinType[] skinTypes = new ISkinType[] { SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet, SkinTypeRegistry.skinOutfit };

            // Extra 4 are for the players armour slots.
            Skin[] skins = new Skin[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE + 4];
            ISkinDye[] dyes = new ISkinDye[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE + 4];

            for (int skinIndex = 0; skinIndex < EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                Skin[] skin = new Skin[skinTypes.length];
                ISkinDye[] dye = new ISkinDye[skinTypes.length];

                for (int i = 0; i < skinTypes.length; i++) {
                    ISkinDescriptor descriptor = skinCapability.getSkinDescriptor(skinTypes[i], skinIndex);
                    if (descriptor != null) {
                        skin[i] = ClientSkinCache.INSTANCE.getSkin(descriptor, false);
                        dye[i] = descriptor.getSkinDye();
                    }
                }

                skins[0 + skinIndex * skinTypes.length] = skin[0];
                skins[1 + skinIndex * skinTypes.length] = skin[1];
                skins[2 + skinIndex * skinTypes.length] = skin[2];
                skins[3 + skinIndex * skinTypes.length] = skin[3];
                skins[4 + skinIndex * skinTypes.length] = skin[4];

                dyes[0 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[0]);
                dyes[1 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[1]);
                dyes[2 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[2]);
                dyes[3 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[3]);
                dyes[4 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[4]);
            }

            ISkinType[] skinTypesArmour = new ISkinType[] { SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet };
            // Get skins from armour.
            for (int i = 0; i < skinTypesArmour.length; i++) {
                ISkinDescriptor skinDescriptor = getSkinDescriptorFromArmourer(player, skinTypesArmour[i]);
                if (skinDescriptor != null) {
                    // skins[skins.length - 4 + i] =
                    // ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
                    // dyes[dyes.length - 4 + i] = skinDescriptor.getSkinDye();
                }
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

    private ISkinDescriptor getSkinDescriptorFromArmourer(Entity entity, ISkinType skinType) {
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = ClientWardrobeHandler.getArmourInSlot(slot);
            return SkinNBTHelper.getSkinDescriptorFromStack(armourStack);
        }
        return null;
    }

    private ISkinDye mixDye(ISkinDye wardrobeDye, ISkinDye itemDye) {
        SkinDye dye = new SkinDye(wardrobeDye);
        if (itemDye != null) {
            for (int i = 0; i < 8; i++) {
                if (itemDye.haveDyeInSlot(i)) {
                    dye.addDye(i, itemDye.getDyeColour(i));
                }
            }
        }
        return dye;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.Post event) {
        if (ClientProxy.getTexturePaintType() != TexturePaintType.TEXTURE_REPLACE) {
            return;
        }
        if (!(event.getEntityPlayer() instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();
        /*
         * if (player instanceof MannequinFakePlayer) { return; }
         */
        if (player.getGameProfile() == null) {
            return;
        }

        profiler.startSection("textureReset");
        if (playerTextureMap.containsKey(player.getGameProfile())) {
            EntityTextureInfo textureInfo = playerTextureMap.get(player.getGameProfile());
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
            playerTextureMap.put(player.getGameProfile(), new EntityTextureInfo());
        }
        profiler.endSection();
    }
}
