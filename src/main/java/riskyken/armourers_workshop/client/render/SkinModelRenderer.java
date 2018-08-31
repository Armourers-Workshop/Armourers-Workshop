package riskyken.armourers_workshop.client.render;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.WeakHashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.skin.IEntityEquipment;
import riskyken.armourers_workshop.api.common.skin.data.ISkinDye;
import riskyken.armourers_workshop.api.common.skin.data.ISkinPointer;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.model.ModelRendererAttachment;
import riskyken.armourers_workshop.client.model.skin.AbstractModelSkin;
import riskyken.armourers_workshop.client.model.skin.IEquipmentModel;
import riskyken.armourers_workshop.client.model.skin.ModelSkinBow;
import riskyken.armourers_workshop.client.model.skin.ModelSkinChest;
import riskyken.armourers_workshop.client.model.skin.ModelSkinFeet;
import riskyken.armourers_workshop.client.model.skin.ModelSkinHead;
import riskyken.armourers_workshop.client.model.skin.ModelSkinLegs;
import riskyken.armourers_workshop.client.model.skin.ModelSkinSkirt;
import riskyken.armourers_workshop.client.model.skin.ModelSkinSword;
import riskyken.armourers_workshop.client.model.skin.ModelSkinWings;
import riskyken.armourers_workshop.client.skin.cache.ClientSkinCache;
import riskyken.armourers_workshop.common.config.ConfigHandlerClient;
import riskyken.armourers_workshop.common.data.PlayerPointer;
import riskyken.armourers_workshop.common.skin.EntityEquipmentData;
import riskyken.armourers_workshop.common.skin.EquipmentWardrobeData;
import riskyken.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.common.skin.data.SkinProperties;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourers_workshop.proxies.ClientProxy;
import riskyken.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import riskyken.armourers_workshop.utils.ModLogger;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

