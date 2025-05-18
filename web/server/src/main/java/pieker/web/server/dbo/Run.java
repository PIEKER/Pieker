package pieker.web.server.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Run {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stepName;        // Reference name (copied from scenario step)
    private String stepIdentifier;  // Reference identifier (copied from scenario step)

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL)
    private List<Assertion> assertions;
}
