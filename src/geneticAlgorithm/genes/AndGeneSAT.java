package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.ProteinSAT;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class AndGeneSAT extends GeneSAT {

    public AndGeneSAT(List<ProteinSAT> inputProteins, ProteinSAT outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public ProteinSAT evaluateFunction(Map<String, ProteinSAT> givenProteins) {
        for (ProteinSAT inputProtein : this.getInputProteins()) {
           if (inputProtein.isPresent()) {
               if (!givenProteins.containsKey(inputProtein.getName())) {
                   return null;
               }
               ProteinSAT given = givenProteins.get(inputProtein.getName());
               if (!given.isPresent()) {
                   return null;
               }
           } else {
               if (givenProteins.containsKey(inputProtein.getName())) {
                   ProteinSAT given = givenProteins.get(inputProtein.getName());
                    if (given.isPresent()) {
                        return null;
                    }
               }
           }
        }
        return this.getOutputProtein();
    }
}
