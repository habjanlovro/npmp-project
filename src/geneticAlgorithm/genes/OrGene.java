package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class OrGene extends Gene {

    public OrGene(List<Protein> inputProteins, Protein outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public Protein evaluateFunction(Map<String, Protein> givenProteins) {
        for (Protein inputProtein : this.getInputProteins()) {
            if (inputProtein.isPresent()) {
                if (givenProteins.containsKey(inputProtein.getName())) {
                    Protein givenProtein = givenProteins.get(inputProtein.getName());
                    if (givenProtein.isPresent()) {
                        return this.getOutputProtein();
                    }
                }
            } else {
                if (!givenProteins.containsKey(inputProtein.getName())) {
                    return this.getOutputProtein();
                }
                Protein givenProtein = givenProteins.get(inputProtein.getName());
                if (!givenProtein.isPresent()) {
                    return this.getOutputProtein();
                }
            }
        }
        return null;
    }
}
