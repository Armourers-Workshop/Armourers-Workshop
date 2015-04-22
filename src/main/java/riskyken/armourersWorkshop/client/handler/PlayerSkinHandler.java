package riskyken.armourersWorkshop.client.handler;

import java.util.BitSet;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.utils.UtilPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerSkinHandler {

    public static PlayerSkinHandler INSTANCE;
    
    private HashMap<UUID, PlayerSkinInfo> skinMap = new HashMap<UUID, PlayerSkinInfo>();
    
    public static void init() {
        INSTANCE = new PlayerSkinHandler();
    }
    
    public PlayerSkinHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void setPlayersSkinData(UUID playerId, EntityNakedInfo nakedInfo) {
        if (!skinMap.containsKey(playerId)) {
            skinMap.put(playerId, new PlayerSkinInfo(nakedInfo));
        } else {
            skinMap.get(playerId).setSkinInfo(nakedInfo);
        }
    }
    
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        if (!skinMap.containsKey(playerId)) {
            return null;
        }
        return skinMap.get(playerId);
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (skinMap.containsKey(UtilPlayer.getIDFromPlayer(player))) {
            PlayerSkinInfo skinInfo = skinMap.get(UtilPlayer.getIDFromPlayer(player));
            skinInfo.preRender((AbstractClientPlayer) player, event.renderer);
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        if (skinMap.containsKey(UtilPlayer.getIDFromPlayer(player))) {
            PlayerSkinInfo skinInfo = skinMap.get(UtilPlayer.getIDFromPlayer(player));
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
        
        if (skinMap.containsKey(UtilPlayer.getIDFromPlayer(player))) {
            PlayerSkinInfo skinInfo = skinMap.get(UtilPlayer.getIDFromPlayer(player));
            BitSet armourOverride = skinInfo.getNakedInfo().armourOverride;
            if (armourOverride.get(slot)) {
                result = -2;
            }
        }
        
        event.result = result;
    }
}
