package riskyken.armourersWorkshop.common.command;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.exporter.SkinExportManager;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class CommandExportSkin extends ModCommand {

    @Override
    public String getCommandName() {
        return "exportSkin";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
        if (player == null) {
            return;
        }
        ItemStack stack = player.getCurrentEquippedItem();
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        
        
        SkinExportManager.exportSkin(skin, "ply", new File(System.getProperty("user.dir"), "test.ply"));
    }
}
