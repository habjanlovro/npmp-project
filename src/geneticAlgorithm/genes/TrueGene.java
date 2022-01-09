package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class TrueGene extends Gene {
    public TrueGene(List<Protein> inputProteins, Protein outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public Protein evaluateFunction(Map<String, Protein> givenProteins) {
        return this.getOutputProtein();
    }
}
