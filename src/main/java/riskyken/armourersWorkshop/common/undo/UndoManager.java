package riskyken.armourersWorkshop.common.undo;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

/**
 *
 * @author RiskyKen
 *
 */
public final class UndoManager {

    public static int maxUndos = 50;
    
    private static HashMap<String, PlayerUndoData> playerUndoData = new HashMap<String, PlayerUndoData>();

    public static void playerPaintedBlock(EntityPlayer player, World world, int x, int y, int z, int oldColour) {
        UndoData undoData = new UndoData(x, y, z, world.provider.dimensionId, oldColour);
        if (!playerUndoData.containsKey(player.getDisplayName())) {
            playerUndoData.put(player.getDisplayName(), new PlayerUndoData(player));
        }
        
        PlayerUndoData playerData = playerUndoData.get(player.getDisplayName());
        playerData.addUndoData(undoData);
    }
    
    public static void playerPressedUndo(EntityPlayer player) {
        String key = player.getDisplayName();
        if (!playerUndoData.containsKey(key)) {
            String outOfUndosText = StatCollector.translateToLocal("chat." + LibModInfo.ID.toLowerCase() + ":undo.outOfUndos");
            player.addChatMessage(new ChatComponentText(outOfUndosText));
            return;
        }
        PlayerUndoData playerData = playerUndoData.get(key);
        World world = player.worldObj;
        String undoText = StatCollector.translateToLocal("chat." + LibModInfo.ID.toLowerCase() + ":undo.undoing");
        player.addChatMessage(new ChatComponentText(undoText));
        playerData.playerPressedUndo(world);
        if (playerData.getAvalableUndos() < 1) {
            playerUndoData.remove(key);
        }
    }
}
