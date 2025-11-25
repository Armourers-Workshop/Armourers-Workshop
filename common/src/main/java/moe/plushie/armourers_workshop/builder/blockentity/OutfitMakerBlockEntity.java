package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableContainerBlockEntity;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import moe.plushie.armourers_workshop.core.utils.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OutfitMakerBlockEntity extends UpdatableContainerBlockEntity {

    private String itemName = "";
    private String itemFlavour = "";

    private final SimpleContainer container = new SimpleContainer(21);

    public OutfitMakerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    protected void abi$readAdditionalData(IDataSerializer serializer) {
        container.deserialize(serializer);
        itemName = serializer.read(CodingKeys.MAKER_NAME);
        itemFlavour = serializer.read(CodingKeys.MAKER_FLAVOUR);
    }

    protected void abi$writeAdditionalData(IDataSerializer serializer) {
        container.serialize(serializer);
        if (Strings.isNotEmpty(itemName)) {
            serializer.write(CodingKeys.MAKER_NAME, itemName);
        }
        if (Strings.isNotEmpty(itemFlavour)) {
            serializer.write(CodingKeys.MAKER_FLAVOUR, itemFlavour);
        }
    }

    public String itemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    public String itemFlavour() {
        return itemFlavour;
    }

    public void setItemFlavour(String flavour) {
        this.itemFlavour = flavour;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    @Override
    protected SimpleContainer getContainer() {
        return container;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> MAKER_NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, "");
        public static final IDataSerializerKey<String> MAKER_FLAVOUR = IDataSerializerKey.create("Flavour", IDataCodec.STRING, "");
    }
}


