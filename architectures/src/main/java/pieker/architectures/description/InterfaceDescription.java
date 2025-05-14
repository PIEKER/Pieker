package pieker.architectures.description;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.ArchitectureModel;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * PIEKER Interface Description base class.
 */
@Getter
@Setter
@NoArgsConstructor
public class InterfaceDescription {

    private List<DescriptionComponent> components;
    private List<DescriptionComponent> mocks;

    /**
     * Validates the description against the architecture model.
     *
     * @param model architecture model to validate against
     * @throws NoSuchElementException if a component in the description does not exist in the model
     */
    public void validate(ArchitectureModel<?> model) {
        for (DescriptionComponent descComp : this.components) {
            model.getComponent(descComp.getName()).orElseThrow();
        }
    }

}
