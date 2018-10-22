package ch.post.it.evoting.verifier.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExecutionStatus {
    private Status status;
    private int testActual;
    private int testCount;
}


