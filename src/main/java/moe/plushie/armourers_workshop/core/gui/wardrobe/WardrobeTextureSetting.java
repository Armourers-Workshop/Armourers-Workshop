package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.container.WardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeTextureSetting extends AWTabPanel {

    private final Wardrobe wardrobe;
    private final HashMap<PlayerTextureDescriptor.Source, String> defaultValues = new HashMap<>();

    private AWComboBox comboList;
    private TextFieldWidget textBox;

    private PlayerTextureDescriptor lastDescriptor = PlayerTextureDescriptor.EMPTY;
    private PlayerTextureDescriptor.Source lastSource = PlayerTextureDescriptor.Source.NONE;

    public WardrobeTextureSetting(WardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_texture");
        this.wardrobe = container.getWardrobe();
        this.prepareDefaultValue();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.addTextField(leftPos + 83 + 1, topPos + 70, defaultValues.get(lastSource));
        this.addComboList(leftPos + 83, topPos + 27, lastSource);
        this.addButton(new ExtendedButton(leftPos + 83, topPos + 90, 100, 20, getDisplayText("set"), this::submit));
    }

    @Override
    public void removed() {
        super.removed();
        this.comboList = null;
        this.textBox = null;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.textBox != null) {
            this.textBox.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        boolean typed = super.keyPressed(key, p_231046_2_, p_231046_3_);
        if (!typed && textBox != null && textBox.isFocused() && key == GLFW.GLFW_KEY_ENTER) {
            submit(null);
        }
        return typed;
    }

    private void prepareDefaultValue() {
        Entity entity = wardrobe.getEntity();
        if (!(entity instanceof MannequinEntity)) {
            return;
        }
        defaultValues.clear();
        lastDescriptor = entity.getEntityData().get(MannequinEntity.DATA_TEXTURE);
        lastSource = lastDescriptor.getSource();
        if (lastSource == PlayerTextureDescriptor.Source.USER) {
            defaultValues.put(lastSource, lastDescriptor.getName());
        }
        if (lastSource == PlayerTextureDescriptor.Source.URL) {
            defaultValues.put(lastSource, lastDescriptor.getURL());
        }
    }

    private void submit(Button button) {
        textBox.setFocus(false);
        int index = comboList.getSelectedIndex();
        PlayerTextureDescriptor.Source source = PlayerTextureDescriptor.Source.values()[index + 1];
        applyText(source, textBox.getValue());
    }

    private void changeSource(PlayerTextureDescriptor.Source newSource) {
        if (this.lastSource == newSource) {
            return;
        }
        defaultValues.put(lastSource, textBox.getValue());
        textBox.setValue(defaultValues.getOrDefault(newSource, ""));
        textBox.setFocus(false);
        textBox.moveCursorToStart();
        comboList.setSelectedIndex(newSource.ordinal() - 1);
        lastSource = newSource;
    }

    private void applyText(PlayerTextureDescriptor.Source source, String value) {
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.EMPTY;
        if (Strings.isNotEmpty(value)) {
            if (source == PlayerTextureDescriptor.Source.URL) {
                descriptor = new PlayerTextureDescriptor(value);
            }
            if (source == PlayerTextureDescriptor.Source.USER) {
                descriptor = new PlayerTextureDescriptor(new GameProfile(null, value));
            }
        }
        PlayerTextureLoader.getInstance().loadTextureDescriptor(descriptor, resolvedDescriptor -> {
            PlayerTextureDescriptor newValue = resolvedDescriptor.orElse(PlayerTextureDescriptor.EMPTY);
            if (lastDescriptor.equals(newValue)) {
                return; // no changes
            }
            lastSource = PlayerTextureDescriptor.Source.NONE;
            lastDescriptor = newValue;
            UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_TEXTURE, newValue);
            NetworkHandler.getInstance().sendToServer(packet);
            // update to use
            defaultValues.put(newValue.getSource(), newValue.getValue());
            changeSource(newValue.getSource());
        });
    }

    private void addComboList(int x, int y, PlayerTextureDescriptor.Source source) {
        int selectedIndex = 0;
        if (source != PlayerTextureDescriptor.Source.NONE) {
            selectedIndex = source.ordinal() - 1;
        }
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        items.add(new AWComboBox.ComboItem(getDisplayText("dropdown.user")));
        items.add(new AWComboBox.ComboItem(getDisplayText("dropdown.url")));
        comboList = new AWComboBox(x, y, 80, 14, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int index = ((AWComboBox) button).getSelectedIndex();
                changeSource(PlayerTextureDescriptor.Source.values()[index + 1]);
            }
        });
        addButton(comboList);
    }

    private void addTextField(int x, int y, String defaultValue) {
        textBox = new TextFieldWidget(font, x, y, 165, 16, StringTextComponent.EMPTY);
        textBox.setMaxLength(1024);
        if (Strings.isNotBlank(defaultValue)) {
            textBox.setValue(defaultValue);
        }
        addWidget(textBox);
    }
}
