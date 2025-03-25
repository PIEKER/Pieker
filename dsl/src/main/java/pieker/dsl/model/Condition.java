package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.dsl.Keyword;

import java.util.ArrayList;
import java.util.List;

import static pieker.dsl.util.Util.getArgumentsFromString;

@Getter
@Setter
public abstract class Condition {

    public abstract void processEntryList();
    public abstract void validateEntryList();

    private final Step step;
    private int line;
    private String description;

    protected List<Entry> entryList = new ArrayList<>();

    protected Condition(Step step){
        this.step = step;
    }

    public void addEntry(String key, String data){
        this.entryList.add(new Entry(key.trim(), data));
    }

    public List<Entry> getEntriesByKey(Keyword key){
        return this.entryList.stream().filter(entry -> entry.key == key).toList();
    }

    public List<Entry> getEntriesByExcludeKey(Keyword key){
        return this.entryList.stream().filter(entry -> entry.key != key).toList();
    }

    @Setter
    @Getter
    public static class Entry {
        private Keyword key;
        private String data;
        private String[] arguments = new String[0];

        public Entry(String key, String data){
            this.key = Keyword.valueOf(key.trim().substring(1).toUpperCase());
            this.data = data;
        }

        /**
         * Performs a strategy based on the provided keyword.
         */
        public void processEntry(){
            this.key.process(this.arguments);
        }

        /**
         * Validates an entry and sets an argument array for further processing
         */
        public void validateEntry(){
            this.arguments = getArgumentsFromString(this.data);
            this.key.validate(this.arguments);
        }
    }
}
