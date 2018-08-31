package riskyken.armourers_workshop.client.handler;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.client.render.SkinModelRenderer;
import riskyken.armourers_workshop.common.data.PlayerPointer;
import riskyken.armourers_workshop.common.skin.EquipmentWardrobeData;
import riskyken.armourers_workshop.common.skin.ExPropsPlayerSkinData;

@SideOnly(Side.CLIENT)
public final class EquipmentWardrobeHandler {
    
    /** Map holding the equipment wardrobe data for all players in tracking range. */
    private final HashMap<PlayerPointer, EquipmentWardrobeData> equipmentWardrobeMap;
    
    /** Lock object use to keep threads in sync. */
    private final Object threadLock;
    
    public EquipmentWardrobeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        this.equipmentWardrobeMap = new HashMap<PlayerPointer, EquipmentWardrobeData>();
        this.threadLock = new Object();
    }
    
    public void setEquipmentWardrobeData(PlayerPointer playerPointer, EquipmentWardrobeData ewd) {
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
    
    public EquipmentWardrobeData getEquipmentWardrobeData(PlayerPointer playerPointer) {
        EquipmentWardrobeData ewd = null;
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
            EquipmentWardrobeData ewd = equipmentWardrobeMap.get(playerPointer);
            renderer.getMainModel().bipedHeadwear.isHidden = ewd.headOverlay;
            if (!ewd.headOverlay) {
                if (SkinModelRenderer.INSTANCE.playerHasCustomHead(player)) {
                    renderer.getMainModel().bipedHeadwear.isHidden = true;
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
        /*
        ItemStack stack = player.getArmorInventoryList().(event.getSlot());
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            result = -2;
        }
        
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
