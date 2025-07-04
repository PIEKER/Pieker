package pieker.log.eval;

public enum CharacterKey {
    EMPTY("empty"),
    EXISTS("exists"),
    FORALL("forall"),
    SUCCESS("success"),
    FAILURE("failure");

    private final String key;

    CharacterKey(String key) {
        this.key = key;
    }

    @Override
    public String toString(){
        return this.key;
    }
}