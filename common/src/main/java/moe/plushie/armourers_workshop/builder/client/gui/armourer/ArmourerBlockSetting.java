package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerClearDialog;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerCopyDialog;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerReplaceDialog;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class ArmourerBlockSetting extends ArmourerBaseSetting {

    protected final ArmourerBlockEntity tileEntity;

    protected AWExtendedButton buttonClear;
    protected AWExtendedButton buttonCopy;
    protected AWExtendedButton buttonReplace;

    protected ArmourerScreen screen;

    protected ArmourerBlockSetting(ArmourerMenu container, ArmourerScreen screen) {
        super("inventory.armourers_workshop.armourer.blockUtils");
        this.tileEntity = container.getTileEntity(ArmourerBlockEntity.class);
        this.screen = screen;
    }

    @Override
    protected void init() {
        super.init();

        this.buttonClear = new AWExtendedButton(leftPos + 10, topPos + 20, 70, 20, getDisplayText("clear"), this::clearAction);
        this.buttonCopy = new AWExtendedButton(leftPos + 10, topPos + 45, 70, 20, getDisplayText("copy"), this::copyAction);
        this.buttonReplace = new AWExtendedButton(leftPos + 10, topPos + 70, 70, 20, getDisplayText("replace"), this::replaceAction);

        this.addButton(buttonClear);
        this.addButton(buttonCopy);
        this.addButton(buttonReplace);
    }

    private void clearAction(Button sender) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.clear.title");
        ArmourerClearDialog dialog = new ArmourerClearDialog(getPartTypes(true), title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(Constants.Key.SKIN_CUBES, dialog1.isClearBlocks());
            nbt.putBoolean(Constants.Key.SKIN_PAINTS, dialog1.isClearPaints());
            nbt.putBoolean(Constants.Key.SKIN_MARKERS, dialog1.isClearMarkers());
            nbt.putString(Constants.Key.SKIN_PART_TYPE, dialog1.getSelectedPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_CLEAR;
            NetworkManager.sendToServer(new UpdateArmourerPacket(tileEntity, field, nbt));
        });
    }

    private void copyAction(Button sender) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.copy.title");
        ArmourerCopyDialog dialog = new ArmourerCopyDialog(getPartTypes(false), title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(Constants.Key.MIRROR, dialog1.isMirror());
            nbt.putBoolean(Constants.Key.SKIN_PAINTS, dialog1.isCopyPaintData());
            nbt.putString(Constants.Key.SOURCE, dialog1.getSourcePartType().getRegistryName().toString());
            nbt.putString(Constants.Key.DESTINATION, dialog1.getDestinationPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_COPY;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
            NetworkManager.sendToServer(packet);
        });
    }

    private void replaceAction(Button sender) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.replace.title");
        ArmourerReplaceDialog dialog = new ArmourerReplaceDialog(title);
        screen.present(dialog, dialog1 -> {
            if (dialog1.isCancelled()) {
                return;
            }
            CompoundTag source = new CompoundTag();
            ItemStack selector = dialog1.getSelector();
            if (selector.getItem() instanceof IItemColorProvider) {
                selector.save(source);
            }
            CompoundTag destination = new CompoundTag();
            ItemStack applier = dialog1.getApplier();
            if (applier.getItem() instanceof IItemColorProvider) {
                applier.save(destination);
            }
            if (source.isEmpty() && destination.isEmpty()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.put(Constants.Key.SOURCE, source);
            nbt.put(Constants.Key.DESTINATION, destination);
            nbt.putBoolean(Constants.Key.KEEP_COLOR, dialog1.isKeepColor());
            nbt.putBoolean(Constants.Key.KEEP_PAINT_TYPE, dialog1.isKeepPaintType());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_REPLACE;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
            NetworkManager.sendToServer(packet);
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
