package moe.plushie.armourers_workshop.core.data.source;

import moe.plushie.armourers_workshop.api.data.IDataSource;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.nbt.CompoundTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Objects;

public abstract class SkinWardrobeDataSource implements IDataSource {

    public CompoundTag load(String id) throws Exception {
        byte[] bytes = query(id);
        if (bytes != null) {
            return SkinFileUtils.readNBT(new ByteArrayInputStream(bytes));
        }
        return null;
    }

    public void save(String id, CompoundTag tag) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(5 * 1024);
        SkinFileUtils.writeNBT(tag, stream);
        byte[] bytes = stream.toByteArray();
        insert(id, bytes);
    }

    protected abstract void insert(String id, byte[] tag) throws Exception;

    protected abstract byte[] query(String id) throws Exception;

    public static class SQL extends SkinWardrobeDataSource {

        private final String name;
        private final Connection connection;

        private final HashMap<String, CompoundTag> lastChanges = new HashMap<>();

        private PreparedStatement insertStatement;
        private PreparedStatement updateStatement;
        private PreparedStatement queryStatement;

        public SQL(String name, Connection connection) {
            this.name = name;
            this.connection = connection;
        }

        @Override
        public void connect() throws Exception {
            ModLog.debug("Connect to wardrobe db: '{}'", name);
            // try to create skin table if needed.
            var builder = new SQLTableBuilder("SkinWardrobe");
            builder.add("id", "VARCHAR(100) NOT NULL PRIMARY KEY"); //  48 + 4 + 48
            builder.add("tag", "LONGBLOB NOT NULL");
            builder.execute(connection);
            // create precompiled statement when create after;
            queryStatement = connection.prepareStatement("SELECT `tag` FROM `SkinWardrobe` where `id` = (?)");
            updateStatement = connection.prepareStatement("UPDATE `SkinWardrobe` SET `tag` = (?) where `id` = (?)");
            insertStatement = connection.prepareStatement("INSERT INTO `SkinWardrobe` (`id`, `tag`) VALUES (?, ?)");
        }

        @Override
        public void disconnect() throws Exception {
            ModLog.debug("Disconnect from wardrobe db: '{}'", name);

            ObjectUtils.safeClose(insertStatement);
            ObjectUtils.safeClose(updateStatement);
            ObjectUtils.safeClose(queryStatement);

            connection.close();
            lastChanges.clear();
        }

        @Override
        public CompoundTag load(String id) throws Exception {
            CompoundTag tag = super.load(id);
            if (tag != null) {
                lastChanges.put(id, tag);
            }
            return tag;
        }

        @Override
        public void save(String id, CompoundTag tag) throws Exception {
            // when no any changes, we will skip insert for reduce database IO.
            if (!Objects.equals(tag, lastChanges.get(id))) {
                super.save(id, tag);
                lastChanges.put(id, tag);
            }
        }

        @Override
        public void insert(String id, byte[] bytes) throws Exception {
            ModLog.debug("Save '{}' skin wardrobe into '{}'", id, name);
            updateStatement.setBytes(1, bytes);
            updateStatement.setString(2, id);
            if (updateStatement.executeUpdate() == 0) {
                insertStatement.setString(1, id);
                insertStatement.setBytes(2, bytes);
                insertStatement.executeUpdate();
            }
        }

        @Override
        public byte[] query(String id) throws Exception {
            queryStatement.setString(1, id);
            try (var result = queryStatement.executeQuery()) {
                if (result.next()) {
                    ModLog.debug("Load '{}' skin wardrobe from '{}'", id, name);
                    return result.getBytes(1);
                }
            }
            return null;
        }
    }
}
