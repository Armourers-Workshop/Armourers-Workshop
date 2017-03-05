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

    public static void begin(EntityPlayer player) {
        if (!playerUndoData.containsKey(player.getCommandSenderName())) {
            playerUndoData.put(player.getCommandSenderName(), new PlayerUndoData(player));
        }
        PlayerUndoData playerData = playerUndoData.get(player.getCommandSenderName());
        playerData.begin();
    }
    
    public static void end(EntityPlayer player) {
        if (!playerUndoData.containsKey(player.getCommandSenderName())) {
            playerUndoData.put(player.getCommandSenderName(), new PlayerUndoData(player));
        }
        PlayerUndoData playerData = playerUndoData.get(player.getCommandSenderName());
        playerData.end();
    }
    
    @Deprecated()
    public static void blockPainted(EntityPlayer player, World world, int x, int y, int z, int oldColour, byte oldPaintType, int side) {
        byte[] oldrgb = new byte[3];
        oldrgb[0] = (byte) ((oldColour >> 16) & 0xFF);
        oldrgb[1] = (byte) ((oldColour >> 8) & 0xFF);
        oldrgb[2] = (byte) ((oldColour) & 0xFF);
        blockPainted(player, world, x, y, z, oldrgb, oldPaintType, side);
    }
    
    public static void blockPainted(EntityPlayer player, World world, int x, int y, int z, byte[] oldrgb, byte oldPaintType, int side) {
        UndoData undoData = new UndoData(x, y, z, world.provider.dimensionId, oldrgb, oldPaintType, side);
        if (!playerUndoData.containsKey(player.getCommandSenderName())) {
            playerUndoData.put(player.getCommandSenderName(), new PlayerUndoData(player));
        }
        
        PlayerUndoData playerData = playerUndoData.get(player.getCommandSenderName());
        playerData.addUndoData(undoData);
    }
    
    public static void undoPressed(EntityPlayer player) {
        String key = player.getCommandSenderName();
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
