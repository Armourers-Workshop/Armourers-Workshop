package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelResolver;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class SkinItemSource {

    public static final SkinItemSource EMPTY = new SkinItemSource(ItemStack.EMPTY);

    private float renderPriority = 0;

    private ItemStack itemStack;
    private SkinItemProperties itemProperties = SkinItemProperties.EMPTY;

    private OpenVector3f itemScale;
    private OpenVector3f itemRotation;
    private OpenItemDisplayContext itemDisplayContext = OpenItemDisplayContext.NONE;

    private OpenRectangle3f displayBox;
    private SkinItemModelResolver itemModelResolver;

    public SkinItemSource(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static SkinItemSource create(ItemStack itemStack) {
        return new SkinItemSource(itemStack);
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack itemStack() {
        return this.itemStack;
    }

    public void setRenderPriority(float renderPriority) {
        this.renderPriority = renderPriority;
    }

    public float renderPriority() {
        return renderPriority;
    }

    public void setRotation(OpenVector3f rotation) {
        this.itemRotation = rotation;
    }

    public OpenVector3f rotation() {
        return itemRotation;
    }

    public void setScale(OpenVector3f scale) {
        this.itemScale = scale;
    }

    public OpenVector3f scale() {
        return itemScale;
    }

    public void setDisplayContext(OpenItemDisplayContext itemDisplayContext) {
        this.itemDisplayContext = itemDisplayContext;
    }

    public OpenItemDisplayContext displayContext() {
        return itemDisplayContext;
    }

    public void setProperties(SkinItemProperties itemProperties) {
        this.itemProperties = itemProperties;
    }

    public SkinItemProperties properties() {
        return itemProperties;
    }

    public void setDisplayBox(OpenRectangle3f displayBox) {
        this.displayBox = displayBox;
    }

    public OpenRectangle3f displayBox() {
        return displayBox;
    }

    public void setItemModelResolver(SkinItemModelResolver itemModelResolver) {
        this.itemModelResolver = itemModelResolver;
    }

    public SkinItemModelResolver itemModelResolver() {
        return itemModelResolver;
    }

    public SkinItemSource copy() {
        var newItemSource = new SkinItemSource(itemStack);
        newItemSource.renderPriority = renderPriority;
        newItemSource.itemProperties = itemProperties;
        newItemSource.itemScale = itemScale;
        newItemSource.itemRotation = itemRotation;
        newItemSource.itemDisplayContext = itemDisplayContext;
        newItemSource.displayBox = displayBox;
        newItemSource.itemModelResolver = itemModelResolver;
        return newItemSource;
    }
}
