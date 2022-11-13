package couree.com.luckycat.core;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class Registry {
    private final Map<String, Entry> keyEntryMap = new HashMap<>();

    void addEntry(String key, String value, Class<?> sourceClass) {
        keyEntryMap.putIfAbsent(key, new Entry());
        final Entry entry = keyEntryMap.get(key);

        entry.addRecord(value, sourceClass);
    }

    String getValue(String key) {
        final Entry entry = keyEntryMap.get(key);
        return entry == null ? null : entry.value;
    }

    List<EntryRecord> getEntryRecordList(String key) {
        final Entry entry = keyEntryMap.get(key);
        return entry == null ? null : entry.entryRecordList;
    }

    public static class Entry {
        private String value;

        private final List<EntryRecord> entryRecordList = new ArrayList<>();

        private void addRecord(String value, Class<?> sourceClass) {
            final String source = String.format("Class[%s]", sourceClass.getName());
            final EntryRecord entryRecord = new EntryRecord(value, source);
            entryRecordList.add(entryRecord);

            this.value = value;
        }
    }

    public record EntryRecord(String value, String source) {
    }
}
