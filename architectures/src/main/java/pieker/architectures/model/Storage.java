package pieker.architectures.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a storage component in the architecture model.
 */
@Getter
@Setter
public class Storage extends AbstractComponent implements Component {

    private StorageType type;

    public enum StorageType {
        FILE_SYSTEM,
        DATABASE,
        UNSUPPORTED
    }

}
