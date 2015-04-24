package riskyken.armourersWorkshop.client.handler;

import java.util.BitSet;
import java.util.HashMap;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerSkinHandler {

    public static PlayerSkinHandler INSTANCE;
    
    private HashMap<PlayerPointer, PlayerSkinInfo> skinMap = new HashMap<PlayerPointer, PlayerSkinInfo>();
    
    public static void init() {
        INSTANCE = new PlayerSkinHandler();
    }
    
    public PlayerSkinHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void setPlayersSkinData(PlayerPointer playerPointer, EntityNakedInfo nakedInfo) {
        if (!skinMap.containsKey(playerPointer)) {
            skinMap.put(playerPointer, new PlayerSkinInfo(nakedInfo));
        } else {
            skinMap.get(playerPointer).setSkinInfo(nakedInfo);
        }
    }
    
    public PlayerSkinInfo getPlayersNakedData(PlayerPointer playerPointer) {
        if (!skinMap.containsKey(playerPointer)) {
            return null;
        }
        return skinMap.get(playerPointer);
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
        
        if (skinMap.containsKey(playerPointer)) {
            PlayerSkinInfo skinInfo = skinMap.get(playerPointer);
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
        
        if (skinMap.containsKey(playerPointer)) {
            PlayerSkinInfo skinInfo = skinMap.get(playerPointer);
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
        PlayerPointer playerPointer = new PlayerPointer(player);
        
        if (skinMap.containsKey(playerPointer)) {
            PlayerSkinInfo skinInfo = skinMap.get(playerPointer);
            BitSet armourOverride = skinInfo.getNakedInfo().armourOverride;
            if (armourOverride.get(slot)) {
                result = -2;
            }
        }
        
        event.result = result;
    }
}
