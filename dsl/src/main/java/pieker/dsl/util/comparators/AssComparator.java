package pieker.dsl.util.comparators;

import pieker.api.Assertions;

import java.util.Comparator;

public class AssComparator implements Comparator<Assertions> {

    @Override
    public int compare(Assertions a1, Assertions a2) {
        return Integer.compare(a1.getAssertAfter(), a2.getAssertAfter());
    }
}
