import java.util.Arrays;

/**
 * Requirement 7: Separate class for modular design.
 */
public class DataRecord {
    private final String[] fields;

    public DataRecord(String[] fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields;
    }

    public String getField(int index) {
        if (index >= 0 && index < fields.length) {
            return fields[index] == null ? "" : fields[index].trim();
        }
        return "";
    }

    @Override
    public String toString() {
        return Arrays.toString(fields);
    }
}