package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.equipment.data.EquipmentSkinTypeData;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiLoadSaveArmour implements IMessage, IMessageHandler<MessageClientGuiLoadSaveArmour, IMessage> {

    Byte type;
    String filename;
    EquipmentSkinTypeData itemData;
    boolean load;
    
    public MessageClientGuiLoadSaveArmour() { }
    
    public MessageClientGuiLoadSaveArmour(EquipmentSkinTypeData itemData, boolean load) {
        this.type = 1;
        this.itemData = itemData;
        this.load = load;
    }
    
    public MessageClientGuiLoadSaveArmour(String filename, boolean load) {
        this.type = 0;
        this.filename = filename;
        this.load = load;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readByte();
        this.load = buf.readBoolean();
        if (type == 0) {
            this.filename = ByteBufUtils.readUTF8String(buf);
        } else {
            itemData = new EquipmentSkinTypeData(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.type);
        buf.writeBoolean(this.load);
        if (type == 0) {
            ByteBufUtils.writeUTF8String(buf, this.filename);
        } else {
            itemData.writeToBuf(buf);
        }
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiLoadSaveArmour message,MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourLibrary) {
            TileEntityArmourLibrary te = ((ContainerArmourLibrary) container).getTileEntity();
            if (message.load) {
                if (message.type == 0) {
                    te.loadArmour(message.filename, player);
                } else {
                    te.loadArmour(message.itemData, player);
                }
                
            } else {
                te.saveArmour(message.filename, player);
            }
            
            ((ContainerArmourLibrary)container).sentList = false;
        }
        return null;
    }
}
