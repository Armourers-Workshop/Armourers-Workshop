package moe.plushie.armourers_workshop.builder.menu;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.other.CubeApplier;
import moe.plushie.armourers_workshop.builder.other.CubeTransform;
import moe.plushie.armourers_workshop.builder.other.WorldUtils;
import moe.plushie.armourers_workshop.core.data.UserNotifications;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockContainerMenu;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exception.SkinLoadException;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ArmourerMenu extends AbstractBlockContainerMenu {

    private final SimpleContainer inventory = new SimpleContainer(4);
    private Group group = null;

    public ArmourerMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IContainerLevelAccess access) {
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
        ItemStack stackInput = inventory.getItem(0);
        ItemStack stackOuput = inventory.getItem(1);
        ArmourerBlockEntity blockEntity = getBlockEntity(ArmourerBlockEntity.class);
        if (stackInput.isEmpty() || !stackOuput.isEmpty() || blockEntity == null) {
            return false;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(stackInput);
        if (descriptor.isEmpty()) {
            return false;
        }
        return descriptor.getType() == blockEntity.getSkinType();
    }

    public boolean shouldSaveArmourItem(Player player) {
        ItemStack stackInput = inventory.getItem(0);
        ItemStack stackOutput = inventory.getItem(1);

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
        ArmourerBlockEntity blockEntity = getBlockEntity(ArmourerBlockEntity.class);
        if (blockEntity == null || blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) {
            return;
        }
        ItemStack stackInput = inventory.getItem(0);

        Skin skin = null;
        SkinProperties skinProps = SkinProperties.create(blockEntity.getSkinProperties());
        skinProps.put(SkinProperty.ALL_AUTHOR_NAME, profile.getName());

        // in the offline server the `player.getStringUUID()` is not real player uuid.
        if (profile.getId() != null) {
            skinProps.put(SkinProperty.ALL_AUTHOR_UUID, profile.getId().toString());
        }

        if (customName != null) {
            skinProps.put(SkinProperty.ALL_CUSTOM_NAME, customName);
        }

        try {
            Level level = blockEntity.getLevel();
            CubeTransform transform = blockEntity.getTransform();
            skin = WorldUtils.saveSkinFromWorld(level, transform, skinProps, blockEntity.getSkinType(), blockEntity.getPaintData());
        } catch (TranslatableException exception) {
            player.sendSystemMessage(exception.getComponent());
            UserNotifications.sendErrorMessage(exception.getComponent(), player);
        } catch (Exception exception) {
            // we unknown why, pls report this.
            exception.printStackTrace();
        }

        if (skin == null) {
            return;
        }

        String identifier = SkinLoader.getInstance().saveSkin("", skin);
        SkinDescriptor descriptor = new SkinDescriptor(identifier, skin.getType());
        if (!player.isCreative()) {
            stackInput.shrink(1);
        }
        inventory.setItem(1, descriptor.asItemStack());
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     *
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(Player player) {
        ArmourerBlockEntity blockEntity = getBlockEntity(ArmourerBlockEntity.class);
        if (blockEntity == null || blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) {
            return;
        }
        ItemStack stackInput = inventory.getItem(0);
        SkinDescriptor descriptor = SkinDescriptor.of(stackInput);
        if (!shouldLoadArmourItem(player)) {
            return;
        }

        try {
            Skin skin = SkinLoader.getInstance().loadSkin(descriptor.getIdentifier());
            if (skin == null) {
                throw SkinLoadException.Type.NOT_FOUND.build("notFound");
            }

            blockEntity.setSkinProperties(skin.getProperties());
            blockEntity.setPaintData(skin.getPaintData());

            CubeApplier applier = new CubeApplier(blockEntity.getLevel());
            CubeTransform transform = blockEntity.getTransform();
            WorldUtils.loadSkinIntoWorld(applier, transform, skin);
            applier.submit(Component.translatable("action.armourers_workshop.block.load"), player);

        } catch (TranslatableException exception) {
            player.sendSystemMessage(exception.getComponent());
            UserNotifications.sendErrorMessage(exception.getComponent(), player);
        } catch (Exception exception) {
            // we unknown why, pls report this.
            exception.printStackTrace();
        }

        inventory.setItem(0, ItemStack.EMPTY);
        inventory.setItem(1, stackInput);
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
                    if (itemStack.getItem() == ModItems.SKIN_TEMPLATE.get()) {
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
