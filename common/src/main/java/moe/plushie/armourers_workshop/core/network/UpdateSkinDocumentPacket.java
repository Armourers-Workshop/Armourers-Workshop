package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentProvider;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.function.Function;

public class UpdateSkinDocumentPacket extends CustomPacket {

    private static final HashMap<Class<? extends Action>, Mode> ALL_ACTIONS = new HashMap<>();

    private String operator;

    private final BlockPos pos;
    private final Action action;

    public UpdateSkinDocumentPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.operator = buffer.readUtf();
        var mode = buffer.readEnum(Mode.class);
        this.action = mode.read(buffer);
    }

    public UpdateSkinDocumentPacket(BlockEntity entity, Action action) {
        this.pos = entity.getBlockPos();
        this.action = action;
        this.operator = "";
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUtf(operator);
        buffer.writeEnum(ALL_ACTIONS.getOrDefault(action.getClass(), Mode.CHANGE_TYPE));
        action.encode(buffer);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        var blockEntity = player.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof SkinDocumentProvider provider) {
            ModLog.debug("the document {} accepted for '{}'", action, player.getScoreboardName());
            var document = provider.getDocument();
            document.beginEditing();
            action.execute(document, player);
            document.endEditing();
            if (!(action instanceof ChangeTypeAction)) {
                operator = player.getStringUUID();
                NetworkManager.sendToTrackingBlock(this, blockEntity);
            }
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        // ignore the packet is by operator self.
        if (operator.equals(player.getStringUUID())) {
            return;
        }
        var blockEntity = player.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof SkinDocumentProvider provider) {
            ModLog.debug("the document {} accepted for server", action);
            var document = provider.getDocument();
            document.beginEditing();
            action.execute(document, player);
            document.endEditing();
        }
    }


    public enum Mode {

        CHANGE_TYPE(ChangeTypeAction.class, ChangeTypeAction::new),
        UPDATE_SETTINGS(UpdateSettingsAction.class, UpdateSettingsAction::new),
        UPDATE_PROPERTIES(UpdatePropertiesAction.class, UpdatePropertiesAction::new),
        INSERT_NODE(InsertNodeAction.class, InsertNodeAction::new),
        UPDATE_NODE(UpdateNodeAction.class, UpdateNodeAction::new),
        REMOVE_NODE(RemoveNodeAction.class, RemoveNodeAction::new),
        MOVE_NODE(MoveNodeAction.class, MoveNodeAction::new);

        final Function<IFriendlyByteBuf, ? extends Action> actionFactory;

        Mode(Class<? extends Action> actionClass, Function<IFriendlyByteBuf, Action> actionFactory) {
            this.actionFactory = actionFactory;
            ALL_ACTIONS.put(actionClass, this);
        }

        public Action read(IFriendlyByteBuf buf) {
            return actionFactory.apply(buf);
        }
    }

    public static abstract class Action {

        public abstract void encode(final IFriendlyByteBuf buffer);

        public abstract void execute(final SkinDocument document, final Player player);

        protected String makeDescription(String action, Object... args) {
            var builder = new StringBuilder();
            builder.append(action);
            builder.append("(");
            for (int i = 0; i < args.length; i += 2) {
                if (i != 0) {
                    builder.append(",");
                }
                builder.append(args[i]);
                builder.append(":");
                builder.append(args[i + 1]);
            }
            builder.append(")");
            return builder.toString();
        }
    }

    public static class ChangeTypeAction extends Action {

        private final SkinDocumentType type;

        public ChangeTypeAction(SkinDocumentType type) {
            this.type = type;
        }

        public ChangeTypeAction(IFriendlyByteBuf buf) {
            this.type = SkinDocumentTypes.byName(buf.readUtf());
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeUtf(type.getRegistryName().toString());
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            document.setType(type);
        }

        @Override
        public String toString() {
            return makeDescription("changeType", "type", type.getRegistryName());
        }
    }

    public static class UpdateSettingsAction extends Action {

        private final CompoundTag tag;

        public UpdateSettingsAction(CompoundTag tag) {
            this.tag = tag;
        }

        public UpdateSettingsAction(IFriendlyByteBuf buf) {
            this.tag = buf.readNbt();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeNbt(tag);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            document.updateSettings(tag);
        }

        @Override
        public String toString() {
            return makeDescription("updateSettings", "tag", tag);
        }
    }

    public static class UpdatePropertiesAction extends Action {

        private final CompoundTag tag;

        public UpdatePropertiesAction(CompoundTag tag) {
            this.tag = tag;
        }

        public UpdatePropertiesAction(IFriendlyByteBuf buf) {
            this.tag = buf.readNbt();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeNbt(tag);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            document.updateProperties(tag);
        }

        @Override
        public String toString() {
            return makeDescription("updateProperties", "tag", tag);
        }
    }

    public static class InsertNodeAction extends Action {

        private final String id;
        private final int targetIndex;
        private final CompoundTag tag;

        public InsertNodeAction(String id, int targetIndex, CompoundTag tag) {
            this.id = id;
            this.targetIndex = targetIndex;
            this.tag = tag;
        }

        public InsertNodeAction(IFriendlyByteBuf buf) {
            this.id = buf.readUtf();
            this.targetIndex = buf.readInt();
            this.tag = buf.readNbt();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeUtf(id);
            buf.writeInt(targetIndex);
            buf.writeNbt(tag);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            var target = document.nodeById(id);
            if (target != null) {
                var newValue = new SkinDocumentNode(tag);
                target.insertAtIndex(newValue, targetIndex);
            }
        }

        @Override
        public String toString() {
            return makeDescription("insertNode", "id", id, "ti", targetIndex, "tag", tag);
        }
    }

    public static class UpdateNodeAction extends Action {

        private final String id;
        private final CompoundTag tag;

        public UpdateNodeAction(String id, CompoundTag tag) {
            this.id = id;
            this.tag = tag;
        }

        public UpdateNodeAction(IFriendlyByteBuf buf) {
            this.id = buf.readUtf();
            this.tag = buf.readNbt();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeUtf(id);
            buf.writeNbt(tag);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            var target = document.nodeById(id);
            if (target != null) {
                target.deserializeNBT(tag);
            }
        }

        @Override
        public String toString() {
            return makeDescription("updateNode", "id", id, "tag", tag);
        }
    }

    public static class RemoveNodeAction extends Action {

        private final String id;

        public RemoveNodeAction(String id) {
            this.id = id;
        }

        public RemoveNodeAction(IFriendlyByteBuf buf) {
            this.id = buf.readUtf();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeUtf(id);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            var target = document.nodeById(id);
            if (target != null) {
                target.removeFromParent();
            }
        }

        @Override
        public String toString() {
            return makeDescription("removeNode", "id", id);
        }
    }

    public static class MoveNodeAction extends Action {

        private final String id;
        private final String targetId;
        private final int targetIndex;

        public MoveNodeAction(String id, String targetId, int targetIndex) {
            this.id = id;
            this.targetId = targetId;
            this.targetIndex = targetIndex;
        }

        public MoveNodeAction(IFriendlyByteBuf buf) {
            this.id = buf.readUtf();
            this.targetId = buf.readUtf();
            this.targetIndex = buf.readInt();
        }

        @Override
        public void encode(IFriendlyByteBuf buf) {
            buf.writeUtf(id);
            buf.writeUtf(targetId);
            buf.writeInt(targetIndex);
        }

        @Override
        public void execute(SkinDocument document, Player player) {
            var parent = document.nodeById(id);
            var target = document.nodeById(targetId);
            if (parent != null && target != null) {
                parent.moveTo(target, targetIndex);
            }
        }

        @Override
        public String toString() {
            return makeDescription("moveNode", "id", id, "tid", targetId, "ti", targetIndex);
        }
    }
}
