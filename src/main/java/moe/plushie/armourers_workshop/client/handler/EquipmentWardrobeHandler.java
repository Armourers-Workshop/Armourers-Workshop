package moe.plushie.armourers_workshop.client.handler;

import java.util.HashMap;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.data.PlayerPointer;
import moe.plushie.armourers_workshop.common.skin.WardrobeData;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EquipmentWardrobeHandler {
    
    /** Map holding the equipment wardrobe data for all players in tracking range. */
    private final HashMap<PlayerPointer, WardrobeData> equipmentWardrobeMap;
    
    /** Lock object use to keep threads in sync. */
    private final Object threadLock;
    
    public EquipmentWardrobeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        this.equipmentWardrobeMap = new HashMap<PlayerPointer, WardrobeData>();
        this.threadLock = new Object();
    }
    
    public void setEquipmentWardrobeData(PlayerPointer playerPointer, WardrobeData ewd) {
        synchronized (threadLock) {
            if (equipmentWardrobeMap.containsKey(playerPointer)) {
                equipmentWardrobeMap.remove(playerPointer);
            }
            equipmentWardrobeMap.put(playerPointer, ewd);
        }
        
        EntityPlayer localPlayer = Minecraft.getMinecraft().player;
        PlayerPointer localPointer = new PlayerPointer(localPlayer);
        if (playerPointer.equals(localPointer)) {
            ExPropsPlayerSkinData.get(localPlayer).setSkinInfo(ewd, false);
        }
    }
    
    public WardrobeData getEquipmentWardrobeData(PlayerPointer playerPointer) {
        WardrobeData ewd = null;
        synchronized (threadLock) {
            ewd = equipmentWardrobeMap.get(playerPointer);
        }
        return ewd;
    }
    
    public void removeEquipmentWardrobeData(PlayerPointer playerPointer) {
        synchronized (threadLock) {
            if (equipmentWardrobeMap.containsKey(playerPointer)) {
                equipmentWardrobeMap.remove(playerPointer);
            }
        }
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
        
        
        
        //Hide the head overlay if the player has turned it off.
        PlayerPointer playerPointer = new PlayerPointer(player);
        RenderPlayer renderer = event.getRenderer();
        if (equipmentWardrobeMap.containsKey(playerPointer)) {
            WardrobeData ewd = equipmentWardrobeMap.get(playerPointer);
            renderer.getMainModel().bipedHeadwear.isHidden = ewd.headOverlay;
            if (!ewd.headOverlay) {
                if (SkinModelRenderer.INSTANCE.playerHasCustomHead(player)) {
                    renderer.getMainModel().bipedHeadwear.isHidden = true;
                }
            }
            
        }
        
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        ISkinType[] skinTypes = new ISkinType[] {SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet};
        
        
        if (skinCapability != null) {
            for (int i = 0; i < skinTypes.length; i++) {
                ISkinType skinType = skinTypes[i];
                
                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, 0);
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
                    } else if(i == 1) {
                        modelPlayer.bipedBody.isHidden = true;
                        modelPlayer.bipedLeftArm.isHidden = true;
                        modelPlayer.bipedRightArm.isHidden = true;
                    } else if(i == 2) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
                    } else if(i == 3) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
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
        
        //Restore the head overlay.
        PlayerPointer playerPointer = new PlayerPointer(player);
        RenderPlayer renderer = event.getRenderer();
        if (equipmentWardrobeMap.containsKey(playerPointer)) {
            renderer.getMainModel().bipedHeadwear.isHidden = false;
        }
        
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        modelPlayer.bipedHead.isHidden = false;
        modelPlayer.bipedBody.isHidden = false;
        modelPlayer.bipedLeftArm.isHidden = false;
        modelPlayer.bipedRightArm.isHidden = false;
        modelPlayer.bipedLeftLeg.isHidden = false;
        modelPlayer.bipedRightLeg.isHidden = false;
    }
    
    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onRender(RenderPlayerEvent.SetArmorModel event) {
        int slot = -event.getSlot() + 3;
        if (slot > 3) {
            return;
        }
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
        
        int result = -1;
        //Hide the armour if it had been skinned.
        
        ItemStack stack = player.inventory.armorInventory.get(event.getSlot());
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            result = -2;
        }
        /*
        //Hide the armour if the player has turned it off.
        PlayerPointer playerPointer = new PlayerPointer(player);
        if (equipmentWardrobeMap.containsKey(playerPointer)) {
            EquipmentWardrobeData ewd = equipmentWardrobeMap.get(playerPointer);
            if (ewd.armourOverride.get(slot)) {
                result = -2;
            }
        }*/
        event.setResult(result);
    }
}
