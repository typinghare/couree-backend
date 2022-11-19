package couree.com.luckycat.glacier;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Registry {
    /**
     * Mapping: key => entry
     */
    private final Map<String, Entry> keyEntryMap = new HashMap<>();

    /**
     * Adds an entry.
     * @param key         key of the entry
     * @param type        type of the entry
     * @param description description of the entry
     * @param updatable   whether the entry can be updated
     */
    public void addEntry(String key, String type, String description, boolean updatable) {
        final Entry entry = new Entry();
        entry.type = type;
        entry.description = description;
        entry.updatable = updatable;

        keyEntryMap.put(key, entry);
    }

    /**
     * Adds an entry record to an entry.
     * @param key         key of the entry
     * @param value       value of the entry record
     * @param sourceClass source class of the entry record comes from
     */
    public void addEntryRecord(String key, String value, Class<?> sourceClass) {
        final Entry entry = this.keyEntryMap.get(key);
        if (!entry.addEntryRecord(value, sourceClass)) {
            throw new RuntimeException(String.format("Registry entry [%s] is not updatable.", key));
        }
    }

    /**
     * Registry entry.
     */
    public static class Entry {
        /**
         * Type of this entry defined in metadata.
         */
        private String type;

        /**
         * Description of this entry defined in metadata.
         */
        private String description;

        /**
         * Whether the value of this entry can be updated. No more than one entry record is allowed to add.
         */
        private boolean updatable;

        /**
         * Entry record list.
         */
        private final List<Record> entryRecordList = new ArrayList<>();

        /**
         * Adds an entry record.
         * @param value       value of this entry
         * @param sourceClass source class this record comes from
         * @return true if successfully added; false if updatable is true and the size of entry record list is one.
         */
        private boolean addEntryRecord(String value, Class<?> sourceClass) {
            if (!updatable && entryRecordList.size() == 1)
                return false;

            final String source = String.format("Class[%s]", sourceClass.getName());
            entryRecordList.add(new Record(value, source));
            return true;
        }

        /**
         * Returns the value of this entry.
         * @return the value of this entry
         */
        public String value() {
            return entryRecordList.get(entryRecordList.size() - 1).value();
        }

        /**
         * Registry entry record.
         */
        private record Record(String value, String source) {
        }
    }
}
