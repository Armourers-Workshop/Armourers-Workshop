package riskyken.armourersWorkshop.client.handler;

import java.util.HashMap;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {

    public static PlayerTextureHandler INSTANCE;
    
    private HashMap<PlayerPointer, EntityTextureInfo> playerTextureMap = new HashMap<PlayerPointer, EntityTextureInfo>();
    
    public PlayerTextureHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public EntityTextureInfo getPlayersNakedData(PlayerPointer playerPointer) {
        if (!playerTextureMap.containsKey(playerPointer)) {
            return null;
        }
        return playerTextureMap.get(playerPointer);
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderPlayerEvent.Pre event) {
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
        
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            textureInfo.updateTexture(player.getLocationSkin());
            textureInfo.updateHairColour(ewd.hairColour);
            textureInfo.updateSkinColour(ewd.skinColour);
            Skin[] skins = new Skin[4];
            skins[0] = EquipmentModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinHead);
            skins[1] = EquipmentModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinChest);
            skins[2] = EquipmentModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs);
            skins[3] = EquipmentModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet);
            textureInfo.updateSkins(skins);
            ResourceLocation replacmentTexture = textureInfo.preRender();
            player.func_152121_a(Type.SKIN, replacmentTexture);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.Specials.Pre event) {
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
        
        if (playerTextureMap.containsKey(playerPointer)) {
            EntityTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            ResourceLocation replacmentTexture = textureInfo.postRender();
            player.func_152121_a(Type.SKIN, replacmentTexture);
        } else {
            playerTextureMap.put(playerPointer, new EntityTextureInfo());
        }
    }
}
