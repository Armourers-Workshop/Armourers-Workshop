package riskyken.armourersWorkshop.common.command;

import java.io.File;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.exporter.ISkinExporter;
import riskyken.armourersWorkshop.common.skin.exporter.SkinExportManager;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class CommandExportSkin extends ModCommand {

    @Override
    public String getName() {
        return "exportSkin";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        
        // Check if the player is holding a valid skin.
        ItemStack stack = player.getHeldItemMainhand();
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        
        // Get export file extension.
        String fileExtension = args[1];
        ISkinExporter skinExporter = SkinExportManager.getSkinExporter(fileExtension);
        if (skinExporter == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        // Get export file name.
        String exportName = args[2];
        if (!exportName.substring(0, 1).equals("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)exportName);
        }
        int usedCommands = 2;
        if (!exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
            for (int i = 3; i < args.length; i++) {
                exportName += " " + args[i];
                if (exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
                    usedCommands = i;
                    break;
                }
            }
        }   
        if (!exportName.substring(exportName.length() - 1, exportName.length()).equals("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object)exportName);
        }
        exportName = exportName.replace("\"", "");
        
        // Add the scale
        float scale = 0.0625F;
        if (args.length > usedCommands + 1) {
            scale = (float) parseDouble(args[usedCommands + 1]);
        }
        
        // Get the skin from the cache.
        // TODO Fix client call.
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        // Creating the export directory.
        File exportDir = new File(System.getProperty("user.dir"), "model-exports");
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        
        SkinExportManager.exportSkin(skin, skinExporter, new File(exportDir, String.format("%s.%s", exportName, fileExtension)), scale);
    }
}
