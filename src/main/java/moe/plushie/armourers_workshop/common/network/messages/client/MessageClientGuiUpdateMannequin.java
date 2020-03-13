package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiUpdateMannequin implements IMessage {

    private int id;
    private BipedRotations bipedRotations = null;
    private TextureData textureData = null;
    private Boolean extraRenders = null;
    private Boolean flying = null;
    private Boolean visible = null;
    private Boolean noClip = null;
    private Vec3d offset = null;

    public MessageClientGuiUpdateMannequin() {
    }

    public MessageClientGuiUpdateMannequin(EntityMannequin entityMannequin) {
        this.id = entityMannequin.getEntityId();
    }

    public MessageClientGuiUpdateMannequin setBipedRotations(BipedRotations bipedRotations) {
        this.bipedRotations = bipedRotations;
        return this;
    }

    public MessageClientGuiUpdateMannequin setTextureData(TextureData textureData) {
        this.textureData = textureData;
        return this;
    }

    public MessageClientGuiUpdateMannequin setExtraRenders(boolean checked) {
        extraRenders = Boolean.valueOf(checked);
        return this;
    }

    public MessageClientGuiUpdateMannequin setFlying(boolean checked) {
        flying = Boolean.valueOf(checked);
        return this;
    }

    public MessageClientGuiUpdateMannequin setVisible(boolean checked) {
        visible = Boolean.valueOf(checked);
        return this;
    }

    public MessageClientGuiUpdateMannequin setNoClip(boolean checked) {
        noClip = Boolean.valueOf(checked);
        return this;
    }

    public MessageClientGuiUpdateMannequin setOffset(Vec3d offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);

        if (bipedRotations != null) {
            buf.writeBoolean(true);
            bipedRotations.writeToBuf(buf);
        } else {
            buf.writeBoolean(false);
        }

        if (textureData != null) {
            buf.writeBoolean(true);
            textureData.writeToBuf(buf);
        } else {
            buf.writeBoolean(false);
        }

        if (extraRenders != null) {
            buf.writeBoolean(true);
            buf.writeBoolean(extraRenders.booleanValue());
        } else {
            buf.writeBoolean(false);
        }

        if (flying != null) {
            buf.writeBoolean(true);
            buf.writeBoolean(flying.booleanValue());
        } else {
            buf.writeBoolean(false);
        }

        if (visible != null) {
            buf.writeBoolean(true);
            buf.writeBoolean(visible.booleanValue());
        } else {
            buf.writeBoolean(false);
        }

        if (noClip != null) {
            buf.writeBoolean(true);
            buf.writeBoolean(noClip.booleanValue());
        } else {
            buf.writeBoolean(false);
        }

        if (offset != null) {
            buf.writeBoolean(true);
            buf.writeDouble(offset.x);
            buf.writeDouble(offset.y);
            buf.writeDouble(offset.z);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        if (buf.readBoolean()) {
            bipedRotations = new BipedRotations();
            bipedRotations.readFromBuf(buf);
        }

        if (buf.readBoolean()) {
            textureData = new TextureData();
            textureData.readFromBuf(buf);
        }

        if (buf.readBoolean()) {
            extraRenders = Boolean.valueOf(buf.readBoolean());
        }

        if (buf.readBoolean()) {
            flying = Boolean.valueOf(buf.readBoolean());
        }

        if (buf.readBoolean()) {
            visible = Boolean.valueOf(buf.readBoolean());
        }

        if (buf.readBoolean()) {
            noClip = Boolean.valueOf(buf.readBoolean());
        }

        if (buf.readBoolean()) {
            offset = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    }

    public static class Handler implements IMessageHandler<MessageClientGuiUpdateMannequin, IMessage> {

        @Override
        public IMessage onMessage(MessageClientGuiUpdateMannequin message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.getEntityWorld();
            Entity entity = world.getEntityByID(message.id);

            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                @Override
                public void run() {
                    if (entity != null && entity instanceof EntityMannequin) {
                        EntityMannequin entityMannequin = (EntityMannequin) entity;

                        if (message.bipedRotations != null) {
                            entityMannequin.setBipedRotations(message.bipedRotations);
                        }

                        if (message.textureData != null) {
                            entityMannequin.setTextureData(message.textureData, true);
                        }

                        if (message.extraRenders != null) {
                            entityMannequin.setRenderExtras(message.extraRenders.booleanValue());
                        }

                        if (message.flying != null) {
                            entityMannequin.setFlying(message.flying.booleanValue());
                        }

                        if (message.visible != null) {
                            entityMannequin.setVisible(message.visible.booleanValue());
                        }

                        if (message.noClip != null) {
                            entityMannequin.setNoClip(message.noClip.booleanValue());
                        }

                        if (message.offset != null) {
                            Vec3d pos = entityMannequin.getPositionVector();
                            pos = pos.add(message.offset);
                            entityMannequin.setPosition(pos.x, pos.y, pos.z);
                        }
                    }
                }
            });

            return null;
        }
    }
}
