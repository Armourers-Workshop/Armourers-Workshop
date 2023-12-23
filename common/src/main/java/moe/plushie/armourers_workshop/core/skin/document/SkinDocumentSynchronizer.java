package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.network.UpdateSkinDocumentPacket;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashMap;
import java.util.Stack;

public class SkinDocumentSynchronizer implements SkinDocumentListener {

    private boolean isApplying = false;
    private boolean isCapturing = false;

    private SkinProperties capturedSkinValues;
    private CompoundTag capturedSettingValues;

    private final LinkedHashMap<String, CompoundTag> capturedNodeValues = new LinkedHashMap<>();
    private final Stack<Boolean> capturedStates = new Stack<>();

    private final BlockEntity blockEntity;

    public SkinDocumentSynchronizer(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
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
    public void documentDidChangeProperties(SkinProperties properties) {
        if (isCapturing) {
            if (capturedSkinValues != null) {
                capturedSkinValues.putAll(properties);
            } else {
                capturedSkinValues = properties;
            }
            return;
        }

        post(new UpdateSkinDocumentPacket.UpdatePropertiesAction(properties));
    }

    @Override
    public void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        post(new UpdateSkinDocumentPacket.InsertNodeAction(node.getId(), index, target.serializeNBT()));
    }

    @Override
    public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
        if (isCapturing) {
            capturedNodeValues.computeIfAbsent(node.getId(), it -> tag).merge(tag);
            return;
        }
        post(new UpdateSkinDocumentPacket.UpdateNodeAction(node.getId(), tag));
    }

    @Override
    public void documentDidRemoveNode(SkinDocumentNode node) {
        post(new UpdateSkinDocumentPacket.RemoveNodeAction(node.getId()));
    }

    @Override
    public void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
        post(new UpdateSkinDocumentPacket.MoveNodeAction(node.getId(), target.getId(), index));
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
        if (capturedSkinValues != null) {
            documentDidChangeProperties(capturedSkinValues);
            capturedSkinValues = null;
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

    private void post(UpdateSkinDocumentPacket.Action action) {
        // when the document applying changes, we can't send it to server again.
        if (isApplying) {
            return;
        }
        NetworkManager.sendToServer(new UpdateSkinDocumentPacket(blockEntity, action));
    }
}
