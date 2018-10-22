package ch.post.it.evoting.verifier.dto;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Test {
    private String id;
    private int testId;
    private int blockId;
    private String name;
    private Category category;
    private Map<Language, String> description;
    private Status status;
    private Map<Language, String> message;

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", testId=" + testId +
                ", blockId=" + blockId +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", description=" + description +
                ", status=" + status +
                ", message=" + message +
                '}';
    }
}