package riskyken.armourersWorkshop.client.render;

import java.util.BitSet;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Holds a cache of ModelCustomItemBuilt that are used when the client renders a
 * player equipment model.
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public class EquipmentPlayerRenderCache {
    
    public HashMap<String, CustomArmourItemData> customArmor = new HashMap<String, CustomArmourItemData>();
    public HashMap<UUID, PlayerSkinInfo> skinMap = new HashMap<UUID, PlayerSkinInfo>();
    
    public ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    
    public EquipmentPlayerRenderCache() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public CustomArmourItemData getPlayerCustomArmour(Entity entity, ArmourType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        String key = player.getPersistentID().toString() + ":" + type.name();
        if (!customArmor.containsKey(key)) {
            return null;
        }

        CustomArmourItemData armorData = customArmor.get(key);
        if (armorData.getType() != type) { return null; }
        return armorData;
    }
    
    public void addCustomArmour(UUID playerId, CustomArmourItemData armourData) {
        String key = playerId.toString() + ":" + armourData.getType().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        armourData.removeHiddenBlocks();
        customArmor.put(key, armourData);
    }
    
    public void setPlayersSkinData(UUID playerId, boolean isNaked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        if (!skinMap.containsKey(playerId)) {
            skinMap.put(playerId, new PlayerSkinInfo(isNaked, skinColour, pantsColour, armourOverride, headOverlay));
        } else {
            skinMap.get(playerId).setSkinInfo(isNaked, skinColour, pantsColour, armourOverride, headOverlay);
        }
    }
    
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        if (!skinMap.containsKey(playerId)) {
            return null;
        }
        return skinMap.get(playerId);
    }
    
    public void removeCustomArmour(UUID playerId, ArmourType type) {
        String key = playerId.toString() + ":" + type.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
    }

    public void removeAllCustomArmourData(UUID playerId) {
        removeCustomArmour(playerId, ArmourType.HEAD);
        removeCustomArmour(playerId, ArmourType.CHEST);
        removeCustomArmour(playerId, ArmourType.LEGS);
        removeCustomArmour(playerId, ArmourType.SKIRT);
        removeCustomArmour(playerId, ArmourType.FEET);
    }

    private boolean playerHasCustomArmourType(UUID playerId, ArmourType armourType) {
        String key = playerId.toString() + ":" + armourType.name();
        return customArmor.containsKey(key);
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (skinMap.containsKey(player.getPersistentID())) {
            PlayerSkinInfo skinInfo = skinMap.get(player.getPersistentID());
            skinInfo.preRender((AbstractClientPlayer) player, event.renderer);
        }
        
        if (playerHasCustomArmourType(player.getPersistentID(), ArmourType.SKIRT)) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
            }
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
    	EntityPlayer player = event.entityPlayer;
    	if (skinMap.containsKey(player.getPersistentID())) {
    		PlayerSkinInfo skinInfo = skinMap.get(player.getPersistentID());
    		skinInfo.postRender((AbstractClientPlayer) player, event.renderer);
    	}
    }
    
    public int getCacheSize() {
        return customArmor.size();
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.SetArmorModel event) {
        EntityPlayer player = event.entityPlayer;
        RenderPlayer render = event.renderer;
        PlayerSkinInfo skinInfo = null;
        int result = -1;
        int slot = -event.slot + 3;
        
    	if (skinMap.containsKey(player.getPersistentID())) {
    		skinInfo = skinMap.get(player.getPersistentID());
    		BitSet armourOverride = skinInfo.getArmourOverride();
    		if (armourOverride.get(slot)) {
    			result = -2;
    		}
    	}
        
        if (slot == ArmourType.HEAD.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.HEAD);
            if (data != null) {
                customHead.render(player, render, data);
            }
            
        }
        if (slot == ArmourType.CHEST.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.CHEST);
            if (data != null) {
                customChest.render(player, render, data);
            }
        }
        if (slot == ArmourType.LEGS.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.LEGS);
            if (data != null) {
                customLegs.render(player, render, data);
                event.result = result;
            }
        }
        if (slot == ArmourType.SKIRT.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.SKIRT);
            if (data != null) {
                customSkirt.render(player, render, data);
            }
        }
        if (slot == ArmourType.FEET.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, ArmourType.FEET);
            if (data != null) {
                customFeet.render(player, render, data);
            }
        }
        event.result = result;
    }
}
