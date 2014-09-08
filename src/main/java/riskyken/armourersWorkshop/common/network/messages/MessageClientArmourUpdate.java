package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.custom.equipment.armour.CustomArmourManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientArmourUpdate implements IMessage, IMessageHandler<MessageClientArmourUpdate, IMessage> {

    byte slotId;
    boolean added;
    
    public MessageClientArmourUpdate() { }
    
    public MessageClientArmourUpdate(byte slotId, boolean added) {
        this.slotId = slotId;
        this.added = added;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotId = buf.readByte();
        this.added = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slotId);
        buf.writeBoolean(added);
    }
    
    @Override
    public IMessage onMessage(MessageClientArmourUpdate message, MessageContext ctx) {
        CustomArmourManager.playerArmourSlotUpdate(ctx.getServerHandler().playerEntity, message.slotId, message.added);
        return null;
    }
}
