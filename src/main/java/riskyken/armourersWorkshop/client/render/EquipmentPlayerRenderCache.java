package riskyken.armourersWorkshop.client.render;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientRequestEquipmentDataData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.ModLogger;
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
public final class EquipmentPlayerRenderCache {
    
    public static final EquipmentPlayerRenderCache INSTANCE = new EquipmentPlayerRenderCache();
    
    private HashMap<UUID, EntityEquipmentData> playerEquipmentMap = new HashMap<UUID, EntityEquipmentData>();
    private HashMap<Integer, CustomArmourItemData> equipmentDataMap = new HashMap<Integer, CustomArmourItemData>();
    private HashSet<Integer> requestedEquipmentIds = new HashSet<Integer>();
    private HashMap<UUID, PlayerSkinInfo> skinMap = new HashMap<UUID, PlayerSkinInfo>();
    
    public ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    
    public EquipmentPlayerRenderCache() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void requestEquipmentDataFromServer(int equipmentId) {
        if (!requestedEquipmentIds.contains(equipmentId)) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientRequestEquipmentDataData(equipmentId, (byte) 1));
            requestedEquipmentIds.add(equipmentId);
        }
    }
    
    public void receivedEquipmentData(CustomArmourItemData equipmentData) {
        int equipmentId = equipmentData.hashCode();
        
        if (equipmentDataMap.containsKey(equipmentId)){
            equipmentDataMap.remove(equipmentId);
        }
        equipmentDataMap.put(equipmentId, equipmentData);
        
        if (requestedEquipmentIds.contains(equipmentId)) {
            requestedEquipmentIds.remove(equipmentId);
        } else {
            ModLogger.log(Level.WARN, "Got an unknown equipment id: " + equipmentId);
        }
    }
    
    public CustomArmourItemData getPlayerCustomArmour(Entity entity, EnumEquipmentType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(player.getPersistentID());
        
        if (!equipmentData.haveEquipment(type)) {
            return null;
        }
        
        int equipmentId = equipmentData.getEquipmentId(type);
        return getCustomArmourItemData(equipmentId);
    }
    
    public CustomArmourItemData getCustomArmourItemData(int equipmentId) {
        if (equipmentDataMap.containsKey(equipmentId)) {
            return equipmentDataMap.get(equipmentId);
        } else {
            requestEquipmentDataFromServer(equipmentId);
        }
        return null;
    }
    
    public void addEquipmentData(UUID playerId, EntityEquipmentData equipmentData) {
        EntityClientPlayerMP localPlayer = Minecraft.getMinecraft().thePlayer;
        if (localPlayer.getPersistentID() == playerId) {
            ExtendedPropsPlayerEquipmentData props = ExtendedPropsPlayerEquipmentData.get(localPlayer);
            props.setEquipmentData(equipmentData);
        }
        if (playerEquipmentMap.containsKey(playerId)) {
            playerEquipmentMap.remove(playerId);
        }
        playerEquipmentMap.put(playerId, equipmentData);
    }
    
    public void removeEquipmentData(UUID playerId) {
        if (playerEquipmentMap.containsKey(playerId)) {
            playerEquipmentMap.remove(playerId);
        }
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

    private boolean playerHasCustomArmourType(UUID playerId, EnumEquipmentType armourType) {
        if (!playerEquipmentMap.containsKey(playerId)) {
            return false;
        }
        EntityEquipmentData equipmentData = playerEquipmentMap.get(playerId);
        return equipmentData.haveEquipment(armourType);
    }
    
    
    ItemStack equippedStack = null;
    int equippedIndex  = -1;
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (skinMap.containsKey(player.getPersistentID())) {
            PlayerSkinInfo skinInfo = skinMap.get(player.getPersistentID());
            skinInfo.preRender((AbstractClientPlayer) player, event.renderer);
        }
        
        if (playerHasCustomArmourType(player.getPersistentID(), EnumEquipmentType.SKIRT)) {
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
    
    @SubscribeEvent
    public void onRender(RenderHandEvent event) {
        
    }
    
    public int getCacheSize() {
        return equipmentDataMap.size();
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.SetArmorModel event) {
        EntityPlayer player = event.entityPlayer;
        RenderPlayer render = event.renderer;
        PlayerSkinInfo skinInfo = null;
        
        if (player.getGameProfile() != null) {
            if (player.isInvisible()) { return; }
        }
        
        int result = -1;
        int slot = -event.slot + 3;
        
        if (skinMap.containsKey(player.getPersistentID())) {
            skinInfo = skinMap.get(player.getPersistentID());
            BitSet armourOverride = skinInfo.getArmourOverride();
            if (armourOverride.get(slot)) {
                result = -2;
            }
        }
        
        if (!playerEquipmentMap.containsKey(player.getPersistentID())) {
            //This player has no custom equipment. booooo
            return;
        }
        
        GL11.glPushMatrix();
        float scale = 1.001F;
        GL11.glScalef(scale, scale, scale);
        
        if (slot == EnumEquipmentType.HEAD.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, EnumEquipmentType.HEAD);
            if (data != null) {
                customHead.render(player, render.modelBipedMain, data);
            }
            
        }
        if (slot == EnumEquipmentType.CHEST.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, EnumEquipmentType.CHEST);
            if (data != null) {
                customChest.render(player, render.modelBipedMain, data);
            }
        }
        if (slot == EnumEquipmentType.LEGS.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, EnumEquipmentType.LEGS);
            if (data != null) {
                customLegs.render(player, render.modelBipedMain, data);
                event.result = result;
            }
        }
        if (slot == EnumEquipmentType.SKIRT.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, EnumEquipmentType.SKIRT);
            if (data != null) {
                customSkirt.render(player, render.modelBipedMain, data);
            }
        }
        if (slot == EnumEquipmentType.FEET.getSlotId()) {
            CustomArmourItemData data = getPlayerCustomArmour(player, EnumEquipmentType.FEET);
            if (data != null) {
                customFeet.render(player, render.modelBipedMain, data);
            }
        }
        
        GL11.glPopMatrix();
        event.result = result;
    }

    public void renderMannequinEquipment(TileEntityMannequin teMannequin, ModelBiped modelBiped) {
        EntityEquipmentData equipmentData = teMannequin.getEquipmentData();
        
        for (int i = 0; i < 6; i++) {
            EnumEquipmentType armourType = EnumEquipmentType.getOrdinal(i + 1);
            if (equipmentData.haveEquipment(armourType)) {
                CustomArmourItemData data = getCustomArmourItemData(equipmentData.getEquipmentId(armourType));
                renderEquipmentPart(null, modelBiped, data);
            }
        }
    }
    
    public void renderEquipmentPartOnEntity(Entity entity, EnumEquipmentType armourType, ModelBiped modelBiped) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            return;
        }
        
        EntityEquipmentData equipmentData = entityProps.getEquipmentData();
        
        if (equipmentData.haveEquipment(armourType)) {
            CustomArmourItemData data = getCustomArmourItemData(equipmentData.getEquipmentId(armourType));
            renderEquipmentPart(entity, modelBiped, data);
        }
    }
    
    public void renderEquipmentPart(int equipmentId, ModelBiped modelBiped) {
        CustomArmourItemData data = getCustomArmourItemData(equipmentId);
        renderEquipmentPart(null, modelBiped, data);
    }
    
    private void renderEquipmentPart(Entity entity, ModelBiped modelBiped, CustomArmourItemData data) {
        if (data == null) {
            return;
        }
        switch (data.getType()) {
        case HEAD:
            customHead.render(entity, modelBiped, data);
            break;
        case CHEST:
            customChest.render(entity, modelBiped, data);
            break;
        case LEGS:
            customLegs.render(entity, modelBiped, data);
            break;
        case SKIRT:
            customSkirt.render(entity, modelBiped, data);
            break;
        case FEET:
            customFeet.render(entity, modelBiped, data);
            break;
        case WEAPON:
            //TODO Render weapons on mannequins
            break;
        default:
            break;
        }
    }
    
    public void tick() {
        for (int i = 0; i < equipmentDataMap.size(); i++) {
            int key = (Integer) equipmentDataMap.keySet().toArray()[i];
            equipmentDataMap.get(key).tick();
        }
        
        for (int i = 0; i < equipmentDataMap.size(); i++) {
            int key = (Integer) equipmentDataMap.keySet().toArray()[i];
            CustomArmourItemData customArmourItemData = equipmentDataMap.get(key);
            if (customArmourItemData.needsCleanup()) {
                equipmentDataMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
                break;
            }
        }
    }
}
