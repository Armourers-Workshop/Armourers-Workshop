package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.undo.UndoManager;

public class MessageClientToolPaintBlock implements IMessage, IMessageHandler<MessageClientToolPaintBlock, IMessage> {

    private int x;
    private int y;
    private int z;
    private byte side;
    private byte[] rgbt = new byte[4];
    
    public MessageClientToolPaintBlock() {
    }
    
    public MessageClientToolPaintBlock(int x, int y, int z, byte side, byte[] rgbt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.rgbt = rgbt;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(side);
        buf.writeBytes(rgbt);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        side = buf.readByte();
        buf.readBytes(rgbt);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public IMessage onMessage(MessageClientToolPaintBlock message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player != null && player.getEntityWorld() != null) {
            World world = player.getEntityWorld();
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IPantableBlock) {
                UndoManager.begin(player);
                IPantableBlock paintable = (IPantableBlock) block;
                int oldColour = paintable.getColour(world, message.x, message.y, message.z, message.side);
                PaintType oldPaintType = paintable.getPaintType(world, message.x, message.y, message.z, message.side);
                UndoManager.blockPainted(player, world, message.x, message.y, message.z, oldColour, (byte)oldPaintType.getKey(), message.side);
                paintable.setColour(world, message.x, message.y, message.z, message.rgbt, message.side);
                paintable.setPaintType(world, message.x, message.y, message.z, PaintType.getPaintTypeFormSKey(message.rgbt[3]), message.side);
                UndoManager.end(player);
            }
        }
        return null;
    }
}
