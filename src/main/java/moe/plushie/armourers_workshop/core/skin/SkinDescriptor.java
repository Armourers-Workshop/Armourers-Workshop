package moe.plushie.armourers_workshop.core.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.concurrent.TimeUnit;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    private final static Cache<ItemStack, SkinDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(32)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final String identifier;
    private final ISkinType type;
    private final ColorScheme colorScheme;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, ColorScheme.EMPTY);
    }

    public SkinDescriptor(SkinDescriptor descriptor, ColorScheme colorScheme) {
        this.identifier = descriptor.getIdentifier();
        this.type = descriptor.getType();
        this.colorScheme = colorScheme;
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
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains(AWConstants.NBT.SKIN)) {
            return EMPTY;
        }
        SkinDescriptor descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new SkinDescriptor(nbt.getCompound(AWConstants.NBT.SKIN));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
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

    public ItemStack asItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = new ItemStack(ModItems.SKIN);
        setDescriptor(itemStack, this);
        return itemStack;
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


//    @Override
//    public ISkinIdentifier getIdentifier() {
//        return identifier;
//    }
//
//    @Override
//    public ISkinDye getSkinDye() {
//        return skinDye;
//    }

    // TODO: IMP
//    public void readFromCompound(NBTTagCompound compound) {
//        readFromCompound(compound, TAG_SKIN_DATA);
//    }
//
//    public void readFromCompound(NBTTagCompound compound, String tag) {
//        NBTTagCompound skinDataCompound = compound.getCompoundTag(tag);
//        this.identifier = SkinIdentifierSerializer.readFromCompound(skinDataCompound);
//        this.skinDye.readFromCompound(skinDataCompound);
//    }
//
//    public void writeToCompound(NBTTagCompound compound) {
//        writeToCompound(compound, TAG_SKIN_DATA);
//    }
//
//    public void writeToCompound(NBTTagCompound compound, String tag) {
//        NBTTagCompound skinDataCompound = new NBTTagCompound();
//        SkinIdentifierSerializer.writeToCompound(identifier, skinDataCompound);
//        skinDye.writeToCompound(skinDataCompound);
//        compound.setTag(tag, skinDataCompound);
//    }


    @Override
    public String toString() {
        return identifier + "@" + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinDescriptor that = (SkinDescriptor) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
