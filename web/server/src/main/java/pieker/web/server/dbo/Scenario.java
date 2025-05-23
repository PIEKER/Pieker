package pieker.web.server.dbo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private List<Run> runs;
}
