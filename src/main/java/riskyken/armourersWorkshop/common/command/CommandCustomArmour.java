package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.custom.equipment.armour.CustomArmourManager;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;

public class CommandCustomArmour extends CommandBase {

    @Override
    public String getCommandName() {
        return "customarmour";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.customarmour.usage";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"build", "clear"}) : null;
    }
    
    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
        
        if (args == null | player == null) {
            throw new WrongUsageException("commands.customarmour.usage", (Object)args);
        }
        
        if (args.length != 1) {
            throw new WrongUsageException("commands.customarmour.usage", (Object)args);
        }
        
        if (args[0].equalsIgnoreCase("clear")) {
            CustomArmourManager.removeAllCustomArmourData(player);
            return;
        }
        
        if (args[0].equalsIgnoreCase("build")) {
            for (int ix = 0; ix < TileEntityArmourerBrain.MULTI_BLOCK_SIZE; ix++) {
                for (int iz = 0; iz < TileEntityArmourerBrain.MULTI_BLOCK_SIZE; iz++) {
                    if (ix == 0 | iz == 0 |ix == TileEntityArmourerBrain.MULTI_BLOCK_SIZE - 1 |
                            iz == TileEntityArmourerBrain.MULTI_BLOCK_SIZE - 1) {
                        player.worldObj.setBlock((int) player.posX + ix, (int) player.posY,(int) player.posZ + iz, ModBlocks.armourerMultiBlock);
                    }
                    
                }
            }
            player.worldObj.setBlock((int) player.posX + 1, (int) player.posY,(int) player.posZ, ModBlocks.armourerBrain);
            
            TileEntity te = player.worldObj.getTileEntity((int) player.posX + 1, (int) player.posY,(int) player.posZ);
            if (te != null && te instanceof TileEntityArmourerBrain) {
                ((TileEntityArmourerBrain)te).setGameProfile(player.getGameProfile());
            }
            return;
        }
        
        
        throw new WrongUsageException("commands.customarmour.usage", (Object)args);
    }

}
