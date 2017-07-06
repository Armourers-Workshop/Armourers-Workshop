package riskyken.armourersWorkshop.common.network.messages.server;

import java.util.Arrays;
import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
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
    private String[] itemOverrides;
    private boolean libraryShowsModelPreviews;
    private boolean lockDyesOnSkins;
    private UUID playerId;
    
    public MessageServerSyncConfig(EntityPlayer player) {
        this();
        playerId = player.getUniqueID();
    }
    
    public MessageServerSyncConfig() {
        this.allowClientsToDownloadSkins = ConfigHandler.allowClientsToDownloadSkins;
        this.allowClientsToUploadSkins = ConfigHandler.allowClientsToUploadSkins;
        this.itemOverrides = ModAddonManager.itemOverrides.toArray(new String[ModAddonManager.itemOverrides.size()]);
        this.libraryShowsModelPreviews = ConfigHandler.libraryShowsModelPreviews;
        this.lockDyesOnSkins = ConfigHandler.lockDyesOnSkins;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(allowClientsToDownloadSkins);
        buf.writeBoolean(allowClientsToUploadSkins);
        ByteBufHelper.writeStringArrayToBuf(buf, itemOverrides);
        buf.writeBoolean(libraryShowsModelPreviews);
        buf.writeBoolean(lockDyesOnSkins);
        if (playerId == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ByteBufHelper.writeUUID(buf, playerId);
        }
    }
    
    
    @Override
    public void fromBytes(ByteBuf buf) {
        allowClientsToDownloadSkins = buf.readBoolean();
        allowClientsToUploadSkins = buf.readBoolean();
        itemOverrides = ByteBufHelper.readStringArrayFromBuf(buf);
        libraryShowsModelPreviews = buf.readBoolean();
        lockDyesOnSkins = buf.readBoolean();
        if (buf.readBoolean()) {
            playerId = ByteBufHelper.readUUID(buf);
        }
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
        ModAddonManager.itemOverrides.clear();
        ModAddonManager.itemOverrides.addAll(Arrays.asList(message.itemOverrides));
        ConfigHandler.libraryShowsModelPreviews = message.libraryShowsModelPreviews;
        ConfigHandler.lockDyesOnSkins = message.lockDyesOnSkins;
        ConfigHandler.remotePlayerId = message.playerId;
    }
}
