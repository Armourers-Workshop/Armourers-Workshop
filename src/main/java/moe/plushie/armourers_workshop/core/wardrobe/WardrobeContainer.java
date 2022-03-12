package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class WardrobeContainer extends Container {

    public static final ContainerType<WardrobeContainer> TYPE = ContainerTypeBuilder
            .create(WardrobeContainer::new, Wardrobe.class)
            .withTitle(TranslateUtils.title("inventory.armourers_workshop.wardrobe"))
            .withDataProvider(AWDataSerializers::readEntityWardrobe, AWDataSerializers::writeEntityWardrobe)
            .build("wardrobe");

    private final Wardrobe wardrobe;
    private final ArrayList<Slot> customSlots = new ArrayList<>();

    private final int slotsX = 83;
    private final int slotsY = 27;
    private Group group = null;

    public WardrobeContainer(int containerId, PlayerInventory inventory, Wardrobe wardrobe) {
        super(TYPE, containerId);
        this.wardrobe = wardrobe;

        addPlayerSlots(inventory);

        addSkinSlots(SkinSlotType.HEAD, Group.SKINS, 0, 0);
        addSkinSlots(SkinSlotType.CHEST, Group.SKINS, 0, 1);
        addSkinSlots(SkinSlotType.LEGS, Group.SKINS, 0, 2);
        addSkinSlots(SkinSlotType.FEET, Group.SKINS, 0, 3);
        addSkinSlots(SkinSlotType.WINGS, Group.SKINS, 0, 4);

        addEquipmentSlots(Group.SKINS, 0, 5);

        addSkinSlots(SkinSlotType.OUTFIT, Group.OUTFITS, 0, 0);
        addSkinSlots(SkinSlotType.DYE, Group.DYES, 0, 0);

        addMannequinSlots(Group.SKINS, 0, 5);
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

    protected void addEquipmentSlots(Group group, int column, int row) {
        SkinSlotType[] slotTypes = {
                SkinSlotType.SWORD,
                SkinSlotType.SHIELD,
                SkinSlotType.BOW,
                null,
                SkinSlotType.PICKAXE,
                SkinSlotType.AXE,
                SkinSlotType.SHOVEL,
                SkinSlotType.HOE
        };
        boolean hasContents = false;
        for (SkinSlotType slotType : slotTypes) {
            if (slotType != null) {
                int count = wardrobe.getUnlockedSize(slotType);
                if (count > 0) {
                    hasContents = true;
                    addSkinSlots(slotType, group, column, row);
                    column += count;
                }
            } else if (hasContents) {
                column += 1;
            }
        }
    }

    protected void addMannequinSlots(Group group, int column, int row) {
        if (wardrobe.getEntity() instanceof MannequinEntity) {
            IInventory inventory = ((MannequinEntity) wardrobe.getEntity()).getInventory();
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                int x = slotsX + (column + i) * 19;
                int y = slotsY + row * 19;
                GroupSlot slot = new GroupSlot(null, inventory, group, i, x, y);
                addSlot(slot);
                customSlots.add(slot);
            }
        }
    }

    protected void addSkinSlots(SkinSlotType slotType, Group group, int column, int row) {
        int index = slotType.getIndex();
        int size = wardrobe.getUnlockedSize(slotType);
        IInventory inventory = wardrobe.getInventory();
        for (int i = 0; i < size; ++i) {
            int x = slotsX + (column + i) * 19;
            int y = slotsY + row * 19;
            GroupSlot slot = new GroupSlot(slotType, inventory, group, index + i, x, y);
            addSlot(slot);
            customSlots.add(slot);
        }
    }

    public List<Slot> getCustomSlots() {
        return customSlots;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        Entity entity = getEntity();
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return entity.distanceToSqr(player.getX(), player.getY(), player.getZ()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (slot instanceof GroupSlot) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();
            return itemStack.copy();
        }
        SkinSlotType slotType = SkinSlotType.of(itemStack);
        int startIndex = getFreeSlot(slotType);
        if (!moveItemStackTo(itemStack, startIndex, startIndex + 1, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    public boolean shouldRenderPlayerInventory() {
        return group != null && group.shouldRenderPlayerInventory();
    }

    private int getFreeSlot(SkinSlotType slotType) {
        for (Slot slot : slots) {
            if (slot instanceof GroupSlot && !slot.hasItem()) {
                GroupSlot slot1 = (GroupSlot) slot;
                if (slot1.slotType == slotType || slot1.slotType == null) {
                    return slot1.index;
                }
            }
        }
        return 0;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Wardrobe getWardrobe() {
        return wardrobe;
    }

    @Nullable
    public Entity getEntity() {
        return wardrobe.getEntity();
    }

    public enum Group {
        SKINS(true, 99),
        OUTFITS(true, 99),
        DYES(true, 99),
        COLORS(false, 99);

        private final boolean exchanges;
        private final int extendedHeight;

        Group(boolean exchanges, int extendedHeight) {
            this.exchanges = exchanges;
            this.extendedHeight = extendedHeight;
        }

        public boolean shouldRenderPlayerInventory() {
            return exchanges;
        }

        public int getExtendedHeight() {
            return extendedHeight;
        }
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

        private final Group group;
        private final SkinSlotType slotType;

        public GroupSlot(SkinSlotType slotType, IInventory inventory, Group group, int index, int x, int y) {
            super(inventory, index, x, y);
            this.group = group;
            this.slotType = slotType;
            if (slotType != null) {
                this.setBackground(AWCore.resource("textures/atlas/items.png"), AWCore.getSlotIcon(slotType.getName()));
            }
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            if (slotType != null && !slotType.equals(SkinSlotType.of(itemStack))) {
                return false;
            }
            return container.canPlaceItem(index, itemStack);
        }

        @Override
        public boolean isActive() {
            return getGroup() == group;
        }
    }
}
