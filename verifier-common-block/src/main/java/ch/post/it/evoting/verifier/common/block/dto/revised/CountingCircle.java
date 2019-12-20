package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class CountingCircle {
    public final String id;
    public final List<DomainOfInfluence> domainsOfInfluence;

    @JsonCreator
    public CountingCircle(@JsonProperty("id") String id,
                          @JsonProperty("domainOfInfluence") List<DomainOfInfluence> domainsOfInfluence) {
        this.id = id;
        this.domainsOfInfluence = ImmutableList.copyOf(domainsOfInfluence);
    }
}
