package riskyken.armourersWorkshop.common.network.messages.client;

import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;

public class MessageClientGuiToolOptionUpdate implements IMessage, IMessageHandler<MessageClientGuiToolOptionUpdate, IMessage> {

    private NBTTagCompound toolOptions;
    
    public MessageClientGuiToolOptionUpdate() { }
    
    public MessageClientGuiToolOptionUpdate(NBTTagCompound toolOptions) {
        this.toolOptions = toolOptions;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.toolOptions = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.toolOptions);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiToolOptionUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player != null) {
            ItemStack stack = player.getCurrentEquippedItem();
            Item item = stack.getItem();
            
            if (item instanceof IConfigurableTool) {
                NBTTagCompound newOptions = message.toolOptions;
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }
                NBTTagCompound stackCompound = stack.getTagCompound();
                Set keySet = newOptions.func_150296_c();
                
                Iterator iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (stackCompound.hasKey(key)) {
                        stackCompound.removeTag(key);
                    }
                    stackCompound.setTag(key, newOptions.getTag(key));
                }
            }
        }
        return null;
    }
}
