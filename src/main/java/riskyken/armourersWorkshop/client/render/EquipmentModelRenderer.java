package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.handler.PlayerSkinHandler;
import riskyken.armourersWorkshop.client.model.ModelRendererAttachment;
import riskyken.armourersWorkshop.client.model.bake.SkinBaker;
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
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
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
    
    private HashMap<PlayerPointer, EntityEquipmentData> playerEquipmentMap = new HashMap<PlayerPointer, EntityEquipmentData>();
    
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
    
    public Skin getPlayerCustomArmour(Entity entity, ISkinType skinType) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
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
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        return equipmentData;
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.target;
            if (player.getGameProfile() == null) {
                return;
            }
            PlayerPointer playerPointer = new PlayerPointer(player);
            if (playerEquipmentMap.containsKey(playerPointer)) {
                playerEquipmentMap.remove(playerPointer);
            }
        }
    }
    
    public int getSkinDataMapSize() {
        return playerEquipmentMap.size();
    }
    
    public Skin getCustomArmourItemData(int equipmentId) {
        return ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(equipmentId);
    }
    
    public void addEquipmentData(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        if (playerEquipmentMap.containsKey(playerPointer)) {
            playerEquipmentMap.remove(playerPointer);
        }
        playerEquipmentMap.put(playerPointer, equipmentData);
    }
    
    public void removeEquipmentData(PlayerPointer playerPointer) {
        if (playerEquipmentMap.containsKey(playerPointer)) {
            playerEquipmentMap.remove(playerPointer);
        }
    }

    private boolean playerHasCustomArmourType(PlayerPointer playerPointer, ISkinType skinType) {
        if (!playerEquipmentMap.containsKey(playerPointer)) {
            return false;
        }
        EntityEquipmentData equipmentData = playerEquipmentMap.get(playerPointer);
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
            
            playerBiped.bipedHead.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinHead, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:head.base")));
            playerBiped.bipedBody.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.base")));
            playerBiped.bipedLeftArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.leftArm")));
            playerBiped.bipedRightArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.rightArm")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.leftLeg")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.rightLeg")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinSkirt, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:skirt.base")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.leftFoot")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.rightFoot")));            
            
            addedRenderAttachment = true;
        }
        
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerHasCustomArmourType(playerPointer, SkinTypeRegistry.skinSkirt)) {
            if (!Loader.isModLoaded("SmartMoving")) {
                PlayerSkinInfo skinInfo = PlayerSkinHandler.INSTANCE.getPlayersNakedData(playerPointer);
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
        PlayerPointer playerPointer = new PlayerPointer(player);
        if (!playerEquipmentMap.containsKey(playerPointer)) {
            return;
        }
        
        if (!SkinBaker.withinMaxRenderDistance(player.posX, player.posY, player.posZ)) {
            return;
        }
        
        for (int slot = 0; slot < 4; slot++) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            if (slot == SkinTypeRegistry.skinHead.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinHead);
                if (data != null) {
                    customHead.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinChest.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinChest);
                if (data != null) {
                    customChest.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinLegs.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs);
                if (data != null) {
                    customLegs.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinSkirt.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinSkirt);
                if (data != null) {
                    customSkirt.render(player, render.modelBipedMain, data);
                }
            }
            if (slot == SkinTypeRegistry.skinFeet.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet);
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
        } else if (skinType == SkinTypeRegistry.skinBow) {
            return customBow;
        }
        return null;
    }

    public void renderMannequinEquipment(TileEntityMannequin teMannequin, ModelBiped modelBiped) {
        EntityEquipmentData equipmentData = teMannequin.getEquipmentData();
        
        if (!SkinBaker.withinMaxRenderDistance(teMannequin.xCoord, teMannequin.yCoord, teMannequin.zCoord)) {
            return;
        }
        
        ArrayList<ISkinType> skinList = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (equipmentData.haveEquipment(skinType)) {
                Skin data = getCustomArmourItemData(equipmentData.getEquipmentId(skinType));
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
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        int equipmentId = EquipmentNBTHelper.getSkinIdFromStack(stack);
        Skin data = getCustomArmourItemData(equipmentId);
        return renderEquipmentPart(null, modelBiped, data);
    }
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        int equipmentId = EquipmentNBTHelper.getSkinIdFromStack(stack);
        Skin data = getCustomArmourItemData(equipmentId);
        return renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin data) {
        if (data == null) {
            return false;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getSkinType());
        if (model == null) {
            return false;
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        model.render(entity, modelBiped, data);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        return true;
    }
    
    private boolean renderEquipmentPartRotated(Entity entity, Skin data, float limb1, float limb2, float limb3, float headY, float headX) {
        if (data == null) {
            return false;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getSkinType());
        if (model == null) {
            return false;
        }
        model.render(entity, data, limb1, limb2, limb3, headY, headX);
        return true;
    }
}
