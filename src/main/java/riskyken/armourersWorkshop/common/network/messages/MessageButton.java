package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageButton implements IMessage, IMessageHandler<MessageButton, IMessage>{

	short buttonId;
	
	public MessageButton() {}
	
	public MessageButton(short buttonId) {
		this.buttonId = buttonId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.buttonId = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(this.buttonId);
	}

	@Override
	public IMessage onMessage(MessageButton message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		
		if (player == null) {
			return null;
		}
		
		Container container = player.openContainer;
		
		/*
		if (container != null && container instanceof ContainerHollower)
		{
			TileEntityDeviceHollower deviceHollower = ((ContainerHollower)container).getTileEntity();
			deviceHollower.receiveButtonEvent(message.buttonId);
		}
		*/
		return null;
	}

}
