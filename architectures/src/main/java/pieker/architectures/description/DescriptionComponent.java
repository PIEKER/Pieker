package pieker.architectures.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Pieker Interface Description component class. Each component resembles a component in the architecture model.
 */
@Getter
@Setter
@NoArgsConstructor
public class DescriptionComponent {

    private String name;
    private DescriptionInterface provides;
    private List<DescriptionLink> dependencies;

}
