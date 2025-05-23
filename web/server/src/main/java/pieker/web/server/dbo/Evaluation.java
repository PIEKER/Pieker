package pieker.web.server.dbo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "run_id")
    @JsonBackReference
    private Run run;

    @ManyToOne
    @JoinColumn(name = "assertion_id")
    @JsonManagedReference
    private Assertion assertion;

    private boolean success;
    private String errorMessage;
}
