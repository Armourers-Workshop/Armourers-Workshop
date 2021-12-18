package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class SkinDescriptor implements ISkinDescriptor {

    public static final String TAG_SKIN_DATA = "armourersWorkshop";


    public final String identifier;
//    private ISkinIdentifier identifier;
//    public ISkinDye skinDye;

//    public SkinDescriptor() {
//        this.skinDye = new SkinDye();
//        this.identifier = new SkinIdentifier(0, null, 0, null);
//    }

    public SkinDescriptor(CompoundNBT nbt) {
        this.identifier = nbt.getString("identifier");
//        this.skinDye = new SkinDye();
//        this.identifier = new SkinIdentifier(0, null, 0, null);
    }

    public static SkinDescriptor of(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTagElement(TAG_SKIN_DATA);
        if (nbt != null) {
            return new SkinDescriptor(nbt);
        }
        return null;
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
}
