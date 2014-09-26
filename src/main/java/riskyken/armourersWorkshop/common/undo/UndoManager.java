package riskyken.armourersWorkshop.common.undo;

import java.util.HashMap;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

/**
 *
 * @author RiskyKen
 *
 */
public final class UndoManager {

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
            player.addChatMessage(new ChatComponentText(I18n.format("chat." + LibModInfo.ID.toLowerCase() + ":undo.outOfUndos")));
            return;
        }
        PlayerUndoData playerData = playerUndoData.get(key);
        World world = player.worldObj;
        player.addChatMessage(new ChatComponentText(I18n.format("chat." + LibModInfo.ID.toLowerCase() + ":undo.undoing")));
        playerData.playerPressedUndo(world);
        if (playerData.getAvalableUndos() < 1) {
            playerUndoData.remove(key);
        }
    }
}
