package moe.plushie.armourers_workshop.builder.container;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.builder.world.WorldUtils;
import moe.plushie.armourers_workshop.core.container.AbstractBlockContainer;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class ArmourerContainer extends AbstractBlockContainer {

    private final IInventory inventory = new Inventory(2);
    private Group group = null;

    public ArmourerContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.ARMOURER, ModBlocks.ARMOURER, access);
        this.addPlayerSlots(playerInventory, 8, 142);
        this.addCustomSlot(inventory, 0, 64, 21);
        this.addCustomSlot(inventory, 1, 147, 21);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, inventory));
    }

    public boolean shouldLoadArmourItem(PlayerEntity player) {
        ItemStack stackInput = inventory.getItem(0);
        ItemStack stackOuput = inventory.getItem(1);
        ArmourerTileEntity tileEntity = getTileEntity(ArmourerTileEntity.class);
        if (stackInput.isEmpty() || !stackOuput.isEmpty() || tileEntity == null) {
            return false;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(stackInput);
        if (descriptor.isEmpty()) {
            return false;
        }
        return descriptor.getType() == tileEntity.getSkinType();
    }

    public boolean shouldSaveArmourItem(PlayerEntity player) {
        ItemStack stackInput = inventory.getItem(0);
        ItemStack stackOutput = inventory.getItem(1);

        if (player.isCreative()) {
            if (stackInput.isEmpty()) {
                stackInput = new ItemStack(ModItems.SKIN_TEMPLATE);
            }
        }

        if (stackInput.isEmpty() || !stackOutput.isEmpty()) {
            return false;
        }

//        if (!(stackInput.getItem() instanceof ISkinHolder)) {
//            return;
//        }

        return true;
    }


    /**
     * Get blocks in the world and saved them onto an items NBT data.
     *
     * @param player The player that pressed the save button.
     * @param customName   Custom name for the item.
     */
    public void saveArmourItem(PlayerEntity player, GameProfile profile, String customName, String tags) {
        if (!shouldSaveArmourItem(player)) {
            return;
        }
        ArmourerTileEntity tileEntity = getTileEntity(ArmourerTileEntity.class);
        if (tileEntity == null || tileEntity.getLevel() == null || tileEntity.getLevel().isClientSide()) {
            return;
        }
        ItemStack stackInput = inventory.getItem(0);

        Skin skin = null;
        SkinProperties skinProps = new SkinProperties(tileEntity.getSkinProperties());
        skinProps.put(SkinProperty.ALL_AUTHOR_NAME, profile.getName());
        // in the offline server the `player.getStringUUID()` is not real player uuid.
        if (profile.getId() != null) {
            skinProps.put(SkinProperty.ALL_AUTHOR_UUID, profile.getId().toString());
        }
        if (customName != null) {
            skinProps.put(SkinProperty.ALL_CUSTOM_NAME, customName);
        }

        try {
            World world = tileEntity.getLevel();
            BlockPos pos = tileEntity.getBlockPos().offset(0, 1, 0);
            Direction direction = Direction.NORTH;
            skin = WorldUtils.saveSkinFromWorld(world, skinProps, tileEntity.getSkinType(), tileEntity.getPaintData(), pos, direction);
        } catch (SkinSaveException e) {
            player.sendMessage(new StringTextComponent(e.getMessage()), player.getUUID());
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
    public void loadArmourItem(PlayerEntity player) {
        ArmourerTileEntity tileEntity = getTileEntity(ArmourerTileEntity.class);
        if (tileEntity == null || tileEntity.getLevel() == null || tileEntity.getLevel().isClientSide()) {
            return;
        }
        ItemStack stackInput = inventory.getItem(0);
        SkinDescriptor descriptor = SkinDescriptor.of(stackInput);
        if (!shouldLoadArmourItem(player)) {
            return;
        }

        Skin skin = SkinLoader.getInstance().loadSkin(descriptor.getIdentifier());
        if (skin == null) {
            return;
        }

        tileEntity.setSkinProperties(skin.getProperties());
        tileEntity.setPaintData(skin.getPaintData());

        try {
            World world = tileEntity.getLevel();
            BlockPos pos = tileEntity.getBlockPos().offset(0, 1, 0);
            Direction direction = Direction.NORTH;
            WorldUtils.loadSkinIntoWorld(world, pos, skin, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        inventory.setItem(0, ItemStack.EMPTY);
        inventory.setItem(1, stackInput);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addCustomSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new GroupSlot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return slot == 0 && !SkinDescriptor.of(itemStack).isEmpty();
            }
        });
    }

    protected void addPlayerSlots(IInventory inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new GroupSlot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new GroupSlot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean shouldRenderPlayerInventory() {
        return group == Group.MAIN;
    }

    public enum Group {
        MAIN, SKIN, DISPLAY, BLOCK
    }

    public class GroupSlot extends Slot {

        public GroupSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderPlayerInventory();
        }
    }
}