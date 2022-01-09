package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class AndGene extends Gene {

    public AndGene(List<Protein> inputProteins, Protein outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public Protein evaluateFunction(Map<String, Protein> givenProteins) {
        for (Protein inputProtein : this.getInputProteins()) {
           if (inputProtein.isPresent()) {
               if (!givenProteins.containsKey(inputProtein.getName())) {
                   return null;
               }
               Protein given = givenProteins.get(inputProtein.getName());
               if (!given.isPresent()) {
                   return null;
               }
           } else {
               if (givenProteins.containsKey(inputProtein.getName())) {
                   Protein given = givenProteins.get(inputProtein.getName());
                    if (given.isPresent()) {
                        return null;
                    }
               }
           }
        }
        return this.getOutputProtein();
    }
}
