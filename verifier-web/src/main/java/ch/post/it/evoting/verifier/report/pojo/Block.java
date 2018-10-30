package ch.post.it.evoting.verifier.report.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Block {

    private String titre;
    private String description;
    private List<Test> tests;

}
