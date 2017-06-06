package riskyken.armourersWorkshop.client.handler;

import java.util.HashMap;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;

/**
 * Handles replacing the players texture with the painted version.
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {

    public static PlayerTextureHandler INSTANCE;
    
    private HashMap<PlayerPointer, EntityTextureInfo> playerTextureMap = new HashMap<PlayerPointer, EntityTextureInfo>();
    private final Profiler profiler;
    private boolean useTexturePainting;
    
    public PlayerTextureHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        profiler = Minecraft.getMinecraft().mcProfiler;
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderPlayerEvent.Pre event) {
        useTexturePainting = ClientProxy.useTexturePainting();
        if(!useTexturePainting) {
            return;
        }
        if (!(event.entityPlayer instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        if (ewd == null) {
            return;
        }
        profiler.startSection("textureBuild");
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            textureInfo.updateTexture(player.getLocationSkin());
            textureInfo.updateHairColour(ewd.hairColour);
            textureInfo.updateSkinColour(ewd.skinColour);
            Skin[] skins = new Skin[4 * ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE];
            
            for (int skinIndex = 0; skinIndex < ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                skins[0 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinHead, skinIndex);
                skins[1 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinChest, skinIndex);
                skins[2 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs, skinIndex);
                skins[3 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet, skinIndex);
            }
            ISkinDye[] dyes = new ISkinDye[4 * ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE];
            for (int skinIndex = 0; skinIndex < ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                dyes[0 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerDyeData(player, SkinTypeRegistry.skinHead, skinIndex);
                dyes[1 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerDyeData(player, SkinTypeRegistry.skinChest, skinIndex);
                dyes[2 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerDyeData(player, SkinTypeRegistry.skinLegs, skinIndex);
                dyes[3 + skinIndex * 4] = SkinModelRenderer.INSTANCE.getPlayerDyeData(player, SkinTypeRegistry.skinFeet, skinIndex);
            }
            
            textureInfo.updateSkins(skins);
            textureInfo.updateDyes(dyes);
            
            ResourceLocation replacmentTexture = textureInfo.preRender();
            player.func_152121_a(Type.SKIN, replacmentTexture);
        }
        profiler.endSection();
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.Post event) {
        if(!useTexturePainting) {
            return;
        }
        if (!(event.entityPlayer instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        if (ewd == null) {
            return;
        }
        
        profiler.startSection("textureReset");
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            ResourceLocation replacmentTexture = textureInfo.postRender();
            player.func_152121_a(Type.SKIN, replacmentTexture);
        } else {
            playerTextureMap.put(playerPointer, new EntityTextureInfo());
        }
        profiler.endSection();
    }
}
