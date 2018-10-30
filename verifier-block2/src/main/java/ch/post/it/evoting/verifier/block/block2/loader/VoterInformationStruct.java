package ch.post.it.evoting.verifier.block.block2.loader;

public class VoterInformationStruct {
    private long count = 0;
    private String eeid = null;

    public long getCount() {
        return count;
    }

    public void increment() {
        count++;
    }

    public String getEeid() {
        return eeid;
    }

    public void setAndCheckUniqueEeid(String eeid) {
        if (this.eeid == null) {
            this.eeid = eeid;
        } else if (!this.eeid.equals(eeid)) {
            throw new RuntimeException("eeid not unique between all voterInformation.csv files");
        }
    }
}
