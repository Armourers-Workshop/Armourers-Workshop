package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
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

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.handler.PlayerSkinHandler;
import riskyken.armourersWorkshop.client.model.ModelRendererAttachment;
import riskyken.armourersWorkshop.client.model.equipmet.AbstractModelCustomEquipment;
import riskyken.armourersWorkshop.client.model.equipmet.IEquipmentModel;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetBow;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetSword;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.handler.EquipmentDataHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.UtilPlayer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Helps render custom equipment on the player and other entities.
 *
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class EquipmentModelRenderer {
    
    public static EquipmentModelRenderer INSTANCE;
    
    private HashMap<UUID, EntityEquipmentData> playerEquipmentMap = new HashMap<UUID, EntityEquipmentData>();
    
    public ModelCustomArmourChest customChest = new ModelCustomArmourChest();
    public ModelCustomArmourHead customHead = new ModelCustomArmourHead();
    public ModelCustomArmourLegs customLegs = new ModelCustomArmourLegs();
    public ModelCustomArmourSkirt customSkirt = new ModelCustomArmourSkirt();
    public ModelCustomArmourFeet customFeet = new ModelCustomArmourFeet();
    public ModelCustomEquipmetSword customSword = new ModelCustomEquipmetSword();
    public ModelCustomEquipmetBow customBow = new ModelCustomEquipmetBow();
    
    private boolean addedRenderAttachment = false;
    public EntityPlayer targetPlayer = null;
    
    public static void init() {
        INSTANCE = new EquipmentModelRenderer();
    }
    
    public EquipmentModelRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public CustomEquipmentItemData getPlayerCustomArmour(Entity entity, ISkinType skinType) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(UtilPlayer.getIDFromPlayer(player));
        
        if (equipmentData == null) {
            return null;
        }
        
        if (!equipmentData.haveEquipment(skinType)) {
            return null;
        }
        
        int equipmentId = equipmentData.getEquipmentId(skinType);
        return getCustomArmourItemData(equipmentId);
    }
    
    public IEntityEquipment getPlayerCustomEquipmentData(Entity entity) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(UtilPlayer.getIDFromPlayer(player));
        
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

    private boolean playerHasCustomArmourType(UUID playerId, ISkinType skinType) {
        if (!playerEquipmentMap.containsKey(playerId)) {
            return false;
        }
        EntityEquipmentData equipmentData = playerEquipmentMap.get(playerId);
        return equipmentData.haveEquipment(skinType);
    }
    
    ItemStack equippedStack = null;
    int equippedIndex  = -1;
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        targetPlayer = player;
        if (!addedRenderAttachment & !ConfigHandler.compatibilityRender) {
            ModelBiped playerBiped = event.renderer.modelBipedMain;
            
            playerBiped.bipedHead.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinHead, EnumEquipmentPart.HEAD));
            playerBiped.bipedBody.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, EnumEquipmentPart.CHEST));
            playerBiped.bipedLeftArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, EnumEquipmentPart.LEFT_ARM));
            playerBiped.bipedRightArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, EnumEquipmentPart.RIGHT_ARM));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, EnumEquipmentPart.LEFT_LEG));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, EnumEquipmentPart.RIGHT_LEG));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinSkirt, EnumEquipmentPart.SKIRT));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, EnumEquipmentPart.LEFT_FOOT));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, EnumEquipmentPart.RIGHT_FOOT));            
            
            addedRenderAttachment = true;
        }
        
        if (playerHasCustomArmourType(UtilPlayer.getIDFromPlayer(player), SkinTypeRegistry.skinSkirt)) {
            if (!Loader.isModLoaded("SmartMoving")) {
                PlayerSkinInfo skinInfo = PlayerSkinHandler.INSTANCE.getPlayersNakedData(UtilPlayer.getIDFromPlayer(player));
                if (skinInfo != null && skinInfo.getNakedInfo().limitLimbs) {
                    if (player.limbSwingAmount > 0.25F) {
                        player.limbSwingAmount = 0.25F;
                    } 
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
    	targetPlayer = null;
    }
    
    @SubscribeEvent
    public void onRenderSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        if (!ConfigHandler.compatibilityRender) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        RenderPlayer render = event.renderer;
        if (player.getGameProfile() == null) {
            return;
        }
        if (!playerEquipmentMap.containsKey(UtilPlayer.getIDFromPlayer(player))) {
            return;
        }
        
        if (!EquipmentRenderHelper.withinMaxRenderDistance(player.posX, player.posY, player.posZ)) {
            return;
        }
        
        for (int slot = 0; slot < 4; slot++) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            if (slot == SkinTypeRegistry.skinHead.getVanillaArmourSlotId()) {
                CustomEquipmentItemData data = getPlayerCustomArmour(player, SkinTypeRegistry.skinHead);
                if (data != null) {
                    customHead.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinChest.getVanillaArmourSlotId()) {
                CustomEquipmentItemData data = getPlayerCustomArmour(player, SkinTypeRegistry.skinChest);
                if (data != null) {
                    customChest.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinLegs.getVanillaArmourSlotId()) {
                CustomEquipmentItemData data = getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs);
                if (data != null) {
                    customLegs.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinSkirt.getVanillaArmourSlotId()) {
                CustomEquipmentItemData data = getPlayerCustomArmour(player, SkinTypeRegistry.skinSkirt);
                if (data != null) {
                    customSkirt.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinFeet.getVanillaArmourSlotId()) {
                CustomEquipmentItemData data = getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet);
                if (data != null) {
                    customFeet.render(player, render.modelBipedMain, data);
                }
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
    
    public AbstractModelCustomEquipment getModelForEquipmentType(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinHead) {
            return customHead;
        } else if (skinType == SkinTypeRegistry.skinChest) {
            return customChest;
        } else if (skinType == SkinTypeRegistry.skinLegs) {
            return customLegs;
        } else if (skinType == SkinTypeRegistry.skinSkirt) {
            return customSkirt;
        } else if (skinType == SkinTypeRegistry.skinFeet) {
            return customFeet;
        } else if (skinType == SkinTypeRegistry.skinSword) {
            return customSword;
        }
        //return customBow;
        return null;
    }

    public void renderMannequinEquipment(TileEntityMannequin teMannequin, ModelBiped modelBiped) {
        EntityEquipmentData equipmentData = teMannequin.getEquipmentData();
        
        if (!EquipmentRenderHelper.withinMaxRenderDistance(teMannequin.xCoord, teMannequin.yCoord, teMannequin.zCoord)) {
            return;
        }
        
        ArrayList<ISkinType> skinList = SkinTypeRegistry.INSTANCE.getRegisteredSkins();
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (equipmentData.haveEquipment(skinType)) {
                CustomEquipmentItemData data = getCustomArmourItemData(equipmentData.getEquipmentId(skinType));
                if (skinType == SkinTypeRegistry.skinSword) {
                    float scale = 0.0625F;
                    GL11.glPushMatrix();
                    
                    if (modelBiped != null) {
                        if (modelBiped.isChild) {
                            float f6 = 2.0F;
                            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
                        }
                    }
                    
                    GL11.glTranslatef(-5F * scale, 0, 0);
                    GL11.glTranslatef(0, 2F * scale, 0);
                    
                    GL11.glRotated(Math.toDegrees(modelBiped.bipedRightArm.rotateAngleZ), 0, 0, 1);
                    GL11.glRotated(Math.toDegrees(modelBiped.bipedRightArm.rotateAngleY), 0, 1, 0);
                    GL11.glRotated(Math.toDegrees(modelBiped.bipedRightArm.rotateAngleX), 1, 0, 0);
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
        if (EquipmentDataHandler.INSTANCE.getSkinTypeFromStack(stack) == null) {
            return;
        }
        int equipmentId = EquipmentDataHandler.INSTANCE.getEquipmentIdFromItemStack(stack);
        CustomEquipmentItemData data = getCustomArmourItemData(equipmentId);
        renderEquipmentPart(null, modelBiped, data);
    }
    
    public void renderEquipmentPartFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (EquipmentDataHandler.INSTANCE.getSkinTypeFromStack(stack) == null) {
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
        IEquipmentModel model = getModelForEquipmentType(data.getSkinType());
        if (model == null) {
            return;
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        model.render(entity, modelBiped, data);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }
    
    private void renderEquipmentPartRotated(Entity entity, CustomEquipmentItemData data, float limb1, float limb2, float limb3, float headY, float headX) {
        if (data == null) {
            return;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getSkinType());
        if (model == null) {
            return;
        }
        model.render(entity, data, limb1, limb2, limb3, headY, headX);
    }
}
