package riskyken.armourersWorkshop.common.command;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.config.ConfigHandlerOverrides;
import riskyken.armourersWorkshop.utils.ModLogger;

public class CommandSetItemAsSkinnable extends ModCommand {

    @Override
    public String getCommandName() {
        return "setItemAsSkinnable";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
        if (player == null) {
            return;
        }
        
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null) {
            Configuration config = ConfigHandlerOverrides.config;
            
            Property prop = config.get(ConfigHandlerOverrides.CATEGORY_OVERRIDES, "itemOverrides", ModAddonManager.getDefaultOverrides());
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
