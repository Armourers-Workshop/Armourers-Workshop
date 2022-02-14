package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import java.util.Iterator;
import java.util.List;

public class SkinWardrobeContainer extends Container {

    public static final ContainerType<SkinWardrobeContainer> TYPE = ContainerTypeBuilder
            .create(SkinWardrobeContainer::new, SkinWardrobe.class)
            .withTitle(TranslateUtils.translate("inventory.armourers_workshop.wardrobe"))
            .withDataCoder(SkinWardrobeContainer::toPacket, SkinWardrobeContainer::fromPacket)
            .build("wardrobe");

    private final SkinWardrobe wardrobe;
    private final ArrayList<Slot> customSlots = new ArrayList<>();
    private Group group = null;

    public SkinWardrobeContainer(int containerId, PlayerInventory inventory, SkinWardrobe wardrobe) {
        super(TYPE, containerId);
        this.wardrobe = wardrobe;

        addPlayerSlots(inventory);

        addSkinSlots(SkinSlotType.HEAD, Group.SKINS, 0, 0);
        addSkinSlots(SkinSlotType.CHEST, Group.SKINS, 0, 1);
        addSkinSlots(SkinSlotType.LEGS, Group.SKINS, 0, 2);
        addSkinSlots(SkinSlotType.FEET, Group.SKINS, 0, 3);
        addSkinSlots(SkinSlotType.WINGS, Group.SKINS, 0, 4);

        addSkinSlots(SkinSlotType.SWORD, Group.SKINS, 0, 5);
        addSkinSlots(SkinSlotType.SHIELD, Group.SKINS, 1, 5);
        addSkinSlots(SkinSlotType.BOW, Group.SKINS, 2, 5);

        addSkinSlots(SkinSlotType.PICKAXE, Group.SKINS, 4, 5);
        addSkinSlots(SkinSlotType.AXE, Group.SKINS, 5, 5);
        addSkinSlots(SkinSlotType.SHOVEL, Group.SKINS, 6, 5);
        addSkinSlots(SkinSlotType.HOE, Group.SKINS, 7, 5);

        addSkinSlots(SkinSlotType.OUTFIT, Group.OUTFITS, 0, 0);
        addSkinSlots(SkinSlotType.DYE, Group.DYES, 0, 0);
    }

    @Nullable
    private static SkinWardrobe fromPacket(PacketBuffer buffer) {
        ClientWorld level = Minecraft.getInstance().level;
        if (level != null) {
            return SkinWardrobe.of(level.getEntity(buffer.readInt()));
        }
        return null;
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

    protected void addSkinSlots(SkinSlotType slotType, Group group, int column, int row) {
        int posX = 83;
        int posY = 27;
        int index = slotType.getIndex();
        int size = wardrobe.getUnlockedSize(slotType);
        IInventory inventory = wardrobe.getInventory();
        for (int i = 0; i < size; ++i) {
            int x = posX + (column + i) * 19;
            int y = posY + row * 19;
            GroupSlot slot = new GroupSlot(slotType, inventory, group, index + i, x, y);
            slot.setBackground(SkinCore.TEX_ITEMS, slotType.getNoItemIcon());
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
        SkinSlotType slotType = SkinSlotType.of(itemStack);
        if (slotType != null) {
            int startIndex = slotType.getIndex() + 36;
            if (!moveItemStackTo(itemStack, startIndex, startIndex + wardrobe.getUnlockedSize(slotType), false)) {
                return ItemStack.EMPTY;
            }
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    public boolean isNeedsPlayerInventory() {
        return group != null && group.shouldRenderPlayerInventory();
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

    public LivingEntity getEntity() {
        return (LivingEntity) wardrobe.getEntity();
    }

    public final class PlayerSlot extends Slot {
        public PlayerSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return isNeedsPlayerInventory();
        }
    }

    public final class GroupSlot extends Slot {

        private final Group group;
        private final SkinSlotType slotType;

        public GroupSlot(SkinSlotType slotType, IInventory inventory, Group group, int index, int x, int y) {
            super(inventory, index, x, y);
            this.group = group;
            this.slotType = slotType;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return slotType.equals(SkinSlotType.of(itemStack)) && super.mayPlace(itemStack);
        }

        @Override
        public boolean isActive() {
            return getGroup() == group;
        }
    }

    public enum Group {
        SKINS(true), OUTFITS(true), DYES(true), COLORS(false);
        final boolean needsPlayerInteract;
        Group(boolean needsPlayerInteract) {
            this.needsPlayerInteract = needsPlayerInteract;
        }
        public boolean shouldRenderPlayerInventory() {
            return needsPlayerInteract;
        }
    }
}
