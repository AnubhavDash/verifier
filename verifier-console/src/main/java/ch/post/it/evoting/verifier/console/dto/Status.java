
package ch.post.it.evoting.verifier.console.dto;

public class Status {

    private String status;
    private Integer testActual;
    private Integer testCount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTestActual() {
        return testActual;
    }

    public void setTestActual(Integer testActual) {
        this.testActual = testActual;
    }

    public Integer getTestCount() {
        return testCount;
    }

    public void setTestCount(Integer testCount) {
        this.testCount = testCount;
    }

}
