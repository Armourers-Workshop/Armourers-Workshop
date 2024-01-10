package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.item.ItemStack;

public class SkinItemSource {

    public static final SkinItemSource EMPTY = new SkinItemSource();

    private float renderPriority;
    private ItemStack itemStack;

    private Vector3f itemScale;
    private Vector3f itemRotation;
    private AbstractItemTransformType transformType;

    public SkinItemSource() {
        this(0, ItemStack.EMPTY, AbstractItemTransformType.NONE);
    }

    public SkinItemSource(float renderPriority, ItemStack itemStack, AbstractItemTransformType transformType) {
        this.renderPriority = renderPriority;
        this.itemStack = itemStack;
        this.transformType = transformType;
    }

    public static SkinItemSource create(ItemStack itemStack) {
        return create(0, itemStack, AbstractItemTransformType.NONE);
    }

    public static SkinItemSource create(ItemStack itemStack, AbstractItemTransformType transformType) {
        return create(0, itemStack, transformType);
    }

    public static SkinItemSource create(float renderPriority, ItemStack itemStack) {
        return create(renderPriority, itemStack, AbstractItemTransformType.NONE);
    }

    public static SkinItemSource create(float renderPriority, ItemStack itemStack, AbstractItemTransformType transformType) {
        SkinItemSource itemSource = new SkinItemSource();
        itemSource.setItem(itemStack);
        itemSource.setRenderPriority(renderPriority);
        itemSource.setTransformType(transformType);
        itemSource.setScale(null);
        itemSource.setRotation(null);
        return itemSource;
    }

    public void setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public void setRenderPriority(float renderPriority) {
        this.renderPriority = renderPriority;
    }

    public float getRenderPriority() {
        return renderPriority;
    }

    public void setRotation(Vector3f rotation) {
        this.itemRotation = rotation;
    }

    public Vector3f getRotation() {
        return itemRotation;
    }

    public void setScale(Vector3f scale) {
        this.itemScale = scale;
    }

    public Vector3f getScale() {
        return itemScale;
    }

    public void setTransformType(AbstractItemTransformType transformType) {
        this.transformType = transformType;
    }

    public AbstractItemTransformType getTransformType() {
        return transformType;
    }
}
