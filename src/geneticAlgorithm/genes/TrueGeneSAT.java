package geneticAlgorithm.genes;

import geneticAlgorithm.proteins.ProteinSAT;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Map;

public class TrueGeneSAT extends GeneSAT {
    public TrueGeneSAT(List<ProteinSAT> inputProteins, ProteinSAT outputProtein) {
        super(inputProteins, outputProtein);
    }

    @Override
    public ProteinSAT evaluateFunction(Map<String, ProteinSAT> givenProteins) {
        return this.getOutputProtein();
    }
}
