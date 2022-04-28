package moe.plushie.armourers_workshop.builder.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.BooleanToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.IntegerToolProperty;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdatePaintingToolPacket;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class PaintingToolScreen extends Screen {

    protected final ArrayList<Pair<IPaintingToolProperty<?>, Button>> properties = new ArrayList<>();

    protected final Hand hand;
    protected final ItemStack itemStack;

    protected int leftPos;
    protected int topPos;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int imageWidth = 176;
    protected int imageHeight = 24; // 24 + n + 8

    public PaintingToolScreen(ITextComponent title, ArrayList<IPaintingToolProperty<?>> properties, ItemStack itemStack, Hand hand) {
        super(title);
        this.hand = hand;
        this.itemStack = itemStack;
        properties.forEach(property -> {
            Button button = createOptionView(property);
            if (button != null) {
                this.properties.add(Pair.of(property, button));
                this.imageHeight += button.getHeight() + 8;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.titleLabelX = leftPos + imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = topPos + 7;
        int dy = topPos + 24;
        for (Pair<IPaintingToolProperty<?>, Button> pair : properties) {
            Button button = pair.getSecond();
            button.x = leftPos + 8;
            button.y = dy;
            button.setWidth(imageWidth - 16);
            addButton(button);
            dy += button.getHeight() + 8;
        }
    }

    //    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.COMMON);
//    private static final int MARGIN_TOP = 22;
//    private static final int MARGIN_LEFT = 6;
//    private static final int CONTROL_PADDING = 6;
//
//    private final int guiWidth;
//    private int guiHeight;
//    protected int guiLeft;
//    protected int guiTop;
//    protected ItemStack stack;
//    private String guiName;
//    private final ArrayList<ToolOption<?>> toolOptionsList;
//
//    public GuiToolOptions(ItemStack stack) {
//        this.stack = stack;
//        toolOptionsList = new ArrayList<ToolOption<?>>();
//        ((IConfigurableTool) stack.getItem()).getToolOptions(toolOptionsList);
//        guiWidth = 175;
//        guiHeight = 61;
//        this.guiName = stack.getDisplayName();
//    }
//
//    @Override
//    public void initGui() {
//        super.initGui();
//        buttonList.clear();
//
//        guiLeft = width / 2 - guiWidth / 2;
//
//        // Work out how tall the GUI needs to be.
//        int controlHeight = MARGIN_TOP;
//        for (int i = 0; i < toolOptionsList.size(); i++) {
//            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
//        }
//        guiHeight = controlHeight;
//        guiTop = height / 2 - guiHeight / 2;
//
//        // Place the controls on the GUI.
//        controlHeight = MARGIN_TOP;
//        for (int i = 0; i < toolOptionsList.size(); i++) {
//            GuiButton control = toolOptionsList.get(i).getGuiControl(i, guiLeft + MARGIN_LEFT, controlHeight + guiTop, stack.getTagCompound());
//            buttonList.add(control);
//            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
//        }
//    }
//

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        this.renderBackground(matrixStack);
        RenderUtils.bind(RenderUtils.TEX_COMMON);
        GuiUtils.drawContinuousTexturedBox(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, 0);
        this.renderLabels(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
    }

    //
//    @Override
//    protected void actionPerformed(GuiButton button) throws IOException {
//        NBTTagCompound compound = new NBTTagCompound();
//        writeToCompound(compound);
//        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
//    }
//
//    @Override
//    protected void keyTyped(char key, int keyCode) throws IOException {
//        super.keyTyped(key, keyCode);
//        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
//            this.mc.player.closeScreen();
//        }
//    }
//
//    private void renderGuiTitle(FontRenderer fontRenderer, String name) {
//        int xPos = this.guiWidth / 2 - fontRenderer.getStringWidth(name) / 2;
//        fontRenderer.drawString(name, this.guiLeft + xPos, this.guiTop + 6, 4210752);
//    }
//
//    @Override
//    public boolean doesGuiPauseGame() {
//        return false;
//    }
//
//    @Override
//    public void onGuiClosed() {
//        NBTTagCompound compound = new NBTTagCompound();
//        writeToCompound(compound);
//        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
//    }
//
//    public void writeToCompound(NBTTagCompound compound) {
//        for (int i = 0; i < toolOptionsList.size(); i++) {
//            toolOptionsList.get(i).writeGuiControlToNBT(buttonList.get(i), compound);
//        }
//    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void sendToServer() {
        UpdatePaintingToolPacket packet = new UpdatePaintingToolPacket(hand, itemStack);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    protected ITextComponent getOptionText(String key) {
        return TranslateUtils.title("tooloption.armourers_workshop" + "." + key);
    }

    private Button createOptionView(IPaintingToolProperty<?> property) {
        if (property instanceof BooleanToolProperty) {
            BooleanToolProperty property1 = (BooleanToolProperty) property;
            ITextComponent title = getOptionText(property.getName());
            return new AWCheckBox(0, 0, 9, 9, title, property1.get(itemStack), button -> {
                if (button instanceof AWCheckBox) {
                    boolean value = ((AWCheckBox)button).isSelected();
                    property1.setValue(itemStack, value);
                    sendToServer();
                }
            });
        }
        if (property instanceof IntegerToolProperty) {
            IntegerToolProperty property1 = (IntegerToolProperty) property;
            ITextComponent title = getOptionText(property.getName());
            Function<Double, ITextComponent> titleProvider = currentValue -> {
                StringTextComponent formattedValue = new StringTextComponent("");
                formattedValue.append(title);
                formattedValue.append(" ");
                formattedValue.append(String.format("%.1f", currentValue));
                return formattedValue;
            };
            AWSliderBox box = new AWSliderBox(0, 0, 150, 20, titleProvider, property1.getMinValue(), property1.getMaxValue(), Objects::hash);
            box.setHands(false);
            box.setValue(property1.get(itemStack));
            box.setEndListener(button -> {
                if (button instanceof AWSliderBox) {
                    int value = (int) ((AWSliderBox) button).getValue();
                    property1.setValue(itemStack, value);
                    sendToServer();
                }
            });
            return box;
        }
        return null;
    }
}
