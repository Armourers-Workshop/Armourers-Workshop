package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.utils.ModLogger;

public class CommandSetItemAsSkinnable extends ModCommand {

    @Override
    public String getName() {
        return "setItemAsSkinnable";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null) {
            Configuration config = ConfigHandler.config;
            
            Property prop = config.get(ConfigHandler.CATEGORY_COMPATIBILITY, "itemOverrides", ModAddonManager.getDefaultOverrides());
            String[] itemOverrides = prop.getStringList();
            String[] newItemOverrides = new String[itemOverrides.length + 1];
            System.arraycopy(itemOverrides, 0, newItemOverrides, 0, itemOverrides.length);
            UniqueIdentifier uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(stack.getItem());
            
            newItemOverrides[newItemOverrides.length - 1] = "sword:" + uniqueIdentifier.toString();
            
            ModLogger.log(String.format("Setting item %s as skinnable.", uniqueIdentifier.toString()));
            
            prop.set(newItemOverrides);
            config.save();
        }
    }
}
