package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

//public class AdvancedPartPickerDialog extends ConfirmDialog {
//
//    protected final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);
//    protected final PartItemList itemList = buildFileList(0.0F, 0.0F, 200.0F, 20.0F);
//
//    public AdvancedPartPickerDialog() {
//        this.setFrame(new CGRect(0, 0, 250, 200));
//        this.scrollView.setFrame(bounds().insetBy(30, 10, 40, 10));
//        this.scrollView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
//        this.scrollView.setBackgroundColor(UIColor.rgba(0xc0222222));
//        this.addSubview(this.scrollView);
//        this.scrollView.addSubview(this.itemList);
//        this.setupContents();
//    }
//
//    private void setupContents() {
//        ArrayList<PartItem> skins = loadInventoryContents();
//        this.itemList.setEntries(skins);
//        CGSize size = this.itemList.sizeThatFits(scrollView.bounds().size());
//        this.itemList.setFrame(new CGRect(0, 0, size.getWidth(), size.getHeight()));
//        this.itemList.reloadData();
//        this.scrollView.setContentSize(size);
//    }
//
//    public SkinDescriptor getSelectedPart() {
//        SkinDescriptor descriptor = this.itemList.getSelectedItem();
//        if (descriptor != null) {
//            return descriptor;
//        }
//        return SkinDescriptor.EMPTY;
//    }
//
//    public void setSelectedPart(SkinDescriptor selectedItem) {
//        if (Objects.equal(this.itemList.getSelectedItem(), selectedItem)) {
//            this.itemList.setSelectedItem(null);
//        } else {
//            this.itemList.setSelectedItem(selectedItem);
//        }
//    }
//
//    private ArrayList<PartItem> loadInventoryContents() {
//        ArrayList<PartItem> allSkins = new ArrayList<>();
//        Player player = Minecraft.getInstance().player;
//        if (player == null) {
//            return allSkins;
//        }
//        Inventory inventory = player.getInventory();
//        int containerSize = inventory.getContainerSize();
//        for (int i = 0; i < containerSize; ++i) {
//            SkinDescriptor descriptor = SkinDescriptor.of(inventory.getItem(i));
//            if (!descriptor.isEmpty()) {
//                allSkins.add(descriptor);
//            }
//        }
//        return allSkins;
//    }
//
//    private PartItemList buildFileList(float x, float y, float width, float height) {
//        PartItemList fileList = new PartItemList(new CGRect(x, y, width, height));
//        fileList.setItemSize(new CGSize(32, 32));
//        fileList.setBackgroundColor(0);
//        fileList.setShowsName(false);
//        fileList.setItemSelector(this::setSelectedPart);
//        return fileList;
//    }
//}
