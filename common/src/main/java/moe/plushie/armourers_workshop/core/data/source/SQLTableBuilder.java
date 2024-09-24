package moe.plushie.armourers_workshop.core.data.source;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

public class SQLTableBuilder {

    private final String name;
    private final LinkedHashMap<String, String> fields = new LinkedHashMap<>();

    public SQLTableBuilder(String name) {
        this.name = name;
    }

    public void add(String name, String type) {
        fields.put(name, type);
    }

    public int execute(Connection connection) throws SQLException {
        // check the table exists.
        var catalog = connection.getCatalog();
        var metaData = connection.getMetaData();
        try (var result = metaData.getTables(catalog, null, name, null)) {
            if (!result.next()) {
                // create a table when not exists.
                var joiner = new StringJoiner(", ", String.format("CREATE TABLE `%s` (", name), ")");
                for (var entry : fields.entrySet()) {
                    joiner.add(String.format("`%s` %s", entry.getKey(), entry.getValue()));
                }
                return execute(connection, joiner.toString());
            }
        }
        // check the table fields exists.
        var instructs = new ArrayList<String>();
        for (var entry : fields.entrySet()) {
            try (var result = metaData.getColumns(catalog, null, name, entry.getKey())) {
                if (!result.next()) {
                    instructs.add(String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s", name, entry.getKey(), entry.getValue()));
                }
            }
        }
        var result = 0;
        for (var instruct : instructs) {
            result += execute(connection, instruct);
        }
        return result;
    }

    private int execute(Connection connection, String sql) throws SQLException {
        try (var stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }
}
