package pieker.dsl.model;

public class When extends Behavior {

    public When(Step step) {
        super(step);
    }

    @Override
    public void validateEntryList() {
        this.entryList.forEach(Entry::validateEntry);
    }

    @Override
    public void processEntryList() {
        this.entryList.forEach(Entry::processEntry);
    }
}
