package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    private final String identifier;
    private final ISkinType type;
    private final ColorScheme colorScheme;

    // not a required property, but it can help we reduce memory usage and improve performance.
    private ItemStack skinItemStack;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, ColorScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, ISkinType type) {
        this(identifier, type, ColorScheme.EMPTY);
    }

    public SkinDescriptor(SkinDescriptor descriptor, ColorScheme colorScheme) {
        this(descriptor.getIdentifier(), descriptor.getType(), colorScheme);
    }

    public SkinDescriptor(String identifier, ISkinType type, ColorScheme colorScheme) {
        this.identifier = identifier;
        this.type = type;
        this.colorScheme = colorScheme;
    }

    public SkinDescriptor(CompoundNBT nbt) {
        this.identifier = nbt.getString(AWConstants.NBT.SKIN_IDENTIFIER);
        this.type = SkinTypes.byName(nbt.getString(AWConstants.NBT.SKIN_TYPE));
        this.colorScheme = AWDataSerializers.getColorScheme(nbt, AWConstants.NBT.SKIN_DYE, ColorScheme.EMPTY);
    }

    public static SkinDescriptor of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return EMPTY;
        }
        ISkinDataProvider dataProvider = (ISkinDataProvider) (Object) itemStack;
        SkinDescriptor descriptor = dataProvider.getSkinData();
        if (descriptor != null) {
            return descriptor;
        }
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains(AWConstants.NBT.SKIN)) {
            return EMPTY;
        }
        descriptor = new SkinDescriptor(nbt.getCompound(AWConstants.NBT.SKIN));
        dataProvider.setSkinData(descriptor);
        return descriptor;
    }

    public static void setDescriptor(ItemStack itemStack, SkinDescriptor descriptor) {
        if (!itemStack.isEmpty()) {
            itemStack.addTagElement(AWConstants.NBT.SKIN, descriptor.serializeNBT());
        }
    }

    public boolean accept(ItemStack itemStack) {
        if (itemStack.isEmpty() || isEmpty()) {
            return false;
        }
        ISkinType skinType = getType();
        if (skinType == SkinTypes.ITEM) {
            return true;
        }
        if (skinType instanceof ISkinToolType) {
            return itemStack.getItem().is(((ISkinToolType) skinType).getTag());
        }
        return false;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(AWConstants.NBT.SKIN_TYPE, type.getRegistryName().toString());
        nbt.putString(AWConstants.NBT.SKIN_IDENTIFIER, identifier);
        AWDataSerializers.putColorScheme(nbt, AWConstants.NBT.SKIN_DYE, colorScheme, ColorScheme.EMPTY);
        return nbt;
    }

    public ItemStack sharedItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (skinItemStack != null) {
            return skinItemStack;
        }
        ItemStack itemStack = new ItemStack(ModItems.SKIN);
        setDescriptor(itemStack, this);
        skinItemStack = itemStack;
        return itemStack;
    }

    public ItemStack asItemStack() {
        return sharedItemStack().copy();
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public ISkinType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier + "@" + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinDescriptor that = (SkinDescriptor) o;
        return identifier.equals(that.identifier) && colorScheme.equals(that.colorScheme);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }


}
