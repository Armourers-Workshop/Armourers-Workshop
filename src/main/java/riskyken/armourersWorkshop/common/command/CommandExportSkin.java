package riskyken.armourersWorkshop.common.command;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.exporter.ISkinExporter;
import riskyken.armourersWorkshop.common.skin.exporter.SkinExportManager;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class CommandExportSkin extends ModCommand {

    @Override
    public String getCommandName() {
        return "exportSkin";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length < 3) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
        if (player == null) {
            return;
        }
        
        // Check if the player is holding a valid skin.
        ItemStack stack = player.getCurrentEquippedItem();
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        
        // Get export file extension.
        String fileExtension = currentCommand[1];
        ISkinExporter skinExporter = SkinExportManager.getSkinExporter(fileExtension);
        if (skinExporter == null) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        // Get export file name.
        String exportName = currentCommand[2];
        if (!exportName.substring(0, 1).equals("\"")) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)exportName);
        }
        int usedCommands = 2;
        if (!exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
            for (int i = 3; i < currentCommand.length; i++) {
                exportName += " " + currentCommand[i];
                if (exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
                    usedCommands = i;
                    break;
                }
            }
        }   
        if (!exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)exportName);
        }
        exportName = exportName.replace("\"", "");
        
        // Add the scale
        float scale = 0.0625F;
        if (currentCommand.length > usedCommands + 1) {
            scale = (float) parseDouble(commandSender, currentCommand[usedCommands + 1]);
        }
        
        // Get the skin from the cache.
        // TODO Fix client call.
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        // Creating the export directory.
        File exportDir = new File(System.getProperty("user.dir"), "model-exports");
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        
        SkinExportManager.exportSkin(skin, skinExporter, new File(exportDir, String.format("%s.%s", exportName, fileExtension)), scale);
    }
}
