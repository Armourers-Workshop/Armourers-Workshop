package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerAddArmourData implements IMessage, IMessageHandler<MessageServerAddArmourData, IMessage> {

    String entityName;
    CustomArmourItemData armourData;
    
    public MessageServerAddArmourData(String entityName, CustomArmourItemData armourData) {
        this.entityName = entityName;
        this.armourData = armourData;
    }
    
    public MessageServerAddArmourData() {}
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityName = ByteBufUtils.readUTF8String(buf);
        armourData = new CustomArmourItemData(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, entityName);
        armourData.writeToBuf(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerAddArmourData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addCustomArmour(message.entityName, message.armourData);
        return null;
    }
}
