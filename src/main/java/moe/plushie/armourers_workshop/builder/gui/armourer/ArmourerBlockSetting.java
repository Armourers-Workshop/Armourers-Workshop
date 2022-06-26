package moe.plushie.armourers_workshop.builder.gui.armourer;

import moe.plushie.armourers_workshop.api.common.IItemBlockSelector;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.gui.armourer.dialog.ArmourerClearDialog;
import moe.plushie.armourers_workshop.builder.gui.armourer.dialog.ArmourerCopyDialog;
import moe.plushie.armourers_workshop.builder.gui.armourer.dialog.ArmourerReplaceDialog;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;

public class ArmourerBlockSetting extends ArmourerBaseSetting {

    protected final ArmourerTileEntity tileEntity;

    private ExtendedButton buttonClear;
    private ExtendedButton buttonCopy;
    private ExtendedButton buttonReplace;

    protected ArmourerScreen screen;

    protected ArmourerBlockSetting(ArmourerContainer container, ArmourerScreen screen) {
        super("inventory.armourers_workshop.armourer.blockUtils");
        this.tileEntity = container.getTileEntity(ArmourerTileEntity.class);
        this.screen = screen;
    }

    @Override
    protected void init() {
        super.init();

        this.buttonClear = new ExtendedButton(leftPos + 10, topPos + 20, 70, 20, getDisplayText("clear"), this::clearAction);
        this.buttonCopy = new ExtendedButton(leftPos + 10, topPos + 45, 70, 20, getDisplayText("copy"), this::copyAction);
        this.buttonReplace = new ExtendedButton(leftPos + 10, topPos + 70, 70, 20, getDisplayText("replace"), this::replaceAction);

        this.addButton(buttonClear);
        this.addButton(buttonCopy);
        this.addButton(buttonReplace);
    }

    private void clearAction(Button sender) {
        ITextComponent title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.clear.title");
        ArmourerClearDialog dialog = new ArmourerClearDialog(getPartTypes(true), title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.putBoolean(AWConstants.NBT.SKIN_CUBES, dialog1.isClearBlocks());
            nbt.putBoolean(AWConstants.NBT.SKIN_PAINTS, dialog1.isClearPaints());
            nbt.putBoolean(AWConstants.NBT.SKIN_MARKERS, dialog1.isClearMarkers());
            nbt.putString(AWConstants.NBT.SKIN_PART_TYPE, dialog1.getSelectedPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_CLEAR;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
            NetworkHandler.getInstance().sendToServer(packet);
        });
    }

    private void copyAction(Button sender) {
        ITextComponent title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.copy.title");
        ArmourerCopyDialog dialog = new ArmourerCopyDialog(getPartTypes(false), title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.putBoolean(AWConstants.NBT.MIRROR, dialog1.isMirror());
            nbt.putBoolean(AWConstants.NBT.SKIN_PAINTS, dialog1.isCopyPaintData());
            nbt.putString(AWConstants.NBT.SOURCE, dialog1.getSourcePartType().getRegistryName().toString());
            nbt.putString(AWConstants.NBT.DESTINATION, dialog1.getDestinationPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_COPY;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
            NetworkHandler.getInstance().sendToServer(packet);
        });
    }

    private void replaceAction(Button sender) {
        ITextComponent title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.replace.title");
        ArmourerReplaceDialog dialog = new ArmourerReplaceDialog(title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundNBT source = new CompoundNBT();
            ItemStack selector = dialog1.getSelector();
            if (selector.getItem() instanceof IItemBlockSelector) {
                IItemBlockSelector value = (IItemBlockSelector) selector.getItem();
                AWDataSerializers.putBlock(source, AWConstants.NBT.BLOCK, value.getBlock(selector));
                AWDataSerializers.putPaintColor(source, AWConstants.NBT.COLOR, value.getItemColor(selector), null);
            }
            CompoundNBT destination = new CompoundNBT();
            ItemStack applier = dialog1.getApplier();
            if (applier.getItem() instanceof IItemBlockSelector) {
                IItemBlockSelector value = (IItemBlockSelector) applier.getItem();
                AWDataSerializers.putBlock(destination, AWConstants.NBT.BLOCK, value.getBlock(applier));
                AWDataSerializers.putPaintColor(destination, AWConstants.NBT.COLOR, value.getItemColor(applier), null);
            }
            if (source.isEmpty() || destination.isEmpty()) {
                return;
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.put(AWConstants.NBT.SOURCE, source);
            nbt.put(AWConstants.NBT.DESTINATION, destination);
            nbt.putBoolean(AWConstants.NBT.KEEP_COLOR, dialog1.isKeepColor());
            nbt.putBoolean(AWConstants.NBT.KEEP_PAINT_TYPE, dialog1.isKeepPaintType());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_REPLACE;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
            NetworkHandler.getInstance().sendToServer(packet);
        });
    }

    public ArrayList<ISkinPartType> getPartTypes(boolean usesAll) {
        ISkinType skinType = tileEntity.getSkinType();
        ISkinProperties skinProperties = tileEntity.getSkinProperties();
        ArrayList<ISkinPartType> partTypes = new ArrayList<>();
        if (usesAll) {
            partTypes.add(0, SkinPartTypes.UNKNOWN);
        }
        if (skinType != SkinTypes.BLOCK) {
            partTypes.addAll(skinType.getParts());
        } else {
            if (skinProperties.get(SkinProperty.BLOCK_MULTIBLOCK)) {
                partTypes.add(SkinPartTypes.BLOCK_MULTI);
            } else {
                partTypes.add(SkinPartTypes.BLOCK);
            }
        }
        return partTypes;
    }
}
