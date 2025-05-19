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
@Table(name = "assertions")
public class Assertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @ManyToOne
    @JoinColumn(name = "step_id")
    @JsonManagedReference
    private Step step;

    @OneToMany(mappedBy = "assertion")
    @JsonBackReference
    private List<Evaluation> evaluation;
}
