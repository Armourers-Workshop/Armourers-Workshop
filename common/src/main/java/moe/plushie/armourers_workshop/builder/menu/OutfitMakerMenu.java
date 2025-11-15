package moe.plushie.armourers_workshop.builder.menu;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import moe.plushie.armourers_workshop.core.data.UserNotifications;
import moe.plushie.armourers_workshop.core.menu.BlockEntityContainerMenu;
import moe.plushie.armourers_workshop.core.menu.SkinSlot;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.SkinLoadException;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public class OutfitMakerMenu extends BlockEntityContainerMenu<OutfitMakerBlockEntity> {

    private final Container inventory;

    public OutfitMakerMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.inventory = blockEntity.getInventory();
        this.addPlayerSlots(playerInventory, 8, 158);
        this.addInputSlots(inventory, 0, inventory.getContainerSize() - 1, 36, 58);
        this.addOutputSlot(inventory, inventory.getContainerSize() - 1, 148, 88);
    }

    public boolean shouldCrafting() {
        // required empty of the output slot.
        if (!outputStack().isEmpty()) {
            return false;
        }
        // required has item on the input slot.
        for (var itemStack : inputStacks()) {
            if (!itemStack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void saveArmourItem(Player player, GameProfile profile) {
        // check again before crafting to avoid fake request.
        if (!shouldCrafting()) {
            return;
        }
        try {
            saveArmourItemWithProfile(profile, blockEntity);
        } catch (TranslatableException exception) {
            player.sendSystemMessage(exception.getComponent());
            UserNotifications.sendErrorMessage(exception.getComponent(), player);
        } catch (Exception exception) {
            // we unknown why, pls report this.
            exception.printStackTrace();
        }
    }

    private void saveArmourItemWithProfile(GameProfile profile, OutfitMakerBlockEntity blockEntity) throws Exception {
        var skinParts = new ArrayList<SkinPart>();
        var properties = new SkinProperties();
        var partIndexs = "";
        SkinPaintData paintData = null;
        int paintDataVersion = 0;
        int skinIndex = 0;
        for (var itemStack : inputStacks()) {
            var descriptor = SkinDescriptor.of(itemStack);
            var skin = SkinLoader.getInstance().loadSkin(descriptor.identifier());
            if (skin == null) {
                continue;
            }
            if (!skin.isBasicOnly()) {
                throw SkinLoadException.Type.NOT_SUPPORTED_CONTENT.build("notSupportContent");
            }
            if (!skin.settings().isEditable()) {
                throw SkinLoadException.Type.NOT_EDITABLE.build("notEditable");
            }
            for (int partIndex = 0; partIndex < skin.partCount(); partIndex++) {
                var part = skin.parts().get(partIndex);
                skinParts.add(part);
            }
            if (skin.paintData() != null) {
                var oldPaintData = skin.paintData();
                if (paintData == null) {
                    paintData = SkinPaintData.v2(oldPaintData.slim());
                }
                for (var partType : skin.type().parts()) {
                    if (partType instanceof ISkinPartTypeTextured texType) {
                        mergePaintPart(oldPaintData, paintData, texType);
                    }
                }
            }
            if (partIndexs.isEmpty()) {
                partIndexs = String.valueOf(skinParts.size());
            } else {
                partIndexs += ":" + skinParts.size();
            }
            // TODO: refactor
            for (var entry : skin.properties().entrySet()) {
                if (entry.getKey().startsWith("wings")) {
                    properties.put(entry.getKey() + skinIndex, entry.getValue());
                } else {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }
            skinIndex++;
        }
        // TODO: support v2 texture
        // because old skin not support v2 texture format,
        // so downgrade v2 to v1 texture format.
        if (paintData != null) {
            var resolvedPaintData = SkinPaintData.v1();
            resolvedPaintData.copyFrom(paintData);
            paintData = resolvedPaintData;
        }
        if (!skinParts.isEmpty()) {
            properties.put(SkinProperty.OUTFIT_PART_INDEXS, partIndexs);
            properties.put(SkinProperty.ALL_AUTHOR_NAME, profile.name());
            // in the offline server the `player.getStringUUID()` is not real player uuid.
            if (profile.id() != null) {
                properties.put(SkinProperty.ALL_AUTHOR_UUID, profile.id().toString());
            }
            properties.put(SkinProperty.ALL_CUSTOM_NAME, blockEntity.itemName());
            properties.put(SkinProperty.ALL_FLAVOUR_TEXT, blockEntity.itemFlavour());
            // build
            var builder = new Skin.Builder(SkinTypes.OUTFIT);
            builder.properties(properties);
            builder.paintData(paintData);
            builder.parts(skinParts);
            var skin = builder.build();
            var identifier = SkinLoader.getInstance().saveSkin("", skin);
            var descriptor = new SkinDescriptor(identifier, skin.type());
            setOutputStack(descriptor.asItemStack());
        }
    }

    protected void addInputSlots(Container inventory, int start, int end, int x, int y) {
        SkinSlotType[] skinTypes = {
                SkinSlotType.HEAD,
                SkinSlotType.CHEST,
                SkinSlotType.LEGS,
                SkinSlotType.FEET,
                SkinSlotType.WINGS
        };
        for (int i = start; i < end; ++i) {
            int col = i % 5;
            int row = i / 5;
            addSlot(new SkinSlot(inventory, i, x + col * 20, y + row * 20, skinTypes));
        }
    }

    protected void addOutputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new AbstractContainerSlot(inventory, slot, x, y));
    }


    protected ItemStack outputStack() {
        var outputSlot = slots.get(slots.size() - 1);
        return outputSlot.getItem();
    }

    protected void setOutputStack(ItemStack itemStack) {
        var outputSlot = slots.get(slots.size() - 1);
        outputSlot.set(itemStack);
    }

    protected Iterable<ItemStack> inputStacks() {
        return Iterables.transform(Iterables.skip(Iterables.limit(slots, slots.size() - 1), 36), Slot::getItem);
    }

    protected void mergePaintPart(SkinPaintData srcData, SkinPaintData destData, ISkinPartTypeTextured texType) {
        var pos = texType.textureSkinPos();

        var width = (texType.textureModelSize().x() * 2) + (texType.textureModelSize().z() * 2);
        var height = texType.textureModelSize().y() + texType.textureModelSize().z();

        for (var ix = 0; ix < width; ix++) {
            for (var iy = 0; iy < height; iy++) {
                var x = pos.x() + ix;
                var y = pos.y() + iy;
                var color = srcData.getColor(x, y);
                if (SkinPaintColor.isOpaque(color)) {
                    destData.setColor(x, y, color);
                }
            }
        }
    }

    @Override
    protected ItemStack abi$quickMoveStack(Player player, int index) {
        return abi$quickMoveStack(player, index, slots.size() - 1);
    }
}
