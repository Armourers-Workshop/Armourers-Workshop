package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkinDocumentType implements IRegistryEntry {

    private static final Map<SkinPartType, Collection<SkinPartType>> LINKED_PARTS = Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_CHEST, Collections.newList(SkinPartTypes.BIPPED_TORSO));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, Collections.newList(SkinPartTypes.BIPPED_LEFT_HAND));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, Collections.newList(SkinPartTypes.BIPPED_RIGHT_HAND));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, Collections.newList(SkinPartTypes.BIPPED_LEFT_LEG));
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, Collections.newList(SkinPartTypes.BIPPED_RIGHT_LEG));
        it.put(SkinPartTypes.BIPPED_RIGHT_WING, Collections.newList(SkinPartTypes.BIPPED_RIGHT_PHALANX));
        it.put(SkinPartTypes.BIPPED_LEFT_WING, Collections.newList(SkinPartTypes.BIPPED_LEFT_PHALANX));
        it.put(SkinPartTypes.ITEM_SHIELD, Collections.newList(SkinPartTypes.ITEM_SHIELD1));
        it.put(SkinPartTypes.ITEM_TRIDENT, Collections.newList(SkinPartTypes.ITEM_TRIDENT1));
        it.put(SkinPartTypes.ITEM_FISHING_ROD, Collections.newList(SkinPartTypes.ITEM_FISHING_ROD1));
    });

    private static final Set<SkinPartType> DISABLED_PARTS = Collections.immutableSet(builder -> {
        builder.add(SkinPartTypes.BLOCK_MULTI);
    });

    private final String category;
    private final SkinType skinType;
    private final ArrayList<SkinPartType> skinPartTypes;

    private OpenResourceKey registryName;

    public SkinDocumentType(String category, SkinType type) {
        this.category = category;
        this.skinType = type;
        this.skinPartTypes = generatePartTypes(type);
    }

    public String name() {
        return registryName.toString();
    }

    public String category() {
        return category;
    }

    public SkinType skinType() {
        return skinType;
    }

    public List<? extends SkinPartType> skinPartTypes() {
        return skinPartTypes;
    }

    public void setRegistryName(OpenResourceKey registryName) {
        this.registryName = registryName;
    }

    @Override
    public OpenResourceKey registryName() {
        return registryName;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", category, "id", registryName);
    }

    private ArrayList<SkinPartType> generatePartTypes(SkinType type) {
        var partTypes = new ArrayList<SkinPartType>();
        for (var partType : type.parts()) {
            // manually disabled parts.
            if (DISABLED_PARTS.contains(partType)) {
                continue;
            }
            var linkedParts = LINKED_PARTS.get(partType);
            partTypes.add(partType);
            if (linkedParts != null) {
                partTypes.addAll(linkedParts);
            }
        }
        return partTypes;
    }
}
