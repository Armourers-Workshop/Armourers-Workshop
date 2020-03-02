package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientToolPaintBlock implements IMessage, IMessageHandler<MessageClientToolPaintBlock, IMessage> {

    private int x;
    private int y;
    private int z;
    private EnumFacing facing;
    private byte[] rgbt = new byte[4];
    
    public MessageClientToolPaintBlock() {
    }
    
    public MessageClientToolPaintBlock(BlockPos pos, EnumFacing facing, byte[] rgbt) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.facing = facing;
        this.rgbt = rgbt;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte((byte)facing.ordinal());
        buf.writeBytes(rgbt);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        facing = EnumFacing.VALUES[buf.readByte()];
        buf.readBytes(rgbt);
    }
    
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
                int oldColour = paintable.getColour(world, pos, message.facing);
                IPaintType oldPaintType = paintable.getPaintType(world, pos, message.facing);
                UndoManager.blockPainted(player, world, pos, oldColour, (byte)oldPaintType.getId(), message.facing);
                paintable.setColour(world, pos, message.rgbt, message.facing);
                paintable.setPaintType(world, pos, PaintTypeRegistry.getInstance().getPaintTypeFormByte(message.rgbt[3]), message.facing);
                UndoManager.end(player);
            }
        }
        return null;
    }
}
