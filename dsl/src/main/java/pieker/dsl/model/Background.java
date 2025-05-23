package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Background {

    private Feature feature;
    private int line;
    private String description;

    private List<Behavior.Entry> entryList = new ArrayList<>();

    public Background(Feature feature){
        this.feature = feature;
    }

    public void addEntry(String key, String data){
        this.entryList.add(new Behavior.Entry(key, data));
    }
}
