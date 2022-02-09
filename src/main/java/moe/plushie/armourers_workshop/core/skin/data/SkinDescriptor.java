package moe.plushie.armourers_workshop.core.skin.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.common.item.SkinItems;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    public static final String NBT_KEY_SKIN = "armourersWorkshop";
    public static final String NBT_KEY_SKIN_NAME = "name";
    public static final String NBT_KEY_SKIN_IDENTIFIER = "identifier";

    private final static Cache<ItemStack, SkinDescriptor> SKINS = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final String name;
    private final String identifier;
    private final SkinDye dye;

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
        this(identifier, "", SkinDye.EMPTY);
    }

    public SkinDescriptor(String identifier, String name, SkinDye dye) {
        this.name = name;
        this.identifier = identifier;
        this.dye = dye;
    }

    public SkinDescriptor(CompoundNBT nbt) {
        this.name = nbt.getString(NBT_KEY_SKIN_NAME);
        this.identifier = nbt.getString(NBT_KEY_SKIN_IDENTIFIER);
        this.dye = new SkinDye();
//        this.skinDye = new SkinDye();
//        this.identifier = new SkinIdentifier(0, null, 0, null);
    }


    @Nonnull
    public static SkinDescriptor of(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains(NBT_KEY_SKIN)) {
            return EMPTY;
        }
        SkinDescriptor descriptor = SKINS.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new SkinDescriptor(nbt.getCompound(NBT_KEY_SKIN));
        SKINS.put(itemStack, descriptor);
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
        nbt.putString(NBT_KEY_SKIN_NAME, name);
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

    public SkinDye getDye() {
        return dye;
    }

    public String getName() {
        return name;
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
