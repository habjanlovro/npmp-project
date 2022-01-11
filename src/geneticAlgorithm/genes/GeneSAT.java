package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.ProteinSAT;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public abstract class GeneSAT {

    protected List<ProteinSAT> inputProteins;
    protected ProteinSAT outputProtein;

    public GeneSAT() {}

    public GeneSAT(List<ProteinSAT> inputProteins, ProteinSAT outputProtein) {
        this.inputProteins = inputProteins;
        this.outputProtein = outputProtein;
    }

    public abstract ProteinSAT evaluateFunction(Map<String, ProteinSAT> givenProteins);

    public List<ProteinSAT> getInputProteins() {
        return inputProteins;
    }

    public void setOutputProtein(ProteinSAT outputProtein) {
        this.outputProtein = outputProtein;
    }

    public ProteinSAT getOutputProtein() {
        return outputProtein;
    }

    public void setInputProteins(List<ProteinSAT> inputProteins) {
        this.inputProteins = inputProteins;
    }
}
