package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;

/**
 * Sent from the server to a client when they connect to sync configs.
 * @author RiskyKen
 *
 */
public class MessageServerSyncConfig implements IMessage, IMessageHandler<MessageServerSyncConfig, IMessage> {
    
    private boolean allowClientsToDownloadSkins;
    private boolean allowClientsToUploadSkins;
    private String[] overrideSwordsActive;
    private String[] overrideBowsActive;
    private boolean libraryShowsModelPreviews;
    
    public MessageServerSyncConfig() {
        this.allowClientsToDownloadSkins = ConfigHandler.allowClientsToDownloadSkins;
        this.allowClientsToUploadSkins = ConfigHandler.allowClientsToUploadSkins;
        this.overrideSwordsActive = Addons.overrideSwordsActive;
        this.overrideBowsActive = Addons.overrideBowsActive;
        this.libraryShowsModelPreviews = ConfigHandler.libraryShowsModelPreviews;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(allowClientsToDownloadSkins);
        buf.writeBoolean(allowClientsToUploadSkins);
        ByteBufHelper.writeStringArrayToBuf(buf, overrideSwordsActive);
        ByteBufHelper.writeStringArrayToBuf(buf, overrideBowsActive);
        buf.writeBoolean(libraryShowsModelPreviews);
    }
    
    
    @Override
    public void fromBytes(ByteBuf buf) {
        allowClientsToDownloadSkins = buf.readBoolean();
        allowClientsToUploadSkins = buf.readBoolean();
        overrideSwordsActive = ByteBufHelper.readStringArrayFromBuf(buf);
        overrideBowsActive = ByteBufHelper.readStringArrayFromBuf(buf);
        libraryShowsModelPreviews = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(MessageServerSyncConfig message, MessageContext ctx) {
        setConfigsOnClient(message);
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private void setConfigsOnClient(MessageServerSyncConfig message) {
        ConfigHandler.allowClientsToDownloadSkins = message.allowClientsToDownloadSkins;
        ConfigHandler.allowClientsToUploadSkins = message.allowClientsToUploadSkins;
        Addons.overrideSwordsActive = message.overrideSwordsActive;
        Addons.overrideBowsActive = message.overrideBowsActive;
        ConfigHandler.libraryShowsModelPreviews = message.libraryShowsModelPreviews;
    }
}
