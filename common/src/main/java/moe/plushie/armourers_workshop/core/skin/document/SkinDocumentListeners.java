package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public class SkinDocumentListeners {

    public static class Proxy implements SkinDocumentListener {

        private final ArrayList<SkinDocumentListener> listeners = new ArrayList<>();

        @Override
        public void documentDidSelectNode(SkinDocumentNode node) {
            listeners.forEach(it -> it.documentDidSelectNode(node));
        }

        @Override
        public void documentDidReload() {
            listeners.forEach(SkinDocumentListener::documentDidReload);
        }

        @Override
        public void documentDidChangeType(SkinDocumentType type) {
            listeners.forEach(it -> it.documentDidChangeType(type));
        }

        @Override
        public void documentDidChangeProperties(SkinProperties properties) {
            listeners.forEach(it -> it.documentDidChangeProperties(properties));
        }

        @Override
        public void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
            listeners.forEach(it -> it.documentDidInsertNode(node, target, index));
        }

        @Override
        public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
            listeners.forEach(it -> it.documentDidUpdateNode(node, tag));
        }

        @Override
        public void documentDidRemoveNode(SkinDocumentNode node) {
            listeners.forEach(it -> it.documentDidRemoveNode(node));
        }

        @Override
        public void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
            listeners.forEach(it -> it.documentDidMoveNode(node, target, index));
        }

        @Override
        public void documentWillBeginEditing() {
            listeners.forEach(SkinDocumentListener::documentWillBeginEditing);
        }

        @Override
        public void documentDidEndEditing() {
            listeners.forEach(SkinDocumentListener::documentDidEndEditing);
        }

        public void addListener(SkinDocumentListener listener) {
            listeners.add(listener);
        }

        public void removeListener(SkinDocumentListener listener) {
            listeners.remove(listener);
        }
    }

    public static class Updater implements SkinDocumentListener {

        private final UpdatableBlockEntity blockEntity;

        public Updater(UpdatableBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void documentDidChangeType(SkinDocumentType type) {
            BlockUtils.combine(blockEntity, blockEntity::sendBlockUpdates);
        }

        @Override
        public void documentDidChangeProperties(SkinProperties properties) {
            blockEntity.setChanged();
        }

        @Override
        public void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
            blockEntity.setChanged();
        }

        @Override
        public void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
            blockEntity.setChanged();
        }

        @Override
        public void documentDidRemoveNode(SkinDocumentNode node) {
            blockEntity.setChanged();
        }

        @Override
        public void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
            blockEntity.setChanged();
        }
    }
}
