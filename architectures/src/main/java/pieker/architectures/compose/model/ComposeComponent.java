
package pieker.architectures.compose.model;

import pieker.architectures.model.Component;

import java.util.Map;

/**
 * Interface representing a component in a Docker Compose architecture model.
 */
public interface ComposeComponent extends Component {

    Map<String, Object> toMap();

}
