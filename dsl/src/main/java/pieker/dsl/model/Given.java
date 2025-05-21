package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.dsl.Keyword;

import java.util.List;

@Getter
@Setter
public class Given extends Behavior {

    public Given(Step step){
        super(step);
    }

    @Override
    public void validateEntryList() {
        this.entryList.forEach(Entry::validateEntry);
    }

    @Override
    public void processEntryList(){
        // Creating Link Proxies
        List<Entry> linkEntryList = this.getEntriesByKey(Keyword.LINK);
        if (!linkEntryList.isEmpty()){
            linkEntryList.forEach(Entry::processEntry);
        }

        // Creating Component Proxies
        List<Entry> serviceEntryList = this.getEntriesByKey(Keyword.SERVICE);
        List<Entry> databaseEntryList = this.getEntriesByKey(Keyword.DATABASE);
        if (!(serviceEntryList.isEmpty() && databaseEntryList.isEmpty())){
            serviceEntryList.forEach(Entry::processEntry);
            databaseEntryList.forEach(Entry::processEntry);
        }

        // Creating Supervisor Code
        List<Entry> requestList = this.getEntriesByKey(Keyword.REQUEST);
        List<Entry> sqlList = this.getEntriesByKey(Keyword.SQL);
        requestList.forEach(Entry::processEntry);
        sqlList.forEach(Entry::processEntry);

        // Creating Traffic Container
        List<Entry> passiveEntryList = this.getEntriesByKey(Keyword.PASSIVE);
        passiveEntryList.forEach(Entry::processEntry);
    }
}
