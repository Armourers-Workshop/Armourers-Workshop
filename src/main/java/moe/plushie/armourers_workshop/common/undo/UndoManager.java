package moe.plushie.armourers_workshop.common.undo;

import java.util.HashMap;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 *
 * @author RiskyKen
 *
 */
public final class UndoManager {

    public static int maxUndos = 50;
    
    private static HashMap<String, PlayerUndoData> playerUndoData = new HashMap<String, PlayerUndoData>();

    public static void begin(EntityPlayer player) {
        if (!playerUndoData.containsKey(player.getName())) {
            playerUndoData.put(player.getName(), new PlayerUndoData(player));
        }
        PlayerUndoData playerData = playerUndoData.get(player.getName());
        playerData.begin();
    }
    
    public static void end(EntityPlayer player) {
        if (!playerUndoData.containsKey(player.getName())) {
            playerUndoData.put(player.getName(), new PlayerUndoData(player));
        }
        PlayerUndoData playerData = playerUndoData.get(player.getName());
        playerData.end();
    }
    
    @Deprecated()
    public static void blockPainted(EntityPlayer player, World world, BlockPos pos, int oldColour, byte oldPaintType, EnumFacing facing) {
        blockPainted(player, world, pos.getX(), pos.getY(), pos.getZ(), oldColour, oldPaintType, facing);
    }
    
    @Deprecated()
    public static void blockPainted(EntityPlayer player, World world, int x, int y, int z, int oldColour, byte oldPaintType, EnumFacing facing) {
        byte[] oldrgb = new byte[3];
        oldrgb[0] = (byte) ((oldColour >> 16) & 0xFF);
        oldrgb[1] = (byte) ((oldColour >> 8) & 0xFF);
        oldrgb[2] = (byte) ((oldColour) & 0xFF);
        blockPainted(player, world, x, y, z, oldrgb, oldPaintType, facing);
    }
    
    public static void blockPainted(EntityPlayer player, World world, int x, int y, int z, byte[] oldrgb, byte oldPaintType, EnumFacing facing) {
        UndoData undoData = new UndoData(x, y, z, world.provider.getDimension(), oldrgb, oldPaintType, facing);
        if (!playerUndoData.containsKey(player.getName())) {
            playerUndoData.put(player.getName(), new PlayerUndoData(player));
        }
        
        PlayerUndoData playerData = playerUndoData.get(player.getName());
        playerData.addUndoData(undoData);
    }
    
    public static void undoPressed(EntityPlayer player) {
        String key = player.getName();
        if (!playerUndoData.containsKey(key)) {
            String outOfUndosText = I18n.format("chat." + LibModInfo.ID.toLowerCase() + ":undo.outOfUndos");
            player.sendMessage(new TextComponentString(outOfUndosText));
            return;
        }
        PlayerUndoData playerData = playerUndoData.get(key);
        World world = player.getEntityWorld();
        String undoText = I18n.format("chat." + LibModInfo.ID.toLowerCase() + ":undo.undoing");
        player.sendMessage(new TextComponentString(undoText));
        playerData.playerPressedUndo(world);
        if (playerData.getAvalableUndos() < 1) {
            playerUndoData.remove(key);
        }
    }
}
