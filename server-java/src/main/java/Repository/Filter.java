package Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Filter {
    private final List<String> conditions = new ArrayList<>();
    private final List<Object> parameters = new ArrayList<>();

    public void addFilter(String column, Object value) {
        if (column != null && value != null) {
            conditions.add(column + " = ?");
            parameters.add(value);
        }
    }

    public void addLikeFilter(String column, String value) {
        if (column != null && value != null && !value.isBlank()) {
            conditions.add(column + " LIKE ?");
            parameters.add("%" + value + "%");
        }
    }

    public void addRangeFilter(String column, Object start, Object end) {
        if (column != null && start != null && end != null) {
            conditions.add(column + " BETWEEN ? AND ?");
            parameters.add(start);
            parameters.add(end);
        }
    }

    public String buildWhere() {
        if (conditions.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    public void applyParameters(PreparedStatement stmt) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            stmt.setObject(i + 1, parameters.get(i));
        }
    }

    public int size() {
        return conditions.size();
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }
}