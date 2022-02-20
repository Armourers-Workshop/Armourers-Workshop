package moe.plushie.armourers_workshop.core.skin.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.AWItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    private static final String NBT_KEY_SKIN = "armourersWorkshop";
    private static final String NBT_KEY_SKIN_TYPE = "skinType";
    private static final String NBT_KEY_SKIN_IDENTIFIER = "identifier";

    private final static Cache<ItemStack, SkinDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final String identifier;
    private final ISkinType type;
    private final SkinPalette palette;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, SkinPalette.EMPTY);
    }

    public SkinDescriptor(String identifier, ISkinType type, SkinPalette palette) {
        this.identifier = identifier;
        this.type = type;
        this.palette = palette;
    }

    public SkinDescriptor(CompoundNBT nbt) {
        this.identifier = nbt.getString(NBT_KEY_SKIN_IDENTIFIER);
        this.type = SkinTypes.byName(nbt.getString(NBT_KEY_SKIN_TYPE));
        this.palette = new SkinPalette();
    }

    @Nonnull
    public static SkinDescriptor of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return EMPTY;
        }
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains(NBT_KEY_SKIN)) {
            return EMPTY;
        }
        SkinDescriptor descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new SkinDescriptor(nbt.getCompound(NBT_KEY_SKIN));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
        return descriptor;
    }

    public boolean accept(ItemStack itemStack) {
        if (itemStack.isEmpty() || isEmpty()) {
            return false;
        }
        ISkinType skinType = getType();
        if (skinType instanceof ISkinToolType) {
            return itemStack.getItem().is(((ISkinToolType) skinType).getTag());
        }
        return false;
    }

    @Nonnull
    public ItemStack asItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = new ItemStack(AWItems.SKIN.get());
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(NBT_KEY_SKIN_IDENTIFIER, identifier);
        nbt.putString(NBT_KEY_SKIN_TYPE, type.getRegistryName().toString());
        itemStack.addTagElement(NBT_KEY_SKIN, nbt);
        return itemStack;
    }


    public boolean isEmpty() {
        return this == EMPTY;
    }

    public SkinPalette getPalette() {
        return palette;
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
        return "SkinDescriptor{" +
                "identifier='" + identifier + '\'' +
                ", type=" + type +
                '}';
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
