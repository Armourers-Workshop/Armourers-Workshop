package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.UtilItems;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiToolOptionUpdate implements IMessage, IMessageHandler<MessageClientGuiToolOptionUpdate, IMessage> {

    byte toolOption;
    int value;
    
    public MessageClientGuiToolOptionUpdate() { }
    
    public MessageClientGuiToolOptionUpdate(byte toolOption, int value) {
        this.toolOption = toolOption;
        this.value = value;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        toolOption = buf.readByte();
        value = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(toolOption);
        buf.writeInt(value);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiToolOptionUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player != null) {
            ItemStack stack = player.getCurrentEquippedItem();
            
            if (message.toolOption == 0) {
                if (stack != null && stack.getItem() instanceof AbstractModItem) {
                    UtilItems.setIntensityOnStack(stack, message.value);
                }
            }
            if (message.toolOption == 1) {
                if (stack != null && stack.getItem() == ModItems.colourPicker) {
                    ((ItemColourPicker)stack.getItem()).setToolColour(stack, message.value);
                }
            }
        }
        return null;
    }
}
