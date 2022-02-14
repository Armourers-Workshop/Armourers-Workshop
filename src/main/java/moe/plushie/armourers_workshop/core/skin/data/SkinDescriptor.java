package moe.plushie.armourers_workshop.core.skin.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.common.item.SkinItems;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    public static final String NBT_KEY_SKIN = "armourersWorkshop";
    public static final String NBT_KEY_SKIN_TYPE = "skinType";
    public static final String NBT_KEY_SKIN_IDENTIFIER = "identifier";

    private final static Cache<ItemStack, SkinDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final String identifier;
    private final ISkinType type;
    private final Palette palette;

//    public static final SkinProperty<String> ALL_CUSTOM_NAME = new SkinProperty<>("customName", "");
//    public static final SkinProperty<String> ALL_FLAVOUR_TEXT = new SkinProperty<>("flavour", "");
//    public static final SkinProperty<String> ALL_AUTHOR_NAME = new SkinProperty<>("authorName", "");
//    public static final SkinProperty<String> ALL_AUTHOR_UUID = new SkinProperty<>("authorUUID", "");

//    private ISkinIdentifier identifier;
//    public ISkinDye skinDye;

    //    public SkinDescriptor() {
//        this.skinDye = new SkinDye();
//        this.identifier = new SkinIdentifier(0, null, 0, null);
//    }

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, Palette.EMPTY);
    }

    public SkinDescriptor(String identifier, ISkinType type, Palette palette) {
        this.identifier = identifier;
        this.type = type;
        this.palette = palette;
    }

    public SkinDescriptor(CompoundNBT nbt) {
        this.identifier = nbt.getString(NBT_KEY_SKIN_IDENTIFIER);
        this.type = SkinTypes.byName(nbt.getString(NBT_KEY_SKIN_TYPE));
        this.palette = new Palette();
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

    @Nonnull
    public ItemStack asItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = new ItemStack(SkinItems.SKIN.get());
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(NBT_KEY_SKIN_IDENTIFIER, identifier);
        nbt.putString(NBT_KEY_SKIN_TYPE, type.getRegistryName());
        itemStack.addTagElement(NBT_KEY_SKIN, nbt);
        return itemStack;
    }


    public boolean isEmpty() {
        return identifier.isEmpty();
    }

    //    public SkinDescriptor(Skin skin) {
//        this(new SkinIdentifier(skin.lightHash(), null, 0, skin.getType()));
//    }
//
//    public SkinDescriptor(ISkinDescriptor skinPointer) {
//        this.skinDye = new SkinDye(skinPointer.getSkinDye());
//    }
//
//    public SkinDescriptor(ISkinIdentifier identifier) {
//        this.identifier = identifier;
//        this.skinDye = new SkinDye();
//    }
//
//    public SkinDescriptor(ISkinIdentifier identifier, ISkinDye skinDye) {
//        this.identifier = identifier;
//        if (skinDye != null) {
//            this.skinDye = new SkinDye(skinDye);
//        } else {
//            this.skinDye = new SkinDye();
//        }
//    }
//

    public Palette getPalette() {
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
