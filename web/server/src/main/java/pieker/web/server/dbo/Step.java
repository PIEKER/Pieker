package pieker.web.server.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Step name (e.g., "increment-counter")
    private String identifier;  // Some unique step logic reference

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;
}
