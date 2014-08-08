package riskyken.armourersWorkshop.common.network;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageButton;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);

    public static void init() {
        networkWrapper.registerMessage(MessageButton.class, MessageButton.class, 0, Side.SERVER);
    }
}
