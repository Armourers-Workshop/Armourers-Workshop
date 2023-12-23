package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
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
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ArmourerBlockSetting extends ArmourerBaseSetting {

    protected final ArmourerBlockEntity blockEntity;

    protected ArmourerBlockSetting(ArmourerMenu container) {
        super("armourer.blockUtils");
        this.blockEntity = container.getBlockEntity();
    }

    @Override
    public void init() {
        super.init();

        UIButton clearBtn = new UIButton(new CGRect(10, 20, 70, 20));
        clearBtn.setTitle(getDisplayText("clear"), UIControl.State.ALL);
        clearBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        clearBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        clearBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::clearAction);
        addSubview(clearBtn);

        UIButton copyBtn = new UIButton(new CGRect(10, 45, 70, 20));
        copyBtn.setTitle(getDisplayText("copy"), UIControl.State.ALL);
        copyBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        copyBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        copyBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::copyAction);
        addSubview(copyBtn);

        UIButton replaceBtn = new UIButton(new CGRect(10, 70, 70, 20));
        replaceBtn.setTitle(getDisplayText("replace"), UIControl.State.ALL);
        replaceBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        replaceBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        replaceBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::replaceAction);
        addSubview(replaceBtn);
    }

    private void clearAction(UIControl sender) {
        ArmourerClearDialog dialog = new ArmourerClearDialog(getPartTypes(true));
        dialog.setTitle(NSString.localizedString("armourer.dialog.clear.title"));
        dialog.showInView(this, () -> {
            if (dialog.isCancelled()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(Constants.Key.SKIN_CUBES, dialog.isClearBlocks());
            nbt.putBoolean(Constants.Key.SKIN_PAINTS, dialog.isClearPaints());
            nbt.putBoolean(Constants.Key.SKIN_MARKERS, dialog.isClearMarkers());
            nbt.putString(Constants.Key.SKIN_PART_TYPE, dialog.getSelectedPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_CLEAR;
            NetworkManager.sendToServer(new UpdateArmourerPacket(blockEntity, field, nbt));
        });
    }

    private void copyAction(UIControl sender) {
        ArmourerCopyDialog dialog = new ArmourerCopyDialog(getPartTypes(false));
        dialog.setTitle(NSString.localizedString("armourer.dialog.copy.title"));
        dialog.showInView(this, () -> {
            if (dialog.isCancelled()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean(Constants.Key.MIRROR, dialog.isMirror());
            nbt.putBoolean(Constants.Key.SKIN_PAINTS, dialog.isCopyPaintData());
            nbt.putString(Constants.Key.SOURCE, dialog.getSourcePartType().getRegistryName().toString());
            nbt.putString(Constants.Key.DESTINATION, dialog.getDestinationPartType().getRegistryName().toString());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_COPY;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(blockEntity, field, nbt);
            NetworkManager.sendToServer(packet);
        });
    }

    private void replaceAction(UIControl sender) {
        ArmourerReplaceDialog dialog = new ArmourerReplaceDialog();
        dialog.setTitle(NSString.localizedString("armourer.dialog.replace.title"));
        dialog.showInView(this, () -> {
            if (dialog.isCancelled()) {
                return;
            }
            CompoundTag source = new CompoundTag();
            ItemStack selector = dialog.getSelector();
            if (selector.getItem() instanceof IItemColorProvider) {
                selector.save(source);
            }
            CompoundTag destination = new CompoundTag();
            ItemStack applier = dialog.getApplier();
            if (applier.getItem() instanceof IItemColorProvider) {
                applier.save(destination);
            }
            if (source.isEmpty() && destination.isEmpty()) {
                return;
            }
            CompoundTag nbt = new CompoundTag();
            nbt.put(Constants.Key.SOURCE, source);
            nbt.put(Constants.Key.DESTINATION, destination);
            nbt.putBoolean(Constants.Key.KEEP_COLOR, dialog.isKeepColor());
            nbt.putBoolean(Constants.Key.KEEP_PAINT_TYPE, dialog.isKeepPaintType());
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_REPLACE;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(blockEntity, field, nbt);
            NetworkManager.sendToServer(packet);
        });
    }

    public ArrayList<ISkinPartType> getPartTypes(boolean usesAll) {
        ISkinType skinType = blockEntity.getSkinType();
        ISkinProperties skinProperties = blockEntity.getSkinProperties();
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
