package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;

public class AndGene extends Gene {

    public AndGene(List<Protein> inputProteins, Protein outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public Protein evaluateFunction(Vector3d position) {
        boolean andCondition = true;
        for (Protein inputProtein : this.getInputProteins()) {
            if (inputProtein.satisfiesEquation(position)) {
                andCondition = false;
                break;
            }
        }
        return andCondition ? this.getOutputProtein() : null;
    }
}
