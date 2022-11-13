package couree.com.luckycat.core.dto;

import couree.com.luckycat.core.Registry;
import couree.com.luckycat.core.base.Dto;

import java.util.List;

public class RegistryEntryDto extends Dto {
    private String key;

    private String value;

    private List<Registry.EntryRecord> entryRecordList;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Registry.EntryRecord> getEntryRecordList() {
        return entryRecordList;
    }

    public void setEntryRecordList(List<Registry.EntryRecord> entryRecordList) {
        this.entryRecordList = entryRecordList;
    }
}
