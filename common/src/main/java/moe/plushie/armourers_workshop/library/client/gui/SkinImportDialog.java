//package moe.plushie.armourers_workshop.library.client.gui;
//
//import com.apple.library.coregraphics.CGRect;
//import com.apple.library.coregraphics.CGSize;
//import com.apple.library.foundation.NSString;
//import com.apple.library.foundation.NSTextAlignment;
//import com.apple.library.uikit.UIButton;
//import com.apple.library.uikit.UICheckBox;
//import com.apple.library.uikit.UIControl;
//import com.apple.library.uikit.UILabel;
//import com.apple.library.uikit.UIScrollView;
//import com.apple.library.uikit.UITextField;
//import com.apple.library.uikit.UITextView;
//import com.apple.library.uikit.UIView;
//import com.google.common.collect.ImmutableMap;
//import com.mojang.authlib.GameProfile;
//import moe.plushie.armourers_workshop.api.core.IResource;
//import moe.plushie.armourers_workshop.api.skin.ISkinType;
//import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
//import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
//import moe.plushie.armourers_workshop.core.client.gui.widget.BaseDialog;
//import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
//import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
//import moe.plushie.armourers_workshop.core.skin.Skin;
//import moe.plushie.armourers_workshop.core.skin.SkinTypes;
//import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
//import moe.plushie.armourers_workshop.core.skin.transformer.SkinPack;
//import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackReader;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModel;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelBone;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelExporter;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelGeometry;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
//import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
//import moe.plushie.armourers_workshop.core.skin.transformer.blockbench.BlockBenchReader;
//import moe.plushie.armourers_workshop.init.ModLog;
//import moe.plushie.armourers_workshop.init.ModTextures;
//import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
//import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
//import moe.plushie.armourers_workshop.utils.SkinFileUtils;
//import moe.plushie.armourers_workshop.utils.math.Vector3f;
//import net.minecraft.client.Minecraft;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipInputStream;
//
//public class SkinImportDialog extends BaseDialog {
//
//    private static final ImmutableMap<ISkinType, Collection<ISkinProperty<Boolean>>> TTTT = new ImmutableMap.Builder<ISkinType, Collection<ISkinProperty<Boolean>>>()
//            .put(SkinTypes.OUTFIT, ObjectUtils.map(
//                    SkinProperty.OVERRIDE_MODEL_HEAD,
//                    SkinProperty.OVERRIDE_MODEL_CHEST,
//                    SkinProperty.OVERRIDE_MODEL_LEFT_ARM,
//                    SkinProperty.OVERRIDE_MODEL_RIGHT_ARM,
//                    SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
//                    SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
//                    SkinProperty.OVERRIDE_OVERLAY_HAT,
//                    SkinProperty.OVERRIDE_OVERLAY_JACKET,
//                    SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE,
//                    SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE,
//                    SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
//                    SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
//                    SkinProperty.OVERRIDE_EQUIPMENT_BOOTS,
//                    SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE,
//                    SkinProperty.OVERRIDE_EQUIPMENT_HELMET,
//                    SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS
//            ))
//            .put(SkinTypes.ARMOR_HEAD, ObjectUtils.map(
//                    SkinProperty.OVERRIDE_MODEL_HEAD,
//                    SkinProperty.OVERRIDE_OVERLAY_HAT,
//                    SkinProperty.OVERRIDE_EQUIPMENT_HELMET
//            ))
//            .put(SkinTypes.ARMOR_CHEST, ObjectUtils.map(
//                    SkinProperty.OVERRIDE_MODEL_CHEST,
//                    SkinProperty.OVERRIDE_MODEL_LEFT_ARM,
//                    SkinProperty.OVERRIDE_MODEL_RIGHT_ARM,
//                    SkinProperty.OVERRIDE_OVERLAY_JACKET,
//                    SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE,
//                    SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE,
//                    SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE
//            ))
//            .put(SkinTypes.ARMOR_FEET, ObjectUtils.map(
//                    SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
//                    SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
//                    SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
//                    SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
//                    SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS
//            ))
//            .put(SkinTypes.ARMOR_LEGS, ObjectUtils.map(
//                    SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
//                    SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
//                    SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
//                    SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
//                    SkinProperty.OVERRIDE_EQUIPMENT_BOOTS
//            ))
//            .put(SkinTypes.ARMOR_WINGS, ObjectUtils.map())
//            .put(SkinTypes.ITEM_SWORD, ObjectUtils.map())
//            .put(SkinTypes.ITEM_SHIELD, ObjectUtils.map())
//            .put(SkinTypes.ITEM_BOW, ObjectUtils.map())
//            .put(SkinTypes.ITEM_TRIDENT, ObjectUtils.map())
//            .put(SkinTypes.ITEM_PICKAXE, ObjectUtils.map())
//            .put(SkinTypes.ITEM_AXE, ObjectUtils.map())
//            .put(SkinTypes.ITEM_SHOVEL, ObjectUtils.map())
//            .put(SkinTypes.ITEM_HOE, ObjectUtils.map())
//            .build();
//
//
//    private final UITextField inputTextField = new UITextField(CGRect.ZERO);
//
//    private final SkinComboBox skinTypeList = new SkinComboBox(CGRect.ZERO);
//
//    private final UITextField textName = new UITextField(CGRect.ZERO);
//    private final UITextView textDescription = new UITextView(CGRect.ZERO);
//
//    private final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);
//
//    private final ArrayList<UICheckBox> boxes = new ArrayList<>();
//    private final HashMap<ISkinProperty<Boolean>, Boolean> values =new HashMap<>();
//
//    public SkinImportDialog(CGRect rect) {
//        super(rect.insetBy(16, 16, 16, 16));
//        setup();
//    }
//
//    private void setup() {
//        float width = bounds().width - 16;
//        float height = bounds().height;
//
//        float left = 8;
//        float top = 20;
//
//        float sw = (width - 4) / 2;
//
//        UILabel t = new UILabel(new CGRect(left, 8, width, 10));
//        t.setText(new NSString("Skin Importer"));
//        t.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
//        t.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
//        addSubview(t);
//
//        inputTextField.setFrame(new CGRect(left + (sw + 4) * 0, top, sw, 16));
//        inputTextField.setPlaceholder(new NSString("Input .bbmodel file name"));
//        inputTextField.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
//        addSubview(inputTextField);
//
//        skinTypeList.setFrame(new CGRect(left + (sw + 4) * 1, top, sw, 16));
//        skinTypeList.setMaxRows(10);
//        skinTypeList.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
//        skinTypeList.reloadSkins(new ArrayList<>(TTTT.keySet()));
//        skinTypeList.setSelectedSkin(SkinTypes.OUTFIT);
//        skinTypeList.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinImportDialog::addProperties);
//        addSubview(skinTypeList);
//
//        top += 20;
//        float qw = width / 2;
//        float ql = left + qw;
//        float qt = top;
//        textName.setFrame(new CGRect(left, top, qw * 0.8f, 16));
//        textName.setPlaceholder(new NSString("Input Skin Name"));
//        textName.setMaxLength(255);
//        textName.setAutoresizingMask(AutoresizingMask.flexibleWidth);
//        addSubview(textName);
//        top += 18;
//
//        textDescription.setFrame(new CGRect(left, top, qw - 4, 64));
//        textDescription.setPlaceholder(new NSString("Input SKin Description"));
//        textDescription.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
//        textDescription.setMaxLength(255);
//        addSubview(textDescription);
//
//        scrollView.setFrame(new CGRect(ql + 4, qt, qw, height - qt - 8 - 24 - 8));
//        insertViewAtIndex(scrollView, 0);
//
//        float bw = 100;
//        float bl = (width - bw) / 2;
//        UIButton act = new UIButton(new CGRect(left + bl, height - 24 - 8, bw, 24));
//        act.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
//        act.setTitle(new NSString("Start"), UIControl.State.NORMAL);
//        act.addTarget(this, UIControl.Event.MOUSE_LEFT_UP, SkinImportDialog::startImport);
//        act.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);
//        addSubview(act);
//
//        addProperties(skinTypeList);
//    }
//
//    private void addProperties(UIControl sender) {
//        boxes.forEach(UIView::removeFromSuperview);
//        Collection<ISkinProperty<Boolean>> bb = TTTT.get(skinTypeList.selectedSkin());
//        if (bb == null || bb.isEmpty()) {
//            return;
//        }
//        float width = scrollView.frame().getWidth();
//        float top = 0;
//        for (ISkinProperty<Boolean> p : bb) {
//            UICheckBox checkBox = new UICheckBox(new CGRect(0, top, width, 10));
//            checkBox.setTitle(NSString.localizedString("armourer.skinSettings." + p.getKey()));
//            checkBox.setSelected(p.getDefaultValue());
//            checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
//                UICheckBox checkBox1 = Objects.unsafeCast(c);
//                self.values.put(p, checkBox1.isSelected());
//            });
//            boxes.add(checkBox);
//            scrollView.addSubview(checkBox);
//            top += 10;
//        }
//        scrollView.setContentSize(new CGSize(0, top));
//    }
//
//    private void startImport(UIControl sender) {
//        sender.setEnabled(false);
//        String fileName = inputTextField.text();
//        try {
//            if (fileName == null || fileName.isEmpty()) {
//                throw new RuntimeException("pls input file name not found");
//            }
//            File root = new File(EnvironmentManager.getRootDirectory(), "model-imports");
//            File inputFile = new File(root, fileName);
//            if (!inputFile.exists()) {
//                throw new RuntimeException(inputFile + " not found");
//            }
//            File outputFile = new File(root, SkinFileUtils.removeExtension(fileName) + ".armour");
//            if (outputFile.exists()) {
//                outputFile.delete();
//            }
//            Skin skin = readSkinFromFile(inputFile);
//            ModLog.debug("{}", skin);
//            SkinFileStreamUtils.saveSkinToFile(outputFile, skin);
//            ModLog.debug("{}", skin);
//            UserNotificationCenter.showToast(outputFile.getName(), " Import Success", null);
//            dismiss();
//        } catch (Exception e) {
//            UserNotificationCenter.showToast(e, "Import Failure", null);
//        }
//        sender.setEnabled(true);
//    }
//
//    public Skin readSkinFromFile(File file) throws IOException {
//        String name = file.getName();
//        Collection<IResource> resources = getResourcesFromFile(file);
//        return readSkinFromReader(BlockBenchReader.from(name, resources));
//    }
//
//    public Skin readSkinFromReader(SkinPackReader reader) throws IOException {
//        // access the bedrock addon pack, and the load the entity models.
//        BedrockModelExporter exporter = new BedrockModelExporter();
//        reader.loadEntityModel(modelReader -> {
//            BedrockModel model = modelReader.readModel();
//            for (BedrockModelGeometry geometry : model.getGeometries()) {
//                BedrockModelTexture texture = modelReader.readTexture(geometry);
//                for (BedrockModelBone bone : geometry.getBones()) {
//                    exporter.add(bone, texture);
//                }
//            }
//            SkinPack pack = modelReader.getPack();
//            if (pack != null) {
//                String name = pack.getName();
//                if (name != null && !name.isEmpty()) {
//                    exporter.add(SkinProperty.ALL_CUSTOM_NAME, name);
//                }
//                String description = pack.getDescription();
//                if (description != null && !description.isEmpty()) {
//                    exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, description);
//                }
//                Collection<String> authors = pack.getAuthors();
//                if (authors != null && !authors.isEmpty()) {
//                    StringBuilder builder = null;
//                    for (String a : authors) {
//                        if (builder == null) {
//                            builder = new StringBuilder(a);
//                        } else {
//                            builder.append(",");
//                            builder.append(a);
//                        }
//                    }
//                    exporter.add(SkinProperty.ALL_AUTHOR_NAME, builder.toString());
//                }
//            }
//            Map<String, BedrockTransform> transforms = modelReader.getTransforms();
//            if (transforms != null) {
//                transforms.forEach((name, transform) -> {
//                    Vector3f translation = transform.getTranslation();
//                    Vector3f rotation = transform.getRotation();
//                    Vector3f scale = transform.getScale();
//                    SkinTransform transform1 = SkinTransform.create(translation, rotation, scale);
//                    if (!transform1.isIdentity()) {
//                        exporter.add(name, transform1);
//                    }
//                });
//            }
//        });
//
//        exporter.add(SkinProperty.ALL_CUSTOM_NAME, textName.text());
//        exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, textDescription.text());
//
//        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
//        exporter.add(SkinProperty.ALL_AUTHOR_NAME, origin.getName());
//        if (origin.getId() != null) {
//            exporter.add(SkinProperty.ALL_AUTHOR_UUID, origin.getId().toString());
//        }
//
//        ISkinType skinType = skinTypeList.selectedSkin();
//        if (skinType == SkinTypes.OUTFIT) {
//            // because the entity origin is at (0, 24, 0).
//            exporter.move(new Vector3f(0, -24, 0));
//        }
//
//        values.forEach(exporter::add);
//        return exporter.export(skinType);
//    }
//}
