package moe.plushie.armourers_workshop.client.render;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.handler.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.client.model.ModelRendererAttachment;
import moe.plushie.armourers_workshop.client.model.skin.AbstractModelSkin;
import moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinChest;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinFeet;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinHead;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinLegs;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinOutfit;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinSword;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinWings;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    
    private final Set<ModelBiped> attachedBipedSet;
    
    public final ModelSkinChest customChest = new ModelSkinChest();
    public final ModelSkinHead customHead = new ModelSkinHead();
    public final ModelSkinLegs customLegs = new ModelSkinLegs();
    public final ModelSkinFeet customFeet = new ModelSkinFeet();
    public final ModelSkinSword customSword = new ModelSkinSword();
    public final ModelSkinBow customBow = new ModelSkinBow();
    public final ModelSkinWings customWings = new ModelSkinWings();
    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();
    
    public EntityPlayer targetPlayer = null;
    
    private SkinModelRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
        attachedBipedSet = Collections.newSetFromMap(new WeakHashMap<ModelBiped, Boolean>());
    }
    
    public Skin getPlayerCustomArmour(Entity entity, ISkinType skinType, int slotIndex) {
        if (!(entity instanceof AbstractClientPlayer)) { return null; }
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        
        // Look for skinned armourer.
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4 && slotIndex == 0) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = ClientWardrobeHandler.getArmourInSlot(slot);
            if (SkinNBTHelper.stackHasSkinData(armourStack)) {
                SkinDescriptor sd = SkinNBTHelper.getSkinDescriptorFromStack(armourStack);
                return getCustomArmourItemData(sd);
            }
        }
        
        // No skinned armour found checking the wardrobe.
        /*
        IEntitySkinCapability skinCapability = entity.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
        if (skinCapability != null) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, slotIndex);
            if (skinDescriptor != null) {
                return getCustomArmourItemData(skinDescriptor);
            }
        }
        */
        return null;
    }
    
    public Skin getCustomArmourItemData(ISkinDescriptor skinPointer) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }
    
    private boolean isPlayerWearingSkirt(EntityPlayer player) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability == null) {
            return false;
        }
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinLegs); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinLegs, i);
            if (skinDescriptor != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin != null) {
                    return SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties());
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
        
        //Limit the players limbs if they have a skirt equipped.
        //A proper lady should not swing her legs around!
        if (isPlayerWearingSkirt(player)) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
                player.prevLimbSwingAmount = 0.25F;
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
    public void onRenderSpecialsPost(RenderPlayerEvent.Post event) {
        if (ClientProxy.getSkinRenderType() != SkinRenderType.RENDER_EVENT) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        RenderPlayer render = event.getRenderer();
        
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap == null) {
            return;
        }
        
        double distance = Minecraft.getMinecraft().player.getDistance(
                player.posX,
                player.posY,
                player.posZ);
        
        if (distance > ConfigHandlerClient.renderDistanceSkin) {
            return;
        }
        
        ExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
        if (wardrobeCap != null) {
            extraColours = wardrobeCap.getExtraColours();
        }
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        /*
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
        */
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
        } else if (skinType == SkinTypeRegistry.skinFeet) {
            return customFeet;
        } else if (skinType == SkinTypeRegistry.skinSword) {
            return customSword;
        } else if (skinType == SkinTypeRegistry.skinBow) {
            return customBow;
        } else if (skinType == SkinTypeRegistry.skinWings) {
            return customWings;
        } else if (skinType == SkinTypeRegistry.skinOutfit) {
            return modelOutfit;
        }
        return null;
    }
    
    public boolean renderEquipmentPartFromStack(Entity entity, ItemStack stack, ModelBiped modelBiped, ExtraColours extraColours, double distance, boolean doLodLoading) {
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPart(entity, modelBiped, data, skinPointer.getSkinDye(), extraColours, distance, doLodLoading);
    }
    
    public boolean renderEquipmentPartFromStack(ItemStack stack, ModelBiped modelBiped, ExtraColours extraColours, double distance, boolean doLodLoading) {
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPart(null, modelBiped, data, skinPointer.getSkinDye(), extraColours, distance, doLodLoading);
    }
    
    public boolean renderEquipmentPartFromSkinPointer(ISkinDescriptor skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        Skin data = getCustomArmourItemData(skinPointer);
        return renderEquipmentPartRotated(null, data, limb1, limb2, limb3, headY, headX);
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin data, ISkinDye skinDye, ExtraColours extraColours, double distance, boolean doLodLoading) {
        if (data == null) {
            return false;
        }
        IEquipmentModel model = getModelForEquipmentType(data.getSkinType());
        if (model == null) {
            return false;
        }
        GlStateManager.pushAttrib();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        model.render(entity, modelBiped, data, false, skinDye, extraColours, false, distance, doLodLoading);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.popAttrib();
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
