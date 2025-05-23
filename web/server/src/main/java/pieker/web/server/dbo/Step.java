package pieker.web.server.dbo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @JsonBackReference
    private Scenario scenario;

    @OneToMany(mappedBy = "step")
    private List<Assertion> assertion;
}
