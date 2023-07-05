package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ExecuteAlertPacket extends CustomPacket {

    private final Component title;
    private final Component message;
    private final Component confirm;
    private final CompoundTag icon;
    private final int type;

    public ExecuteAlertPacket(Component title, Component message, Component confirm, int type, CompoundTag nbt) {
        this.title = title;
        this.message = message;
        this.confirm = confirm;
        this.type = type;
        this.icon = nbt;
    }

    public ExecuteAlertPacket(FriendlyByteBuf buffer) {
        this.title = buffer.readComponent();
        this.message = buffer.readComponent();
        this.confirm = buffer.readComponent();
        this.type = buffer.readInt();
        this.icon = buffer.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeComponent(title);
        buffer.writeComponent(message);
        buffer.writeComponent(confirm);
        buffer.writeInt(type);
        buffer.writeNbt(icon);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        // this safe call, because in the server side, toast class will be removed.
        UserNotificationCenter.showAlertFromServer(this);
    }

    public Component getTitle() {
        return title;
    }

    public Component getMessage() {
        return message;
    }

    public Component getConfirm() {
        return confirm;
    }

    public CompoundTag getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }
}
