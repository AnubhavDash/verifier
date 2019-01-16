package ch.post.it.evoting.verifier.common;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDefinition {
    private int id;
    private int blockId;
    private String name;
    private Category category;
    private EnumSet<TestTrait> testTraits = EnumSet.noneOf( TestTrait.class);
    private Map<Language, String> description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean containsTestTrait( TestTrait trait ) { return this.testTraits.contains(trait); }

    public boolean containsAnyTestTrait( Set<TestTrait> traits ) { return !this.testTraits.isEmpty() && this.testTraits.stream().anyMatch(t -> traits.contains(t)); }

    public void addTestTrait(TestTrait trait) { this.testTraits.add(trait); }

    public void removeTestTrait(TestTrait trait) { this.testTraits.remove(trait); }

    public EnumSet<TestTrait> getTestTraits() { return this.testTraits; }

    public Map<Language, String> getDescription() { return description; }

    public void setDescription(Map<Language, String> description) { this.description = description; }
}
