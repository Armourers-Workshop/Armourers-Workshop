package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkinWardrobeContainer extends Container {

    public static final ContainerType<SkinWardrobeContainer> TYPE = ContainerTypeBuilder
            .create(SkinWardrobeContainer::new, SkinWardrobe.class)
            .withTitle(TranslateUtils.translate("inventory.armourers_workshop.wardrobe"))
            .withDataCoder(SkinWardrobeContainer::toPacket, SkinWardrobeContainer::fromPacket)
            .build("wardrobe");

    private final ResourceLocation itemsAtlas = AWCore.resource("textures/atlas/items.png");
    private final SkinWardrobe wardrobe;
    private final ArrayList<Slot> customSlots = new ArrayList<>();

    private SkinWardrobeGroup group = null;

    public SkinWardrobeContainer(int containerId, PlayerInventory inventory, SkinWardrobe wardrobe) {
        super(TYPE, containerId);
        this.wardrobe = wardrobe;

        addPlayerSlots(inventory);

        addSkinSlots(SkinWardrobeSlotType.HEAD, SkinWardrobeGroup.SKINS, 0, 0);
        addSkinSlots(SkinWardrobeSlotType.CHEST, SkinWardrobeGroup.SKINS, 0, 1);
        addSkinSlots(SkinWardrobeSlotType.LEGS, SkinWardrobeGroup.SKINS, 0, 2);
        addSkinSlots(SkinWardrobeSlotType.FEET, SkinWardrobeGroup.SKINS, 0, 3);
        addSkinSlots(SkinWardrobeSlotType.WINGS, SkinWardrobeGroup.SKINS, 0, 4);

        addSkinSlots(SkinWardrobeSlotType.SWORD, SkinWardrobeGroup.SKINS, 0, 5);
        addSkinSlots(SkinWardrobeSlotType.SHIELD, SkinWardrobeGroup.SKINS, 1, 5);
        addSkinSlots(SkinWardrobeSlotType.BOW, SkinWardrobeGroup.SKINS, 2, 5);

        addSkinSlots(SkinWardrobeSlotType.PICKAXE, SkinWardrobeGroup.SKINS, 4, 5);
        addSkinSlots(SkinWardrobeSlotType.AXE, SkinWardrobeGroup.SKINS, 5, 5);
        addSkinSlots(SkinWardrobeSlotType.SHOVEL, SkinWardrobeGroup.SKINS, 6, 5);
        addSkinSlots(SkinWardrobeSlotType.HOE, SkinWardrobeGroup.SKINS, 7, 5);

        addSkinSlots(SkinWardrobeSlotType.OUTFIT, SkinWardrobeGroup.OUTFITS, 0, 0);
        addSkinSlots(SkinWardrobeSlotType.DYE, SkinWardrobeGroup.DYES, 0, 0);
    }

    @Nullable
    private static SkinWardrobe fromPacket(PlayerEntity player, PacketBuffer buffer) {
        int entityId = buffer.readInt();
        return SkinWardrobe.of(player.level.getEntity(entityId));
    }

    private static void toPacket(SkinWardrobe wardrobe, PacketBuffer buffer) {
        Entity entity = wardrobe.getEntity();
        if (entity != null) {
            buffer.writeInt(entity.getId());
        }
    }

    protected void addPlayerSlots(IInventory inventory) {
        int posX = 51;
        int posY = 168;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new PlayerSlot(inventory, col, posX + 8 + col * 18, posY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new PlayerSlot(inventory, col + row * 9 + 9, posX + 8 + col * 18, posY + row * 18));
            }
        }
    }

    protected void addSkinSlots(SkinWardrobeSlotType slotType, SkinWardrobeGroup group, int column, int row) {
        int posX = 83;
        int posY = 27;
        int index = slotType.getIndex();
        int size = wardrobe.getUnlockedSize(slotType);
        IInventory inventory = wardrobe.getInventory();
        for (int i = 0; i < size; ++i) {
            int x = posX + (column + i) * 19;
            int y = posY + row * 19;
            GroupSlot slot = new GroupSlot(slotType, inventory, group, index + i, x, y);
            slot.setBackground(itemsAtlas, slotType.getNoItemIcon());
            addSlot(slot);
            customSlots.add(slot);
        }
    }

    public List<Slot> getCustomSlots() {
        return customSlots;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (slot instanceof GroupSlot) {
            if (!moveItemStackTo(itemStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();
            return itemStack.copy();
        }
        SkinWardrobeSlotType slotType = SkinWardrobeSlotType.of(itemStack);
        if (slotType != null) {
            int startIndex = slotType.getIndex() + 36;
            if (!moveItemStackTo(itemStack, startIndex, startIndex + wardrobe.getUnlockedSize(slotType), false)) {
                return ItemStack.EMPTY;
            }
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    public boolean shouldRenderPlayerInventory() {
        return group != null && group.shouldRenderPlayerInventory();
    }

    public SkinWardrobeGroup getGroup() {
        return this.group;
    }

    public void setGroup(SkinWardrobeGroup group) {
        this.group = group;
    }

    public SkinWardrobe getWardrobe() {
        return wardrobe;
    }

    public Entity getEntity() {
        return wardrobe.getEntity();
    }

    public final class PlayerSlot extends Slot {
        public PlayerSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderPlayerInventory();
        }
    }

    public final class GroupSlot extends Slot {

        private final SkinWardrobeGroup group;
        private final SkinWardrobeSlotType slotType;

        public GroupSlot(SkinWardrobeSlotType slotType, IInventory inventory, SkinWardrobeGroup group, int index, int x, int y) {
            super(inventory, index, x, y);
            this.group = group;
            this.slotType = slotType;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return slotType.equals(SkinWardrobeSlotType.of(itemStack)) && super.mayPlace(itemStack);
        }

        @Override
        public boolean isActive() {
            return getGroup() == group;
        }
    }

}
