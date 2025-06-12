package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.core.network.UpdateSkinDocumentPacket;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashMap;
import java.util.Stack;

public class SkinDocumentSynchronizer implements SkinDocumentListener {

    protected boolean isApplying = false;
    protected boolean isCapturing = false;
    protected boolean isClient = false;

    protected CompoundTag capturedPropertiesValues;
    protected CompoundTag capturedSettingValues;

    protected final LinkedHashMap<String, CompoundTag> capturedNodeValues = new LinkedHashMap<>();
    protected final Stack<Boolean> capturedStates = new Stack<>();

    protected final BlockEntity blockEntity;

    public SkinDocumentSynchronizer(BlockEntity blockEntity, boolean isClient) {
        this.blockEntity = blockEntity;
        this.isClient = isClient;
    }

    @Override
    public void documentDidChangeType(SkinDocumentType type) {
        post(new UpdateSkinDocumentPacket.ChangeTypeAction(type));
    }

    @Override
    public void documentDidChangeSettings(CompoundTag tag) {
        if (isCapturing) {
            if (capturedSettingValues != null) {
                capturedSettingValues.merge(tag);
            } else {
                capturedSettingValues = tag;
            }
            return;
        }
        post(new UpdateSkinDocumentPacket.UpdateSettingsAction(tag));
    }

    @Override
    public void documentDidChangeProperties(CompoundTag tag) {
        if (isCapturing) {
            if (capturedPropertiesValues != null) {
                capturedPropertiesValues.merge(tag);
            } else {
                capturedPropertiesValues = tag;
            }
            return;
        }

        post(new UpdateSkinDocumentPacket.UpdatePropertiesAction(tag));
    }

    @Override
    public void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        var builder = new TagSerializer();
        target.serialize(builder);
        post(new UpdateSkinDocumentPacket.InsertNodeAction(node.id(), index, builder.tag()));
    }

    @Override
    public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
        if (isCapturing) {
            capturedNodeValues.computeIfAbsent(node.id(), it -> tag).merge(tag);
            return;
        }
        post(new UpdateSkinDocumentPacket.UpdateNodeAction(node.id(), tag));
    }

    @Override
    public void documentDidRemoveNode(SkinDocumentNode node) {
        post(new UpdateSkinDocumentPacket.RemoveNodeAction(node.id()));
    }

    @Override
    public void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        post(new UpdateSkinDocumentPacket.MoveNodeAction(node.id(), target.id(), index));
    }

    @Override
    public void documentWillBeginEditing() {
        isApplying = true;
    }

    @Override
    public void documentDidEndEditing() {
        isApplying = false;
    }

    public void beginCapture() {
        capturedStates.push(isCapturing);
        isCapturing = true;
    }

    public void endCapture() {
        isCapturing = capturedStates.pop();
        if (isCapturing) {
            return;
        }
        if (capturedPropertiesValues != null) {
            documentDidChangeProperties(capturedPropertiesValues);
            capturedPropertiesValues = null;
        }
        if (capturedSettingValues != null) {
            documentDidChangeSettings(capturedSettingValues);
            capturedSettingValues = null;
        }
        capturedNodeValues.forEach((id, tag) -> {
            post(new UpdateSkinDocumentPacket.UpdateNodeAction(id, tag));
        });
        capturedNodeValues.clear();
    }

    protected void post(UpdateSkinDocumentPacket.Action action) {
        // when the document applying changes, we can't send it to server again.
        if (isApplying) {
            return;
        }
        if (isClient) {
            NetworkManager.sendToServer(new UpdateSkinDocumentPacket(blockEntity, action));
        } else {
            NetworkManager.sendToTrackingBlock(new UpdateSkinDocumentPacket(blockEntity, action), blockEntity);
        }
    }
}
