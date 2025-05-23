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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assertion")
public class Assertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;
    private String assertType;
    private String assertExpression;

    @ManyToOne
    @JoinColumn(name = "step_id")
    @JsonBackReference
    private Step step;

    @OneToMany(mappedBy = "assertion")
    private List<Evaluation> evaluations;
}
