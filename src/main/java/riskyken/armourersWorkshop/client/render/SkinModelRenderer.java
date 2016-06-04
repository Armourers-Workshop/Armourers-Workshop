package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.ModelRendererAttachment;
import riskyken.armourersWorkshop.client.model.bake.SkinBaker;
import riskyken.armourersWorkshop.client.model.skin.AbstractModelSkin;
import riskyken.armourersWorkshop.client.model.skin.IEquipmentModel;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinBow;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinChest;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinFeet;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinHead;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinLegs;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinSkirt;
import riskyken.armourersWorkshop.client.model.skin.ModelSkinSword;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

/**
 * Helps render custom equipment on the player and other entities.
 *
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class SkinModelRenderer {
    
    public static SkinModelRenderer INSTANCE;
    
    private HashMap<PlayerPointer, EntityEquipmentData> playerEquipmentMap = new HashMap<PlayerPointer, EntityEquipmentData>();
    
    public ModelSkinChest customChest = new ModelSkinChest();
    public ModelSkinHead customHead = new ModelSkinHead();
    public ModelSkinLegs customLegs = new ModelSkinLegs();
    public ModelSkinSkirt customSkirt = new ModelSkinSkirt();
    public ModelSkinFeet customFeet = new ModelSkinFeet();
    public ModelSkinSword customSword = new ModelSkinSword();
    public ModelSkinBow customBow = new ModelSkinBow();
    
    private boolean addedRenderAttachment = false;
    public EntityPlayer targetPlayer = null;
    
    public static void init() {
        INSTANCE = new SkinModelRenderer();
    }
    
    public SkinModelRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public Skin getPlayerCustomArmour(Entity entity, ISkinType skinType, int slotIndex) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        //Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4 && slotIndex == 0) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = player.getCurrentArmor(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(armourStack);
                return getCustomArmourItemData(sp);
            }
        }
        
        //No skinned armour found checking the equipment wardrobe.
        if (equipmentData == null) {
            return null;
        }
        
        if (!equipmentData.haveEquipment(skinType, slotIndex)) {
            return null;
        }
        
        ISkinPointer skinPointer = equipmentData.getSkinPointer(skinType, slotIndex);
        return getCustomArmourItemData(skinPointer);
    }
    
    public ISkinDye getPlayerDyeData(Entity entity, ISkinType skinType, int slotIndex) {
        if (!(entity instanceof AbstractClientPlayer)) {
            return null;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        //Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4 && slotIndex == 0) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = player.getCurrentArmor(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(armourStack);
                return sp.getSkinDye();
            }
        }
        
        //No skinned armour found checking the equipment wardrobe.
        if (equipmentData == null) {
            return null;
        }
        
        if (!equipmentData.haveEquipment(skinType, slotIndex)) {
            return null;
        }
        
        ISkinDye skinDye = equipmentData.getSkinDye(skinType, slotIndex);
        return skinDye;
    }
    
    public byte[] getPlayerExtraColours(Entity entity) {
        if (!(entity instanceof AbstractClientPlayer)) {
            return null;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        return null;
    }
    
    public IEntityEquipment getPlayerCustomEquipmentData(Entity entity) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        return equipmentData;
    }
    
    public int getSkinDataMapSize() {
        return playerEquipmentMap.size();
    }
    
    public Skin getCustomArmourItemData(ISkinPointer skinPointer) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
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
    
    private boolean playerHasSkirtOn(PlayerPointer playerPointer) {
        if (!playerEquipmentMap.containsKey(playerPointer)) {
            return false;
        }
        EntityEquipmentData equipmentData = playerEquipmentMap.get(playerPointer);
        for (int i = 0; i < equipmentData.getNumberOfSlots(); i++) {
            if (!equipmentData.haveEquipment(SkinTypeRegistry.skinLegs, i)) {
                return false;
            } else {
                ISkinPointer skinPointer = equipmentData.getSkinPointer(SkinTypeRegistry.skinLegs, i);
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
                if (skin != null) {
                    for (int j = 0; j < skin.getPartCount(); j++) {
                        if (skin.getParts().get(j).getPartType().getPartName().equals("skirt")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    ItemStack equippedStack = null;
    int equippedIndex  = -1;
    HashSet<String> addedRenderSet = new HashSet<String>();
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        targetPlayer = player;
        ModelBiped playerBiped = event.renderer.modelBipedMain;
        
        if (!addedRenderSet.contains(playerBiped.toString()) & ClientProxy.useAttachedModelRender()) {
            addedRenderSet.add(playerBiped.toString());
            playerBiped.bipedHead.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinHead, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:head.base")));
            playerBiped.bipedBody.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.base")));
            playerBiped.bipedLeftArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.leftArm")));
            playerBiped.bipedRightArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.rightArm")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.leftLeg")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.rightLeg")));
            playerBiped.bipedBody.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.skirt")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.leftFoot")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.rightFoot")));            
            
            addedRenderAttachment = true;
            ModLogger.log("Added model render attachment to " + playerBiped.toString());
            ModLogger.log("Using player renderer " + event.renderer.toString());
        }
        
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerHasSkirtOn(playerPointer)) {
            EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
            if (ewd != null && ewd.limitLimbs) {
                if (player.limbSwingAmount > 0.25F) {
                    player.limbSwingAmount = 0.25F;
                    player.prevLimbSwingAmount = 0.25F;
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
        if (ClientProxy.useAttachedModelRender()) {
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
        
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(new PlayerPointer(player));
        byte[] extraColours = null;
        if (ewd != null) {
            Color skinColour = new Color(ewd.skinColour);
            Color hairColour = new Color(ewd.hairColour);
            extraColours = new byte[] {
                    (byte)skinColour.getRed(), (byte)skinColour.getGreen(), (byte)skinColour.getBlue(),
                    (byte)hairColour.getRed(), (byte)hairColour.getGreen(), (byte)hairColour.getBlue()};
        }
        
        for (int slot = 0; slot < 4; slot++) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            for (int skinIndex = 0; skinIndex < 5; skinIndex++) {
                if (slot == SkinTypeRegistry.skinHead.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinHead, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinHead, skinIndex);
                    if (data != null) {
                        customHead.render(player, render.modelBipedMain, data, false, dye, extraColours, false);
                    }
                }
                if (slot == SkinTypeRegistry.skinChest.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinChest, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinChest, skinIndex);
                    if (data != null) {
                        customChest.render(player, render.modelBipedMain, data, false, dye, extraColours, false);
                    }
                }
                if (slot == SkinTypeRegistry.skinLegs.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinLegs, skinIndex);
                    if (data != null) {
                        customLegs.render(player, render.modelBipedMain, data, false, dye, extraColours, false);
                    }
                }
                if (slot == SkinTypeRegistry.skinSkirt.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinSkirt, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinSkirt, skinIndex);
                    if (data != null) {
                        customSkirt.render(player, render.modelBipedMain, data, false, dye, extraColours, false);
                    }
                }
                if (slot == SkinTypeRegistry.skinFeet.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinFeet, skinIndex);
                    if (data != null) {
                        customFeet.render(player, render.modelBipedMain, data, false, dye, extraColours, false);
                    }
                }
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
    
    public AbstractModelSkin getModelForEquipmentType(ISkinType skinType) {
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
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped, byte[] extraColours) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPart(null, modelBiped, data, skinPointer.getSkinDye(), extraColours);
    }
    
    public boolean renderEquipmentPartFromSkinPointer(ISkinPointer skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin data, ISkinDye skinDye, byte[] extraColours) {
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
        model.render(entity, modelBiped, data, false, skinDye, extraColours, false);
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
