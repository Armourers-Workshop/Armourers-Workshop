package riskyken.armourersWorkshop.client.handler;

import java.util.BitSet;
import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.PlayerTextureInfo;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.PlayerEquipmentWardrobeData;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

@SideOnly(Side.CLIENT)
public class PlayerTextureHandler {

    public static PlayerTextureHandler INSTANCE;
    
    private HashMap<PlayerPointer, PlayerTextureInfo> playerTextureMap = new HashMap<PlayerPointer, PlayerTextureInfo>();
    
    public static void init() {
        INSTANCE = new PlayerTextureHandler();
    }
    
    public PlayerTextureHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void setPlayersSkinData(PlayerPointer playerPointer, PlayerEquipmentWardrobeData nakedInfo) {
        if (!playerTextureMap.containsKey(playerPointer)) {
            playerTextureMap.put(playerPointer, new PlayerTextureInfo(nakedInfo));
        } else {
            playerTextureMap.get(playerPointer).setSkinInfo(nakedInfo);
        }
    }
    
    public PlayerTextureInfo getPlayersNakedData(PlayerPointer playerPointer) {
        if (!playerTextureMap.containsKey(playerPointer)) {
            return null;
        }
        return playerTextureMap.get(playerPointer);
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerTextureMap.containsKey(playerPointer)) {
            PlayerTextureInfo skinInfo = playerTextureMap.get(playerPointer);
            skinInfo.preRender((AbstractClientPlayer) player, event.renderer);
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (player.getGameProfile() == null) {
            return;
        }
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerTextureMap.containsKey(playerPointer)) {
            PlayerTextureInfo skinInfo = playerTextureMap.get(playerPointer);
            skinInfo.postRender((AbstractClientPlayer) player, event.renderer);
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.SetArmorModel event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        int result = -1;
        
        int slot = -event.slot + 3;
        if (slot > 3) {
            return;
        }
        
        if (player.getGameProfile() == null) {
            return;
        }
        
        //Hide the armour if it had been skinned.
        ItemStack stack = player.getCurrentArmor(event.slot);
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            result = -2;
        }
        
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (playerTextureMap.containsKey(playerPointer)) {
            PlayerTextureInfo textureInfo = playerTextureMap.get(playerPointer);
            BitSet armourOverride = textureInfo.getEquipmentWardrobeData().armourOverride;
            if (armourOverride.get(slot)) {
                result = -2;
            }
        }
        
        event.result = result;
    }
}
