package riskyken.armourersWorkshop.client.render;

import java.util.BitSet;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.model.equipmet.IEquipmentModel;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetBow;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetSword;
import riskyken.armourersWorkshop.common.BipedRotations;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.EntityNakedInfo;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.handler.EquipmentDataHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
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
public final class EquipmentModelRender {
    
    public static final EquipmentModelRender INSTANCE = new EquipmentModelRender();
    
    private HashMap<UUID, EntityEquipmentData> playerEquipmentMap = new HashMap<UUID, EntityEquipmentData>();
    private HashMap<UUID, PlayerSkinInfo> skinMap = new HashMap<UUID, PlayerSkinInfo>();
    
    public ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    public ModelCustomEquipmetSword customSword = new ModelCustomEquipmetSword();
    public ModelCustomEquipmetBow customBow = new ModelCustomEquipmetBow();
    
    public EquipmentModelRender() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public CustomEquipmentItemData getPlayerCustomArmour(Entity entity, EnumEquipmentType type) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(player.getPersistentID());
        
        if (!equipmentData.haveEquipment(type)) {
            return null;
        }
        
        int equipmentId = equipmentData.getEquipmentId(type);
        return getCustomArmourItemData(equipmentId);
    }
    
    public IEntityEquipment getPlayerCustomEquipmentData(Entity entity) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(player.getPersistentID());
        
        return equipmentData;
    }
    
    public CustomEquipmentItemData getCustomArmourItemData(int equipmentId) {
        return ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(equipmentId);
    }
    
    public void addEquipmentData(UUID playerId, EntityEquipmentData equipmentData) {
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
    
    public void setPlayersSkinData(UUID playerId, EntityNakedInfo nakedInfo) {
        if (!skinMap.containsKey(playerId)) {
            skinMap.put(playerId, new PlayerSkinInfo(nakedInfo));
        } else {
            skinMap.get(playerId).setSkinInfo(nakedInfo);
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
    
    public IEquipmentModel getModelForEquipmentType(EnumEquipmentType equipmentType) {
        switch (equipmentType) {
        case NONE:
            return null;
        case HEAD:
            return customHead;
        case CHEST:
            return customChest;
        case LEGS:
            return customLegs;
        case SKIRT:
            return customSkirt;
        case FEET:
            return customFeet;
        case SWORD:
            return customSword;
        case BOW:
            return customBow;
        }
        return null;
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
        if (slot > 3) {
            return;
        }
        
        if (skinMap.containsKey(player.getPersistentID())) {
            skinInfo = skinMap.get(player.getPersistentID());
            BitSet armourOverride = skinInfo.getNakedInfo().armourOverride;
            if (armourOverride.get(slot)) {
                result = -2;
            }
        }
        
        if (!playerEquipmentMap.containsKey(player.getPersistentID())) {
            //This player has no custom equipment. booooo
            return;
        }
        
        if (!EquipmentRenderHelper.withinMaxRenderDistance(player.posX, player.posY, player.posZ)) {
            event.result = result;
            return;
        }
        
        GL11.glPushMatrix();
        float scale = 1.001F;
        GL11.glScalef(scale, scale, scale);
        
        if (slot == EnumEquipmentType.HEAD.getVanillaSlotId()) {
            CustomEquipmentItemData data = getPlayerCustomArmour(player, EnumEquipmentType.HEAD);
            if (data != null) {
                customHead.render(player, render.modelBipedMain, data);
            }
            
        }
        if (slot == EnumEquipmentType.CHEST.getVanillaSlotId()) {
            CustomEquipmentItemData data = getPlayerCustomArmour(player, EnumEquipmentType.CHEST);
            if (data != null) {
                customChest.render(player, render.modelBipedMain, data);
            }
        }
        if (slot == EnumEquipmentType.LEGS.getVanillaSlotId()) {
            CustomEquipmentItemData data = getPlayerCustomArmour(player, EnumEquipmentType.LEGS);
            if (data != null) {
                customLegs.render(player, render.modelBipedMain, data);
                event.result = result;
            }
        }
        if (slot == EnumEquipmentType.SKIRT.getVanillaSlotId()) {
            CustomEquipmentItemData data = getPlayerCustomArmour(player, EnumEquipmentType.SKIRT);
            if (data != null) {
                customSkirt.render(player, render.modelBipedMain, data);
            }
        }
        if (slot == EnumEquipmentType.FEET.getVanillaSlotId()) {
            CustomEquipmentItemData data = getPlayerCustomArmour(player, EnumEquipmentType.FEET);
            if (data != null) {
                customFeet.render(player, render.modelBipedMain, data);
            }
        }
        
        GL11.glPopMatrix();
        event.result = result;
    }

    public void renderMannequinEquipment(TileEntityMannequin teMannequin, ModelBiped modelBiped) {
        EntityEquipmentData equipmentData = teMannequin.getEquipmentData();
        
        if (!EquipmentRenderHelper.withinMaxRenderDistance(teMannequin.xCoord, teMannequin.yCoord, teMannequin.zCoord)) {
            return;
        }
        
        for (int i = 0; i < 6; i++) {
            EnumEquipmentType armourType = EnumEquipmentType.getOrdinal(i + 1);
            if (equipmentData.haveEquipment(armourType)) {
                CustomEquipmentItemData data = getCustomArmourItemData(equipmentData.getEquipmentId(armourType));
                if (armourType == EnumEquipmentType.SWORD | armourType == EnumEquipmentType.BOW) {
                    float scale = 0.0625F;
                    GL11.glPushMatrix();
                    BipedRotations ripedRotations = teMannequin.getBipedRotations();
                    
                    if (modelBiped != null) {
                        if (modelBiped.isChild) {
                            float f6 = 2.0F;
                            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
                        }
                    }
                    
                    GL11.glTranslatef(-5F * scale, 0, 0);
                    GL11.glTranslatef(0, 2F * scale, 0);
                    
                    GL11.glRotated(Math.toDegrees(ripedRotations.rightArm.rotationZ), 0, 0, 1);
                    GL11.glRotated(Math.toDegrees(ripedRotations.rightArm.rotationY), 0, 1, 0);
                    GL11.glRotated(Math.toDegrees(ripedRotations.rightArm.rotationX), 1, 0, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                    
                    GL11.glTranslatef(0, 0, -8 * scale);
                    GL11.glTranslatef(-1F * scale, 0, 0);
                    renderEquipmentPart(null, null, data);
                    
                    GL11.glPopMatrix();
                } else {
                    renderEquipmentPart(null, modelBiped, data);
                }
            }
        }
    }
    
    public void renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped) {
        if (EquipmentDataHandler.INSTANCE.getEquipmentTypeFromStack(stack) == EnumEquipmentType.NONE) {
            return;
        }
        int equipmentId = EquipmentDataHandler.INSTANCE.getEquipmentIdFromItemStack(stack);
        CustomEquipmentItemData data = getCustomArmourItemData(equipmentId);
        renderEquipmentPart(null, modelBiped, data);
    }
    
    public void renderEquipmentPartFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (EquipmentDataHandler.INSTANCE.getEquipmentTypeFromStack(stack) == EnumEquipmentType.NONE) {
            return;
        }
        int equipmentId = EquipmentDataHandler.INSTANCE.getEquipmentIdFromItemStack(stack);
        CustomEquipmentItemData data = getCustomArmourItemData(equipmentId);
        renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    private void renderEquipmentPart(Entity entity, ModelBiped modelBiped, CustomEquipmentItemData data) {
        if (data == null) {
            return;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getType());
        if (model == null) {
            return;
        }
        model.render(entity, modelBiped, data);
    }
    
    private void renderEquipmentPartRotated(Entity entity, CustomEquipmentItemData data, float limb1, float limb2, float limb3, float headY, float headX) {
        if (data == null) {
            return;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getType());
        if (model == null) {
            return;
        }
        model.render(entity, data, limb1, limb2, limb3, headY, headX);
    }
}
