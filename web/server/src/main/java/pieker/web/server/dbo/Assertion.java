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
@Table(name = "assertions")
public class Assertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @ElementCollection
    @CollectionTable(name = "assertion_evaluations", joinColumns = @JoinColumn(name = "assertion_id"))
    private List<Evaluation> evaluation;

    @ManyToOne
    @JoinColumn(name = "run_id")
    private Run run;
}
