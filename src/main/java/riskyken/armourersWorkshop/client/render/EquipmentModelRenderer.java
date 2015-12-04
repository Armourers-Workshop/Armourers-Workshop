package riskyken.armourersWorkshop.client.render;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
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
import riskyken.armourersWorkshop.client.model.equipmet.AbstractModelCustomEquipment;
import riskyken.armourersWorkshop.client.model.equipmet.IEquipmentModel;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourChest;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourFeet;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourHead;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourLegs;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomArmourSkirt;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetBow;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetSword;
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
        
        //Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = player.getCurrentArmor(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(armourStack);
                return getCustomArmourItemData(sp.skinId);
            }
        }
        
        //No skinned armour found checking the equipment wardrobe.
        if (equipmentData == null) {
            return null;
        }
        
        if (!equipmentData.haveEquipment(skinType)) {
            return null;
        }
        
        int equipmentId = equipmentData.getEquipmentId(skinType);
        return getCustomArmourItemData(equipmentId);
    }
    
    public ISkinDye getPlayerDyeData(Entity entity, ISkinType skinType) {
        if (!(entity instanceof AbstractClientPlayer)) {
            return null;
        }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        EntityEquipmentData equipmentData = playerEquipmentMap.get(new PlayerPointer(player));
        
        //Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
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
        
        if (!equipmentData.haveEquipment(skinType)) {
            return null;
        }
        
        ISkinDye skinDye = equipmentData.getSkinDye(skinType);
        return skinDye;
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
    
    public Skin getCustomArmourItemData(int equipmentId) {
        return ClientSkinCache.INSTANCE.getSkin(equipmentId);
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
        if (!equipmentData.haveEquipment(SkinTypeRegistry.skinLegs)) {
            return false;
        }
        int skinId = equipmentData.getEquipmentId(SkinTypeRegistry.skinLegs);
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinId);
        //TODO check for skirt data
        return true;
    }
    
    ItemStack equippedStack = null;
    int equippedIndex  = -1;
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        targetPlayer = player;
        if (!addedRenderAttachment & ClientProxy.useAttachedModelRender()) {
            ModelBiped playerBiped = event.renderer.modelBipedMain;
            
            playerBiped.bipedHead.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinHead, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:head.base")));
            playerBiped.bipedBody.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.base")));
            playerBiped.bipedLeftArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.leftArm")));
            playerBiped.bipedRightArm.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.rightArm")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.leftLeg")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.rightLeg")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.skirt")));
            playerBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.leftFoot")));
            playerBiped.bipedRightLeg.addChild(new ModelRendererAttachment(playerBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.rightFoot")));            
            
            addedRenderAttachment = true;
            ModLogger.log("Added model render attachment to " + playerBiped.toString());
            ModLogger.log("Using player renderer " + event.renderer.toString());
            Thread.dumpStack();
        }
        
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerHasSkirtOn(playerPointer)) {
            if (!Loader.isModLoaded("SmartMoving")) {
                EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
                if (ewd != null && ewd.limitLimbs) {
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
        
        for (int slot = 0; slot < 4; slot++) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            if (slot == SkinTypeRegistry.skinHead.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinHead);
                ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinHead);
                if (data != null) {
                    customHead.render(player, render.modelBipedMain, data, false, dye);
                }
            }
            if (slot == SkinTypeRegistry.skinChest.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinChest);
                ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinChest);
                if (data != null) {
                    customChest.render(player, render.modelBipedMain, data, false, dye);
                }
            }
            if (slot == SkinTypeRegistry.skinLegs.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinLegs);
                ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinLegs);
                if (data != null) {
                    customLegs.render(player, render.modelBipedMain, data, false, dye);
                }
            }
            if (slot == SkinTypeRegistry.skinSkirt.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinSkirt);
                ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinSkirt);
                if (data != null) {
                    customSkirt.render(player, render.modelBipedMain, data, false, dye);
                }
            }
            if (slot == SkinTypeRegistry.skinFeet.getVanillaArmourSlotId()) {
                Skin data = getPlayerCustomArmour(player, SkinTypeRegistry.skinFeet);
                ISkinDye dye = getPlayerDyeData(player, SkinTypeRegistry.skinFeet);
                if (data != null) {
                    customFeet.render(player, render.modelBipedMain, data, false, dye);
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
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer.getSkinId());
        return renderEquipmentPart(null, modelBiped, data, skinPointer.getSkinDye());
    }
    
    public boolean renderEquipmentPartFromSkinPointer(ISkinPointer skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        Skin data = getCustomArmourItemData(skinPointer.getSkinId());
        return renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin data, ISkinDye skinDye) {
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
        model.render(entity, modelBiped, data, false, skinDye);
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
