package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.ProteinSAT;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class OrGeneSAT extends GeneSAT {

    public OrGeneSAT(List<ProteinSAT> inputProteins, ProteinSAT outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public ProteinSAT evaluateFunction(Map<String, ProteinSAT> givenProteins) {
        for (ProteinSAT inputProtein : this.getInputProteins()) {
            if (inputProtein.isPresent()) {
                if (givenProteins.containsKey(inputProtein.getName())) {
                    ProteinSAT givenProtein = givenProteins.get(inputProtein.getName());
                    if (givenProtein.isPresent()) {
                        return this.getOutputProtein();
                    }
                }
            } else {
                if (!givenProteins.containsKey(inputProtein.getName())) {
                    return this.getOutputProtein();
                }
                ProteinSAT givenProtein = givenProteins.get(inputProtein.getName());
                if (!givenProtein.isPresent()) {
                    return this.getOutputProtein();
                }
            }
        }
        return null;
    }
}
