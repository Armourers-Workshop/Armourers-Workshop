package moe.plushie.armourers_workshop.builder.menu;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.WorldUtils;
import moe.plushie.armourers_workshop.core.data.UserNotifications;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockEntityMenu;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exception.SkinLoadException;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ArmourerMenu extends AbstractBlockEntityMenu<ArmourerBlockEntity> {

    private final SimpleContainer inventory = new SimpleContainer(4);
    private Group group = null;

    public ArmourerMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.addPlayerSlots(playerInventory, 8, 142, visibleSlotBuilder(this::shouldRenderInventory));
        this.addCustomSlot(inventory, 0, 64, 21);
        this.addCustomSlot(inventory, 1, 147, 21);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, inventory);
    }

    public boolean shouldLoadArmourItem(Player player) {
        var stackInput = inventory.getItem(0);
        var stackOuput = inventory.getItem(1);
        if (stackInput.isEmpty() || !stackOuput.isEmpty()) {
            return false;
        }
        var descriptor = SkinDescriptor.of(stackInput);
        return !descriptor.isEmpty();
    }

    public boolean shouldSaveArmourItem(Player player) {
        var stackInput = inventory.getItem(0);
        var stackOutput = inventory.getItem(1);

        if (player.isCreative()) {
            if (stackInput.isEmpty()) {
                stackInput = new ItemStack(ModItems.SKIN_TEMPLATE.get());
            }
        }

        return !stackInput.isEmpty() && stackOutput.isEmpty();
    }


    /**
     * Get blocks in the world and saved them onto an items NBT data.
     *
     * @param player     The player that pressed the save button.
     * @param customName Custom name for the item.
     */
    public void saveArmourItem(Player player, GameProfile profile, String customName, String tags) {
        if (!shouldSaveArmourItem(player)) {
            return;
        }
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) {
            return;
        }
        try {
            var stackInput = inventory.getItem(0);
            var skinProps = blockEntity.getSkinProperties().copy();

            skinProps.put(SkinProperty.ALL_AUTHOR_NAME, profile.getName());

            // in the offline server the `player.getStringUUID()` is not real player uuid.
            if (profile.getId() != null) {
                skinProps.put(SkinProperty.ALL_AUTHOR_UUID, profile.getId().toString());
            }

            if (customName != null) {
                skinProps.put(SkinProperty.ALL_CUSTOM_NAME, customName);
            }

            var level = blockEntity.getLevel();
            var transform = blockEntity.getTransform();
            var skin = WorldUtils.saveSkinFromWorld(level, transform, skinProps, blockEntity.getSkinType(), blockEntity.getPaintData());

            var identifier = SkinLoader.getInstance().saveSkin("", skin);
            var descriptor = new SkinDescriptor(identifier, skin.getType());
            if (!player.isCreative()) {
                stackInput.shrink(1);
            }

            inventory.setItem(1, descriptor.asItemStack());
        } catch (TranslatableException exception) {
            player.sendSystemMessage(exception.getComponent());
            UserNotifications.sendErrorMessage(exception.getComponent(), player);
        } catch (Exception exception) {
            // we unknown why, pls report this.
            exception.printStackTrace();
        }
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     *
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(Player player) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) {
            return;
        }
        var stackInput = inventory.getItem(0);
        var descriptor = SkinDescriptor.of(stackInput);
        if (!shouldLoadArmourItem(player)) {
            return;
        }
        try {
            var skin = SkinLoader.getInstance().loadSkin(descriptor.getIdentifier());
            if (skin == null) {
                throw SkinLoadException.Type.NOT_FOUND.build("notFound");
            }
            // because descriptor maybe is a wrong skin type.
            if (skin.getType() != blockEntity.getSkinType() || skin.getVersion() >= 20) {
                throw SkinLoadException.Type.NOT_SUPPORTED.build("notSupported");
            }
            if (!skin.getSettings().isEditable()) {
                throw SkinLoadException.Type.NOT_EDITABLE.build("notEditable");
            }
            blockEntity.setSkinProperties(skin.getProperties());
            blockEntity.setPaintData(skin.getPaintData());

            var collector = new CubeChangesCollector(blockEntity.getLevel());
            var transform = blockEntity.getTransform();
            WorldUtils.loadSkinIntoWorld(collector, transform, skin);
            collector.submit(Component.translatable("action.armourers_workshop.block.load"), player);

            inventory.setItem(0, ItemStack.EMPTY);
            inventory.setItem(1, stackInput);

        } catch (TranslatableException exception) {
            player.sendSystemMessage(exception.getComponent());
            UserNotifications.sendErrorMessage(exception.getComponent(), player);
        } catch (Exception exception) {
            // we unknown why, pls report this.
            exception.printStackTrace();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addCustomSlot(Container inventory, int slot, int x, int y) {
        addSlot(new GroupSlot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                if (slot == 0) {
                    // we can put the skin template to save skin in the survival mode.
                    if (itemStack.is(ModItems.SKIN_TEMPLATE.get())) {
                        return true;
                    }
                    return !SkinDescriptor.of(itemStack).isEmpty();
                }
                return false; // this is output slot.
            }
        });
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean shouldRenderInventory() {
        return group == Group.MAIN;
    }

    public enum Group {
        MAIN, SKIN, DISPLAY, BLOCK
    }

    public class GroupSlot extends Slot {

        public GroupSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderInventory();
        }
    }
}
