package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerClearDialog;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerCopyDialog;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog.ArmourerReplaceDialog;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class ArmourerBlockSetting extends ArmourerBaseSetting {

    protected final ArmourerBlockEntity blockEntity;

    protected ArmourerBlockSetting(ArmourerMenu container) {
        super("armourer.blockUtils");
        this.blockEntity = container.getBlockEntity();
    }

    @Override
    public void init() {
        super.init();

        var clearBtn = new UIButton(new CGRect(10, 20, 70, 20));
        clearBtn.setTitle(getDisplayText("clear"), UIControl.State.ALL);
        clearBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        clearBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        clearBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::clearAction);
        addSubview(clearBtn);

        var copyBtn = new UIButton(new CGRect(10, 45, 70, 20));
        copyBtn.setTitle(getDisplayText("copy"), UIControl.State.ALL);
        copyBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        copyBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        copyBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::copyAction);
        addSubview(copyBtn);

        var replaceBtn = new UIButton(new CGRect(10, 70, 70, 20));
        replaceBtn.setTitle(getDisplayText("replace"), UIControl.State.ALL);
        replaceBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        replaceBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        replaceBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerBlockSetting::replaceAction);
        addSubview(replaceBtn);
    }

    private void clearAction(UIControl sender) {
        var dialog = new ArmourerClearDialog(getPartTypes(true));
        dialog.setTitle(NSString.localizedString("armourer.dialog.clear.title"));
        dialog.showInView(this, () -> {
            if (dialog.isCancelled()) {
                return;
            }
            var serializer = new TagSerializer();
            serializer.write(UpdateArmourerPacket.CodingKeys.PART_TYPE, dialog.selectedPartType());
            serializer.write(UpdateArmourerPacket.CodingKeys.CLEAR_CUBES, dialog.isClearBlocks());
            serializer.write(UpdateArmourerPacket.CodingKeys.CLEAR_MARKERS, dialog.isClearMarkers());
            serializer.write(UpdateArmourerPacket.CodingKeys.CLEAR_PAINTS, dialog.isClearPaints());
            NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_CLEAR.buildPacket(blockEntity, serializer.tag()));
        });
    }

    private void copyAction(UIControl sender) {
        var dialog = new ArmourerCopyDialog(getPartTypes(false));
        dialog.setTitle(NSString.localizedString("armourer.dialog.copy.title"));
        dialog.showInView(this, () -> {
            if (dialog.isCancelled()) {
                return;
            }
            var serializer = new TagSerializer();
            serializer.write(UpdateArmourerPacket.CodingKeys.SOURCE_PART_TYPE, dialog.sourcePartType());
            serializer.write(UpdateArmourerPacket.CodingKeys.DESTINATION_PART_TYPE, dialog.destinationPartType());
            serializer.write(UpdateArmourerPacket.CodingKeys.COPY_MIRROR, dialog.isMirror());
            serializer.write(UpdateArmourerPacket.CodingKeys.COPY_PAINT_DATA, dialog.isCopyPaintData());
            NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_COPY.buildPacket(blockEntity, serializer.tag()));
        });
    }

    private void replaceAction(UIControl sender) {
        var dialog = new ArmourerReplaceDialog();
        dialog.setTitle(NSString.localizedString("armourer.dialog.replace.title"));
        dialog.showInView(this, () -> {
            var level = Minecraft.getInstance().level;
            if (dialog.isCancelled() || level == null) {
                return;
            }
            var selector = dialog.selector();
            var applier = dialog.applier();
            if (selector.isEmpty() && applier.isEmpty()) {
                return;
            }
            var serializer = new TagSerializer(SerializationContext.from(level));
            serializer.write(UpdateArmourerPacket.CodingKeys.SOURCE_ITEM, selector);
            serializer.write(UpdateArmourerPacket.CodingKeys.DESTINATION_ITEM, applier);
            serializer.write(UpdateArmourerPacket.CodingKeys.KEEP_COLOR, dialog.isKeepColor());
            serializer.write(UpdateArmourerPacket.CodingKeys.KEEP_PAINT_TYPE, dialog.isKeepPaintType());
            NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_REPLACE.buildPacket(blockEntity, serializer.tag()));
        });
    }

    public ArrayList<SkinPartType> getPartTypes(boolean usesAll) {
        var skinType = blockEntity.skinType();
        var skinProperties = blockEntity.skinProperties();
        var partTypes = new ArrayList<SkinPartType>();
        if (usesAll) {
            partTypes.add(0, SkinPartTypes.UNKNOWN);
        }
        if (skinType != SkinTypes.BLOCK) {
            partTypes.addAll(skinType.parts());
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
