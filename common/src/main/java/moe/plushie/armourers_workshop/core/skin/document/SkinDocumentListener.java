package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import net.minecraft.nbt.CompoundTag;

public interface SkinDocumentListener {

    default void documentDidChangeType(SkinDocumentType type) {
    }

    default void documentDidChangeProperties(SkinProperties properties) {
    }

    default void documentDidInsertNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
    }

    default void documentDidUpdateNode(SkinDocumentNode node, CompoundTag tag) {
    }

    default void documentDidRemoveNode(SkinDocumentNode node) {
    }

    default void documentDidMoveNode(SkinDocumentNode node, SkinDocumentNode target, int index) {
    }

    default void documentDidSelectNode(SkinDocumentNode node) {
    }

    default void documentDidReload() {
    }

    default void documentWillBeginEditing() {
    }

    default void documentDidEndEditing() {
    }

}
