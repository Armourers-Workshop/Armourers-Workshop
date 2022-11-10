package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlot;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkinWardrobeMenu extends AbstractContainerMenu {

    private final Player player;
    private final SkinWardrobe wardrobe;
    private final ArrayList<ItemStack> lastSyncSlot = new ArrayList<>();
    private final ArrayList<Slot> customSlots = new ArrayList<>();

    private final int slotsX = 83;
    private final int slotsY = 27;

    private Group group = null;

    public SkinWardrobeMenu(MenuType<?> menuType, int containerId, Inventory inventory, SkinWardrobe wardrobe) {
        super(menuType, containerId);
        this.wardrobe = wardrobe;
        this.player = inventory.player;

        addPlayerSlots(inventory, 59, 168, visibleSlotBuilder(this::shouldRenderInventory));

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

    protected void addEquipmentSlots(Group group, int column, int row) {
        SkinSlotType[] slotTypes = {SkinSlotType.SWORD, SkinSlotType.SHIELD, SkinSlotType.BOW, SkinSlotType.TRIDENT, null, SkinSlotType.PICKAXE, SkinSlotType.AXE, SkinSlotType.SHOVEL, SkinSlotType.HOE};
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
            Container inventory = ((MannequinEntity) wardrobe.getEntity()).getInventory();
            int size = inventory.getContainerSize();
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                int x = slotsX + (column + (size - i - 1)) * 19; // reverse: slot 1 => left, slot 2 => right
                int y = slotsY + row * 19;
                SkinSlot slot = addGroupSlot(inventory, i, x, y, group);
                customSlots.add(slot);
            }
        }
    }

    protected void addSkinSlots(SkinSlotType slotType, Group group, int column, int row) {
        int index = slotType.getIndex();
        int size = wardrobe.getUnlockedSize(slotType);
        Container inventory = wardrobe.getInventory();
        for (int i = 0; i < size; ++i) {
            int x = slotsX + (column + i) * 19;
            int y = slotsY + row * 19;
            SkinSlot slot = addGroupSlot(inventory, index + i, x, y, group, slotType);
            customSlots.add(slot);
        }
    }

    protected SkinSlot addGroupSlot(Container inventory, int index, int x, int y, Group group, SkinSlotType... slotTypes) {
        SkinSlot slot = new SkinSlot(inventory, index, x, y, slotTypes) {
            @Override
            public boolean isActive() {
                return getGroup() == group;
            }
        };
        addSlot(slot);
        return slot;
    }

    public List<Slot> getCustomSlots() {
        return customSlots;
    }

    public void forEachCustomSlots(Consumer<Slot> consumer) {
        for (Slot slot : customSlots) {
            if (slot.isActive()) {
                consumer.accept(slot);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        Entity entity = getEntity();
        if (entity == null || !entity.isAlive() || !wardrobe.isEditable(player)) {
            return false;
        }
        return entity.distanceToSqr(player.getX(), player.getY(), player.getZ()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (slot instanceof SkinSlot) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.set(ItemStack.EMPTY);
            return itemStack.copy();
        }
        SkinSlotType slotType = SkinSlotType.of(itemStack);
        int startIndex = getFreeSlot(slotType);
        if (!moveItemStackTo(itemStack, startIndex, startIndex + 1, false)) {
            return ItemStack.EMPTY;
        }
        slot.set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        // no need listen slot changes for client side.
        if (player.level == null || player.level.isClientSide()) {
            return;
        }
        // in normal, the size is inconsistent this will only happen when the container is first loaded.
        if (lastSyncSlot.size() != slots.size()) {
            lastSyncSlot.ensureCapacity(slots.size());
            slots.forEach(s -> lastSyncSlot.add(s.getItem()));
            return;
        }
        // if slots is ready, we check all slots and fast synchronize changes to all players if changes.
        int changes = 0;
        for (int index = 0; index < slots.size(); ++index) {
            // the first 36 slots we defined as player slots, no synchronize is required.
            if (index < 36) {
                continue;
            }
            ItemStack newItemStack = slots.get(index).getItem();
            if (!lastSyncSlot.get(index).equals(newItemStack)) {
                lastSyncSlot.set(index, newItemStack);
                changes += 1;
            }
        }
        if (changes != 0) {
            ModLog.debug("observer slots has {} changes, sync to players", changes);
            wardrobe.broadcast();
        }
    }

    public boolean shouldRenderInventory() {
        return group != null && group.shouldRenderInventory();
    }

    private int getFreeSlot(SkinSlotType slotType) {
        for (Slot slot : slots) {
            if (slot instanceof SkinSlot && !slot.hasItem()) {
                SkinSlot slot1 = (SkinSlot) slot;
                if (slot1.getSlotTypes().contains(slotType) || slot1.getSlotTypes().isEmpty()) {
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

    public SkinWardrobe getWardrobe() {
        return wardrobe;
    }

    @Nullable
    public Entity getEntity() {
        return wardrobe.getEntity();
    }

    public enum Group {
        SKINS(true, 99), OUTFITS(true, 99), DYES(true, 99), COLORS(false, 99);

        private final boolean exchanges;
        private final int extendedHeight;

        Group(boolean exchanges, int extendedHeight) {
            this.exchanges = exchanges;
            this.extendedHeight = extendedHeight;
        }

        public boolean shouldRenderInventory() {
            return exchanges;
        }

        public int getExtendedHeight() {
            return extendedHeight;
        }
    }
}
