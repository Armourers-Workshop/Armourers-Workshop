package moe.plushie.armourers_workshop.common.command;

import java.io.File;
import java.util.List;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.exporter.ISkinExporter;
import moe.plushie.armourers_workshop.common.skin.exporter.SkinExportManager;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandExportSkin extends ModCommand {

    public CommandExportSkin(ModCommand parent) {
        super(parent, "export_skin");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, SkinExportManager.getExporters());
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }

        // Check if the player is holding a valid skin.
        ItemStack stack = player.getHeldItemMainhand();
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        // Get export file extension.
        String fileExtension = args[1];
        ISkinExporter skinExporter = SkinExportManager.getSkinExporter(fileExtension);
        if (skinExporter == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        // Get export file name.
        String exportName = args[2];
        if (!exportName.substring(0, 1).equals("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object) exportName);
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
            throw new WrongUsageException(getUsage(sender), (Object) exportName);
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
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        // Creating the export directory.
        File exportDir = new File(ArmourersWorkshop.getProxy().getModDirectory(), "model-exports");
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        SkinExportManager.exportSkin(skin, skinExporter, exportDir, exportName, scale);
    }
}
