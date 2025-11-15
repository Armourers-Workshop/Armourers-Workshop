package moe.plushie.armourers_workshop.builder.blockentity;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.UserNotifications;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentExporter;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentImporter;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentListeners;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentProvider;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentSynchronizer;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AdvancedBuilderBlockEntity extends UpdatableBlockEntity implements SkinDocumentProvider {

    private AABB renderBoundingBox;

    public final OpenVector3f carmeOffset = new OpenVector3f();
    public final OpenVector3f carmeRot = new OpenVector3f();
    public final OpenVector3f carmeScale = new OpenVector3f(1, 1, 1);

    public OpenVector3f offset = new OpenVector3f(0, 12, 0);

    private final SkinDocument document = new SkinDocument();

    public OpenVector3f getRenderOrigin() {
        var pos = getBlockPos();
        return new OpenVector3f(
                pos.getX() + offset.x() + 0.5f,
                pos.getY() + offset.y() + 0.5f,
                pos.getZ() + offset.z() + 0.5f
        );
    }

    public AdvancedBuilderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.document.addListener(new SkinDocumentListeners.Updater(this));
    }

    @Override
    protected void abi$readAdditionalData(IDataSerializer serializer) {
        document.deserialize(serializer);
    }

    @Override
    protected void abi$writeAdditionalData(IDataSerializer serializer) {
        document.serialize(serializer);
    }

    public void importToNode(String identifier, Skin skin, SkinDocumentNode node) {
        // manual sync this changes, because it changed by server.
        var synchronizer = new SkinDocumentSynchronizer(this, false);
        document.addListener(synchronizer);
        node.setSkin(new SkinDescriptor(identifier, skin.type()));
        document.removeListener(synchronizer);
        if (skin.itemTransforms() != null) {
            importToSettings(skin.itemTransforms(), node);
        }
    }

    private void importToSettings(OpenItemTransforms itemTransforms, SkinDocumentNode node) {
        var newItemTransforms = new OpenItemTransforms();
        if (document.itemTransforms() != null) {
            newItemTransforms.putAll(document.itemTransforms());
        }
        var overrideNames = SkinUtils.getItemOverrides(node.type());
        if (!overrideNames.isEmpty()) {
            overrideNames.forEach(name -> itemTransforms.forEach((type, transform) -> newItemTransforms.put(name + ";" + type, transform)));
        } else {
            newItemTransforms.putAll(itemTransforms);
        }
        // manual sync this changes, because it changed by server.
        var synchronizer = new SkinDocumentSynchronizer(this, false);
        document.addListener(synchronizer);
        document.setItemTransforms(newItemTransforms);
        document.removeListener(synchronizer);
    }

    public void importToDocument(String identifier, Skin skin) {
        BlockUtils.performBatch(() -> {
            var importer = new SkinDocumentImporter(document);
            document.reset();
            document.setItemTransforms(skin.itemTransforms());
            importer.execute(identifier, skin);
        });
    }

    public void exportFromDocument(ServerPlayer player, GameProfile profile) {
        var exporter = new SkinDocumentExporter(document);
        exporter.setItemTransforms(document.itemTransforms());
        EnvironmentExecutor.runOnBackground(() -> () -> {
            try {
                var skin = exporter.execute(player, profile);
                player.server().execute(() -> {
                    var identifier = SkinLoader.getInstance().saveSkin("", skin);
                    var descriptor = new SkinDescriptor(identifier, skin.type());
                    var itemStack = descriptor.asItemStack();
                    player.giveItem(itemStack);
                });
            } catch (TranslatableException exception) {
                UserNotifications.sendErrorMessage(exception.getComponent(), player);
            }
        });
    }

    @Override
    public SkinDocument document() {
        return document;
    }

    @Override
    public AABB getVisibleBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        var s = 16f;
        var origin = getRenderOrigin();
        var rect = new OpenRectangle3f(origin.x() - s / 2, origin.y() - s / 2, origin.z() - s / 2, s, s, s);
        renderBoundingBox = new AABB(rect.minX(), rect.minY(), rect.minZ(), rect.maxX(), rect.maxY(), rect.maxZ());
        return renderBoundingBox;
    }
}
