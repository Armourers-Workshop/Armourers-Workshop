package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.undo.UndoManager;

public class MessageClientToolPaintBlock implements IMessage, IMessageHandler<MessageClientToolPaintBlock, IMessage> {

    private BlockPos pos;
    private int x;
    private int y;
    private int z;
    private EnumFacing side;
    private byte[] rgbt = new byte[4];
    
    public MessageClientToolPaintBlock() {
    }
    
    public MessageClientToolPaintBlock(BlockPos pos, EnumFacing side, byte[] rgbt) {
        this.pos = pos;
        this.side = side;
        this.rgbt = rgbt;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeBlockPos(buf, pos);
        buf.writeByte(side.ordinal());
        buf.writeBytes(rgbt);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        pos = ByteBufHelper.readBlockPos(buf);
        side = EnumFacing.values()[buf.readByte()];
        buf.readBytes(rgbt);
    }
    
    @Override
    public IMessage onMessage(MessageClientToolPaintBlock message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player != null && player.getEntityWorld() != null) {
            World world = player.getEntityWorld();
            IBlockState blockState = world.getBlockState(message.pos);
            if (blockState.getBlock() instanceof IPantableBlock) {
                UndoManager.begin(player);
                IPantableBlock paintable = (IPantableBlock) blockState.getBlock();
                int oldColour = paintable.getColour(world, message.pos, message.side);
                PaintType oldPaintType = paintable.getPaintType(world, message.pos, message.side);
                UndoManager.blockPainted(player, world, message.pos, oldColour, (byte)oldPaintType.getKey(), message.side);
                paintable.setColour(world, message.pos, message.rgbt, message.side);
                paintable.setPaintType(world, message.pos, PaintType.getPaintTypeFormSKey(message.rgbt[3]), message.side);
                UndoManager.end(player);
            }
        }
        return null;
    }
}
