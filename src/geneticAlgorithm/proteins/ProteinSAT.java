package geneticAlgorithm.proteins;

public class ProteinSAT {

    private String name;
    private boolean isPresent;

    public ProteinSAT() {}

    public ProteinSAT(String name, boolean isPresent) {
        this.name = name;
        this.isPresent = isPresent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
