package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.core.client.gui.widget.Toast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ExecuteAlertPacket extends CustomPacket {

    private final Component title;
    private final Component message;
    private final Component confirm;
    private final int type;

    public ExecuteAlertPacket(Component title, Component message, Component confirm, int type) {
        this.title = title;
        this.message = message;
        this.confirm = confirm;
        this.type = type;
    }

    public ExecuteAlertPacket(FriendlyByteBuf buffer) {
        this.title = buffer.readComponent();
        this.message = buffer.readComponent();
        this.confirm = buffer.readComponent();
        this.type = buffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeComponent(title);
        buffer.writeComponent(message);
        buffer.writeComponent(confirm);
        buffer.writeInt(type);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        // this safe call, because in the server side, toast class will be removed.
        Toast.showAlertFromServer(this);
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

    public int getType() {
        return type;
    }
}
