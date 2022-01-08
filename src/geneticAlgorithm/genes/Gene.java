package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;

public abstract class Gene {

    protected List<Protein> inputProteins;
    protected Protein outputProtein;

    public Gene() {}

    public Gene(List<Protein> inputProteins, Protein outputProtein) {
        this.inputProteins = inputProteins;
        this.outputProtein = outputProtein;
    }

    public abstract Protein evaluateFunction(Vector3d position);

    public List<Protein> getInputProteins() {
        return inputProteins;
    }

    public void setOutputProtein(Protein outputProtein) {
        this.outputProtein = outputProtein;
    }

    public Protein getOutputProtein() {
        return outputProtein;
    }

    public void setInputProteins(List<Protein> inputProteins) {
        this.inputProteins = inputProteins;
    }
}
