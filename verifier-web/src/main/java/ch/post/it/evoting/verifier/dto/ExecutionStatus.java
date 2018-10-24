package ch.post.it.evoting.verifier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExecutionStatus {
    private Status status;
    private int testActual;
    private int testCount;
}


