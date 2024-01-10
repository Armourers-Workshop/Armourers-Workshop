package moe.plushie.armourers_workshop.core.skin.document;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkinDocumentType implements IRegistryEntry {

    private static final ImmutableMap<ISkinPartType, Collection<ISkinPartType>> LINKED_PARTS = new ImmutableMap.Builder<ISkinPartType, Collection<ISkinPartType>>()
            .put(SkinPartTypes.BIPPED_CHEST, ObjectUtils.map(SkinPartTypes.BIPPED_TORSO))
            .put(SkinPartTypes.BIPPED_LEFT_ARM, ObjectUtils.map(SkinPartTypes.BIPPED_LEFT_HAND))
            .put(SkinPartTypes.BIPPED_RIGHT_ARM, ObjectUtils.map(SkinPartTypes.BIPPED_RIGHT_HAND))
            .put(SkinPartTypes.BIPPED_LEFT_THIGH, ObjectUtils.map(SkinPartTypes.BIPPED_LEFT_LEG))
            .put(SkinPartTypes.BIPPED_RIGHT_THIGH, ObjectUtils.map(SkinPartTypes.BIPPED_RIGHT_LEG))
            .put(SkinPartTypes.BIPPED_RIGHT_WING, ObjectUtils.map(SkinPartTypes.BIPPED_RIGHT_PHALANX))
            .put(SkinPartTypes.BIPPED_LEFT_WING, ObjectUtils.map(SkinPartTypes.BIPPED_LEFT_PHALANX))
            .put(SkinPartTypes.ITEM_SHIELD, ObjectUtils.map(SkinPartTypes.ITEM_SHIELD1))
            .put(SkinPartTypes.ITEM_TRIDENT, ObjectUtils.map(SkinPartTypes.ITEM_TRIDENT1))
            .put(SkinPartTypes.ITEM_FISHING_ROD, ObjectUtils.map(SkinPartTypes.ITEM_FISHING_ROD1))
            .build();

    private static final ImmutableSet<ISkinPartType> DISABLED_PARTS = new ImmutableSet.Builder<ISkinPartType>()
            .add(SkinPartTypes.BLOCK_MULTI)
            .build();

    private final String name;
    private final ISkinType skinType;
    private final ArrayList<ISkinPartType> skinPartTypes;

    private ResourceLocation registryName;

    public SkinDocumentType(String name, ISkinType type) {
        this.name = name;
        this.skinType = type;
        this.skinPartTypes = generatePartTypes(type);
    }

    public String getName() {
        return name;
    }

    public ISkinType getSkinType() {
        return skinType;
    }

    public List<? extends ISkinPartType> getSkinPartTypes() {
        return skinPartTypes;
    }

    public void setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public boolean usesItemTransforms() {
        return skinType instanceof ISkinEquipmentType || skinType == SkinTypes.ITEM;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "name", name, "id", registryName);
    }

    private ArrayList<ISkinPartType> generatePartTypes(ISkinType type) {
        ArrayList<ISkinPartType> partTypes = new ArrayList<>();
        for (ISkinPartType partType : type.getParts()) {
            // manually disabled parts.
            if (DISABLED_PARTS.contains(partType)) {
                continue;
            }
            Collection<ISkinPartType> linkedParts = LINKED_PARTS.get(partType);
            partTypes.add(partType);
            if (linkedParts != null) {
                partTypes.addAll(linkedParts);
            }
        }
        return partTypes;
    }
}
