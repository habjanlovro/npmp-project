package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;

public class OrGene extends Gene {

    public OrGene(List<Protein> inputProteins, Protein outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public Protein evaluateFunction(Vector3d position) {
        boolean orCondition = false;
        for (Protein inputProtein : this.getInputProteins()) {
            if (inputProtein.satisfiesEquation(position)) {
                orCondition = true;
                break;
            }
        }
        return orCondition ? this.getOutputProtein() : null;
    }
}
