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
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Step configurations
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Step> steps;

    // Runs of this scenario
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private List<Run> runs;
}
