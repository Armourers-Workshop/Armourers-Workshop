package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EquipmentWardrobeHandler {
    
    public EquipmentWardrobeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private static ItemStack[] armour = new ItemStack[4];
    
    public static ItemStack getArmourInSlot(int slotId) {
        return armour[slotId];
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        /*
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        */
        if (player.getGameProfile() == null) {
            return;
        }
        if (player instanceof FakePlayer) {
            return;
        }
        
        for (int i = 0; i < armour.length; i++) {
            armour[i] = ItemStack.EMPTY;
        }
        
        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
        if (wardrobeCapability != null) {
            for (int i = 0; i < armour.length; i++) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.values()[i + 2];
                armour[i] = player.inventory.armorInventory.get(i);
                if (wardrobeCapability.getArmourOverride(slot)) {
                    player.inventory.armorInventory.set(i, ItemStack.EMPTY);
                }
            }
        }

        
        // Hide the head overlay if the player has turned it off.
        RenderPlayer renderer = event.getRenderer();
        if (SkinModelRenderer.INSTANCE.playerHasCustomHead(player)) {
            renderer.getMainModel().bipedHeadwear.isHidden = true;
        }
        
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        ISkinType[] skinTypes = new ISkinType[] {SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet};
        
        if (skinCapability != null) {
            for (int i = 0; i < skinTypes.length; i++) {
                ISkinType skinType = skinTypes[i];
                
                ISkinDescriptor skinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(armour[3 - i]);
                
                if (skinDescriptor == null) {
                    skinDescriptor = skinCapability.getSkinDescriptor(skinType, 0);
                }
                
                if (skinDescriptor == null) {
                    continue;
                }
                
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin == null) {
                    continue;
                }
                if (SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skin.getProperties())) {
                    if (i == 0) {
                        modelPlayer.bipedHead.isHidden = true;
                        modelPlayer.bipedHeadwear.isHidden = true;
                    } else if(i == 1) {
                        modelPlayer.bipedBody.isHidden = true;
                        modelPlayer.bipedBodyWear.isHidden = true;
                        modelPlayer.bipedLeftArm.isHidden = true;
                        modelPlayer.bipedLeftArmwear.isHidden = true;
                        modelPlayer.bipedRightArm.isHidden = true;
                        modelPlayer.bipedLeftArmwear.isHidden = true;
                    } else if(i == 2) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedLeftLegwear.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
                        modelPlayer.bipedRightLegwear.isHidden = true;
                    } else if(i == 3) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedLeftLegwear.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
                        modelPlayer.bipedRightLegwear.isHidden = true;
                    }
                }
                
                
            }
        }

    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        /*if (player instanceof MannequinFakePlayer) {
            return;
        }*/
        if (player.getGameProfile() == null) {
            return;
        }
        if (player instanceof FakePlayer) {
            return;
        }
        
        for (int i = 0; i < armour.length; i++) {
            player.inventory.armorInventory.set(i, armour[i]);
        }
        
        //Restore the head overlay.
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        modelPlayer.bipedHead.isHidden = false;
        modelPlayer.bipedHeadwear.isHidden = false;
        
        modelPlayer.bipedBody.isHidden = false;
        modelPlayer.bipedBodyWear.isHidden = false;
        modelPlayer.bipedLeftArm.isHidden = false;
        modelPlayer.bipedLeftArmwear.isHidden = false;
        modelPlayer.bipedRightArm.isHidden = false;
        modelPlayer.bipedLeftArmwear.isHidden = false;

        modelPlayer.bipedLeftLeg.isHidden = false;
        modelPlayer.bipedLeftLegwear.isHidden = false;
        modelPlayer.bipedRightLeg.isHidden = false;
        modelPlayer.bipedRightLegwear.isHidden = false;
    }
}
