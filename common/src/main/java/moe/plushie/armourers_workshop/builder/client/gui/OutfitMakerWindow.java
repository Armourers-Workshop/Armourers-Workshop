package moe.plushie.armourers_workshop.builder.client.gui;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UITextField;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateOutfitMakerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class OutfitMakerWindow extends MenuWindow<OutfitMakerMenu> {

    private final UIButton saveButton = new UIButton(new CGRect(146, 120, 20, 20));

    private final UITextField nameTextField = new UITextField(new CGRect(8, 18, 158, 16));
    private final UITextField flavourTextField = new UITextField(new CGRect(8, 38, 158, 16));

    private final OutfitMakerBlockEntity blockEntity;

    public OutfitMakerWindow(OutfitMakerMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setBackgroundView(UIImage.of(ModTextures.OUTFIT_MAKER).build());
        this.setFrame(new CGRect(0, 0, 176, 240));
        this.blockEntity = container.getBlockEntity();
    }

    @Override
    public void init() {
        super.init();

        setupTextField(nameTextField, blockEntity.getItemName(), "outfit-maker.skinName");
        setupTextField(flavourTextField, blockEntity.getItemFlavour(), "outfit-maker.skinFlavour");

        var offsets = new HashMap<Integer, CGPoint>();
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 0));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
        offsets.put(UIControl.State.SELECTED | UIControl.State.NORMAL, new CGPoint(0, 1));
        offsets.put(UIControl.State.SELECTED | UIControl.State.HIGHLIGHTED, new CGPoint(1, 1));
        saveButton.setImage(UIImage.of(ModTextures.OUTFIT_MAKER).uv(176, 224).fixed(16, 16).unzip(offsets::get).build(), UIControl.State.ALL);
        saveButton.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        saveButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, OutfitMakerWindow::saveSkin);
        addSubview(saveButton);
    }

    private void saveSkin(UIControl sender) {
        if (!menu.shouldCrafting()) {
            return;
        }
        var origin = Minecraft.getInstance().getUser().getGameProfile();
        var nbt = DataSerializers.writeGameProfile(new CompoundTag(), origin);
        NetworkManager.sendToServer(UpdateOutfitMakerPacket.Field.ITEM_CRAFTING.buildPacket(blockEntity, nbt));
    }

    private void saveSkinInfo(UIControl textField) {
        var value = nameTextField.text();
        var field = UpdateOutfitMakerPacket.Field.ITEM_NAME;
        if (textField == flavourTextField) {
            field = UpdateOutfitMakerPacket.Field.ITEM_FLAVOUR;
            value = flavourTextField.text();
        }
        if (Objects.equals(value, field.get(blockEntity))) {
            return; // ignore when value not changes
        }
        NetworkManager.sendToServer(field.buildPacket(blockEntity, value));
    }

    private void setupTextField(UITextField textField, String value, String placeholderKey) {
        textField.setMaxLength(40);
        textField.setText(value);
        textField.setPlaceholder(NSString.localizedString(placeholderKey));
        textField.addTarget(this, UIControl.Event.EDITING_DID_END, OutfitMakerWindow::saveSkinInfo);
        addSubview(textField);
    }

    @Override
    public boolean shouldRenderExtendScreen() {
        return true;
    }
}
