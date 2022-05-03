package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.tileentity.AbstractContainerTileEntity;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class ArmourerTileEntity extends AbstractTileEntity {

    protected int flags = 0;

    protected ISkinType skinType = SkinTypes.ARMOR_HEAD;
    protected SkinProperties skinProperties = new SkinProperties();
    protected PlayerTextureDescriptor textureDescriptor = PlayerTextureDescriptor.EMPTY;

    public ArmourerTileEntity() {
        super(ModTileEntities.ARMOURER);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        this.skinType = SkinTypes.byName(AWDataSerializers.getString(nbt, AWConstants.NBT.SKIN_TYPE, SkinTypes.ARMOR_HEAD.getRegistryName().toString()));
        this.skinProperties = AWDataSerializers.getSkinProperties(nbt, AWConstants.NBT.SKIN_PROPERTIES);
        this.textureDescriptor = AWDataSerializers.getTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, PlayerTextureDescriptor.EMPTY);
        this.flags = AWDataSerializers.getInt(nbt, AWConstants.NBT.FLAGS, 0);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putString(nbt, AWConstants.NBT.SKIN_TYPE, skinType.getRegistryName().toString(), null);
        AWDataSerializers.putSkinProperties(nbt, AWConstants.NBT.SKIN_PROPERTIES, skinProperties);
        AWDataSerializers.putTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, textureDescriptor, PlayerTextureDescriptor.EMPTY);
        AWDataSerializers.putInt(nbt, AWConstants.NBT.FLAGS, flags, 0);
    }

    public ISkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(ISkinType skinType) {
        this.skinType = skinType;
        this.sendBlockUpdates();
    }

    public SkinProperties getSkinProperties() {
        return skinProperties;
    }

    public void setSkinProperties(SkinProperties skinProperties) {
        this.skinProperties = skinProperties;
        this.sendBlockUpdates();
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
        this.sendBlockUpdates();
    }


    public PlayerTextureDescriptor getTextureDescriptor() {
        return textureDescriptor;
    }

    public void setTextureDescriptor(PlayerTextureDescriptor textureDescriptor) {
        this.textureDescriptor = textureDescriptor;
        this.sendBlockUpdates();
    }

    public boolean isShowGuides() {
        return (flags & 0x01) != 0;
    }

    public void setShowGuides(boolean value) {
        if (value) {
            flags |= 0x01;
        } else {
            flags &= ~0x01;
        }
    }

    public boolean isShowHelper() {
        return (flags & 0x02) != 0;
    }

    public void setShowHelper(boolean value) {
        if (value) {
            flags |= 0x02;
        } else {
            flags &= ~0x02;
        }
    }

    private void sendBlockUpdates() {
        this.setChanged();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE);
        }
    }
}