/**
 * Helps render custom equipment on the player and other entities.
 *
 * TODO Clean up this class it's a mess >:|
 *
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class SkinModelRenderer {
    
    public static SkinModelRenderer INSTANCE;
    
    public static void init() {
        INSTANCE = new SkinModelRenderer();
    }
    
    private final HashMap<PlayerPointer, EntityEquipmentData> playerEquipmentMap;
    private final Set<ModelBiped> attachedBipedSet;
    
    public final ModelSkinChest customChest = new ModelSkinChest();
    public final ModelSkinHead customHead = new ModelSkinHead();
    public final ModelSkinLegs customLegs = new ModelSkinLegs();
    public final ModelSkinSkirt customSkirt = new ModelSkinSkirt();
    public final ModelSkinFeet customFeet = new ModelSkinFeet();
    public final ModelSkinSword customSword = new ModelSkinSword();
    public final ModelSkinBow customBow = new ModelSkinBow();
    public final ModelSkinWings customWings = new ModelSkinWings();
    
    public EntityPlayer targetPlayer = null;
    
    private SkinModelRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
        playerEquipmentMap = new HashMap<PlayerPointer, EntityEquipmentData>();
        attachedBipedSet = Collections.newSetFromMap(new WeakHashMap<ModelBiped, Boolean>());
    }
    
    public Skin getPlayerCustomArmour(Entity entity, ISkinType skinType, int slotIndex) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        //Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4 && slotIndex == 0) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            /*ItemStack armourStack = player.getCurrentArmor(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(armourStack);
                return getCustomArmourItemData(sp);
            }*/
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
            /*
            ItemStack armourStack = player.getCurrentArmor(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(armourStack);
                return sp.getSkinDye();
            }
            */
        }
        
        //No skinned armour found checking the equipment wardrobe.
        if (equipmentData == null) {
            return null;
        }
        
        if (!equipmentData.haveEquipment(skinType, slotIndex)) {
            return null;
        }
        
        ISkinDye skinDye = equipmentData.getSkinPointer(skinType, slotIndex).getSkinDye();
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
    
    private boolean isPlayerWearingSkirt(PlayerPointer playerPointer) {
        EntityEquipmentData equipmentData = playerEquipmentMap.get(playerPointer);
        if (equipmentData != null) { 
            for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
                ISkinPointer skinPointer = equipmentData.getSkinPointer(SkinTypeRegistry.skinLegs, i);
                if (skinPointer != null) {
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
        }
        return false;
    }
    
    public boolean playerHasCustomHead(EntityPlayer player) {
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        if (equipmentData != null) {
            for (int i = 0; i < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; i++) {
                ISkinPointer sp = equipmentData.getSkinPointer(SkinTypeRegistry.skinHead, i);
                if (sp != null) {
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(sp, false);
                    if (skin!= null) {
                        if (SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skin.getProperties())) {
                            return true;
                        }
                        if (SkinProperties.PROP_ARMOUR_HIDE_OVERLAY.getValue(skin.getProperties())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        targetPlayer = player;
        
        if (ClientProxy.getSkinRenderType() == SkinRenderType.MODEL_ATTACHMENT) {
            attachModelsToBiped(event.getRenderer().getMainModel(), event.getRenderer());
        }
        
        
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        //Limit the players limbs if they have a skirt equipped.
        //A proper lady should not swing her legs around!
        if (isPlayerWearingSkirt(playerPointer)) {
            EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
            if (ewd != null && ewd.limitLimbs) {
                if (player.limbSwingAmount > 0.25F) {
                    player.limbSwingAmount = 0.25F;
                    player.prevLimbSwingAmount = 0.25F;
                } 
            }
        }
    }
    
    private void attachModelsToBiped(ModelBiped modelBiped, RenderPlayer renderPlayer) {
        if (attachedBipedSet.contains(modelBiped)) {
            return;
        }
        attachedBipedSet.add(modelBiped);
        modelBiped.bipedHead.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinHead, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:head.base")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.base")));
        modelBiped.bipedLeftArm.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.leftArm")));
        modelBiped.bipedRightArm.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.rightArm")));
        modelBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.leftLeg")));
        modelBiped.bipedRightLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.rightLeg")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.skirt")));
        modelBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.leftFoot")));
        modelBiped.bipedRightLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.rightFoot")));  
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinWings, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:wings.leftWing")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinWings, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:wings.rightWing")));
        ModLogger.log(String.format("Added model render attachment to %s", modelBiped.toString()));
        ModLogger.log(String.format("Using player renderer %s", renderPlayer.toString()));
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
    	targetPlayer = null;
    }
    
    @SubscribeEvent
    public void onRenderSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        if (ClientProxy.getSkinRenderType() != SkinRenderType.RENDER_EVENT) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        RenderPlayer render = event.getRenderer();
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        if (!playerEquipmentMap.containsKey(playerPointer)) {
            return;
        }
        
        double distance = Minecraft.getMinecraft().player.getDistance(
                player.posX,
                player.posY,
                player.posZ);
        
        if (distance > ConfigHandlerClient.maxSkinRenderDistance) {
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
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        for (int slot = 0; slot < 4; slot++) {

            for (int skinIndex = 0; skinIndex < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                if (slot == SkinTypeRegistry.skinHead.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinHead, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinHead, skinIndex);
                    if (data != null) {
                        customHead.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
                if (slot == SkinTypeRegistry.skinChest.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinChest, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinChest, skinIndex);
                    if (data != null) {
                        customChest.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
                if (slot == SkinTypeRegistry.skinLegs.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinLegs, skinIndex);
                    if (data != null) {
                        customLegs.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
                if (slot == SkinTypeRegistry.skinSkirt.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinSkirt, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinSkirt, skinIndex);
                    if (data != null) {
                        customSkirt.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
                if (slot == SkinTypeRegistry.skinFeet.getVanillaArmourSlotId()) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinFeet, skinIndex);
                    if (data != null) {
                        customFeet.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
                if (slot == 0) {
                    Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinWings, skinIndex);
                    ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinWings, skinIndex);
                    if (data != null) {
                        customWings.render(player, render.getMainModel(), data, false, dye, extraColours, false, distance, true);
                    }
                }
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
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
        } else if (skinType == SkinTypeRegistry.skinWings) {
            return customWings;
        }
        return null;
    }
    
    public boolean renderEquipmentPartFromStack(Entity entity, ItemStack stack, ModelBiped modelBiped, byte[] extraColours, double distance, boolean doLodLoading) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPart(entity, modelBiped, data, skinPointer.getSkinDye(), extraColours, distance, doLodLoading);
    }
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped, byte[] extraColours, double distance, boolean doLodLoading) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPart(null, modelBiped, data, skinPointer.getSkinDye(), extraColours, distance, doLodLoading);
    }
    
    public boolean renderEquipmentPartFromSkinPointer(ISkinPointer skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin data, ISkinDye skinDye, byte[] extraColours, double distance, boolean doLodLoading) {
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
        model.render(entity, modelBiped, data, false, skinDye, extraColours, false, distance, doLodLoading);
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
