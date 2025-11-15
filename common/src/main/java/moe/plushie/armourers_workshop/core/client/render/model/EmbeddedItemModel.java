package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemProperties;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.ItemStackRenderState;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class EmbeddedItemModel {

    private SkinItemProperties properties = SkinItemProperties.EMPTY;
    private EntityRenderState entityRenderState;

    private final int mode; // 2 force render in box
    private final SkinDescriptor skin;
    private final EntitySlot slot;

    private final ItemStackRenderState renderState;

    private EmbeddedItemModel(int mode, SkinDescriptor skin, EntitySlot slot, ItemStackRenderState renderState) {
        this.mode = mode;
        this.slot = slot;
        this.skin = skin;
        this.renderState = renderState;
    }

    public static EmbeddedItemModel fromWardrobe(EntitySlot slot, ItemStackRenderState renderState) {
        return new EmbeddedItemModel(0, slot.descriptor(), slot, renderState);
    }

    public static EmbeddedItemModel fromComponent(SkinDescriptor descriptor, ItemStackRenderState renderState) {
        return new EmbeddedItemModel(1, descriptor, null, renderState);

    }

    public static EmbeddedItemModel fromSelf(SkinDescriptor descriptor, ItemStackRenderState renderState) {
        return new EmbeddedItemModel(2, descriptor, null, renderState);
    }

    public boolean shouldRenderInBox() {
        // for the item required render to box.
        if (mode == 2) {
            return true;
        }
        var type = skin.type();
        if (type == SkinTypes.BOAT || type == SkinTypes.ITEM_FISHING || type == SkinTypes.HORSE) {
            return true;
        }
        // for the tool type skin, don't render in the box.
        if (type.isTool()) {
            return false;
        }
        // for the item type skin, don't render in the box.
        if (type == SkinTypes.ITEM) {
            return false;
        }
        return true;
    }

    public void setProperties(SkinItemProperties properties) {
        this.properties = properties;
    }

    public SkinItemProperties properties() {
        return properties;
    }

    public EntitySlot slot() {
        return slot;
    }

    public SkinDescriptor skin() {
        return skin;
    }

    public ItemStackRenderState renderState() {
        return renderState;
    }

    public void setEntityRenderState(EntityRenderState entityRenderState) {
        this.entityRenderState = entityRenderState;
    }

    @Nullable
    public EntityRenderState entityRenderState() {
        return entityRenderState;
    }
}
