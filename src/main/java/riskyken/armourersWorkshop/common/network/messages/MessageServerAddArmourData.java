package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerAddArmourData implements IMessage, IMessageHandler<MessageServerAddArmourData, IMessage> {

    UUID playerId;
    CustomArmourItemData armourData;
    
    public MessageServerAddArmourData(UUID playerId, CustomArmourItemData armourData) {
        this.playerId = playerId;
        this.armourData = armourData;
    }
    
    public MessageServerAddArmourData() {}
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
        armourData = new CustomArmourItemData(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeUUID(buf, this.playerId);
        armourData.writeToBuf(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerAddArmourData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addCustomArmour(message.playerId, message.armourData);
        return null;
    }
}
