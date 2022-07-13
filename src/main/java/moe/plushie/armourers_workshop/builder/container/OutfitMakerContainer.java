package moe.plushie.armourers_workshop.builder.container;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.builder.tileentity.OutfitMakerTileEntity;
import moe.plushie.armourers_workshop.core.container.AbstractBlockContainer;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.utils.slot.SkinSlot;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("NullableProblems")
public class OutfitMakerContainer extends AbstractBlockContainer {

    private final IInventory inventory;

    public OutfitMakerContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.OUTFIT_MAKER, ModBlocks.OUTFIT_MAKER, access);
        this.inventory = getTileInventory();
        if (this.inventory == null) {
            return;
        }
        this.addPlayerSlots(playerInventory, 8, 158);
        this.addInputSlots(inventory, 0, inventory.getContainerSize() - 1, 36, 58);
        this.addOutputSlot(inventory, inventory.getContainerSize() - 1, 148, 88);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    public boolean shouldCrafting() {
        // required empty of the output slot.
        if (!getOutputStack().isEmpty()) {
            return false;
        }
        // required has item on the input slot.
        for (ItemStack itemStack : getInputStacks()) {
            if (!itemStack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void saveArmourItem(PlayerEntity player, GameProfile profile) {
        // check again before crafting to avoid fake request.
        OutfitMakerTileEntity tileEntity = getTileEntity(OutfitMakerTileEntity.class);
        if (!shouldCrafting() || tileEntity == null) {
            return;
        }
        ArrayList<SkinPart> skinParts = new ArrayList<>();
        SkinProperties skinProperties = new SkinProperties();
        String partIndexs = "";
        SkinPaintData paintData = null;
        int skinIndex = 0;
        for (ItemStack itemStack : getInputStacks()) {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            Skin skin = SkinLoader.getInstance().loadSkin(descriptor.getIdentifier());
            if (skin == null) {
                continue;
            }
            for (int partIndex = 0; partIndex < skin.getPartCount(); partIndex++) {
                SkinPart part = skin.getParts().get(partIndex);
                skinParts.add(part);
            }
            // TODO: IMP
            if (skin.getPaintData() != null) {
                if (paintData == null) {
                    paintData = SkinPaintData.v2();
                }
                for (ISkinPartType partType : skin.getType().getParts()) {
                    if (partType instanceof ISkinPartTypeTextured) {
                        ISkinPartTypeTextured texType = ((ISkinPartTypeTextured) partType);
                        mergePaintPart(texType, paintData, skin.getPaintData());
                    }
                }
            }
            if (partIndexs.isEmpty()) {
                partIndexs = String.valueOf(skinParts.size());
            } else {
                partIndexs += ":" + skinParts.size();
            }
            // TODO: refactor
            for (Map.Entry<String, Object> entry : skin.getProperties().entrySet()) {
                if (entry.getKey().startsWith("wings")) {
                    skinProperties.put(entry.getKey() + skinIndex, entry.getValue());
                } else {
                    skinProperties.put(entry.getKey(), entry.getValue());
                }
            }
            skinIndex++;
        }
        // TODO: support v2 texture
        // because old skin not support v2 texture format,
        // so downgrade v2 to v1 texture format.
        if (paintData != null) {
            SkinPaintData resolvedPaintData = SkinPaintData.v1();
            resolvedPaintData.copyFrom(paintData);
            paintData = resolvedPaintData;
        }
        if (!skinParts.isEmpty()) {
            skinProperties.put(SkinProperty.OUTFIT_PART_INDEXS, partIndexs);
            skinProperties.put(SkinProperty.ALL_AUTHOR_NAME, profile.getName());
            // in the offline server the `player.getStringUUID()` is not real player uuid.
            if (profile.getId() != null) {
                skinProperties.put(SkinProperty.ALL_AUTHOR_UUID, profile.getId().toString());
            }
            skinProperties.put(SkinProperty.ALL_CUSTOM_NAME, tileEntity.getItemName());
            skinProperties.put(SkinProperty.ALL_FLAVOUR_TEXT, tileEntity.getItemFlavour());
            // build
            Skin skin = SkinSerializer.makeSkin(SkinTypes.OUTFIT, skinProperties, paintData, skinParts);
            String identifier = SkinLoader.getInstance().saveSkin("", skin);
            SkinDescriptor descriptor = new SkinDescriptor(identifier, skin.getType());
            setOutputStack(descriptor.asItemStack());
        }
    }

    protected void addPlayerSlots(IInventory inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    protected void addInputSlots(IInventory inventory, int start, int end, int x, int y) {
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

    protected void addOutputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
        });
    }


    protected ItemStack getOutputStack() {
        Slot outputSlot = slots.get(slots.size() - 1);
        return outputSlot.getItem();
    }

    protected void setOutputStack(ItemStack itemStack) {
        Slot outputSlot = slots.get(slots.size() - 1);
        outputSlot.set(itemStack);
    }

    protected Iterable<ItemStack> getInputStacks() {
        return Iterables.transform(Iterables.skip(Iterables.limit(slots, slots.size() - 1), 36), Slot::getItem);
    }

    private void mergePaintPart(ISkinPartTypeTextured texType, SkinPaintData desPaint, SkinPaintData srcPaint) {
        Point pos = texType.getTextureSkinPos();

        int width = (texType.getTextureModelSize().getX() * 2) + (texType.getTextureModelSize().getZ() * 2);
        int height = texType.getTextureModelSize().getY() + texType.getTextureModelSize().getZ();

        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                int x = pos.x + ix;
                int y = pos.y + iy;
                int color = srcPaint.getColor(x, y);
                if (PaintColor.isOpaque(color)) {
                    desPaint.setColor(x, y, color);
                }
            }
        }
    }
}