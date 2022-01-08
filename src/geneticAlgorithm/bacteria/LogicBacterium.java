package geneticAlgorithm.bacteria;

import bsim.BSim;
import bsim.particle.BSimBacterium;
import geneticAlgorithm.genes.Gene;
import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.util.List;
import java.util.Random;

public class LogicBacterium extends BSimBacterium {

    private List<Gene> fitnessFunctionGenes;
    private List<Gene> clausesGenes;

    private final Random rng = new Random();
    private double crossoverRate;
    private double mutationRate;

    private double productionRate;

    public LogicBacterium(
            BSim bSim,
            Vector3d vector3d,
            List<Gene> fitnessFunctionGenes,
            List<Gene> clausesGenes,
            double crossoverRate,
            double mutationRate, double productionRate) {
        super(bSim, vector3d);
        this.fitnessFunctionGenes = fitnessFunctionGenes;
        this.clausesGenes = clausesGenes;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.productionRate = productionRate;
    }

    @Override
    public void action() {
        super.action();

        for (Gene fitnessGene : this.getFitnessFunctionGenes()) {
            Protein fitnessProtein = fitnessGene.evaluateFunction(this.getPosition());
            if (fitnessProtein != null) {
                fitnessProtein.getChemField().addQuantity(
                        this.getPosition(),
                        this.sim.getDt() * this.getProductionRate());
            }
        }
        for (Gene clauseGene : this.getClausesGenes()) {
            Protein clauseProtein = clauseGene.evaluateFunction(this.getPosition());
            if (clauseProtein != null) {
                clauseProtein.getChemField().addQuantity(
                        this.getPosition(),
                        this.sim.getDt() * this.getProductionRate());
            }
        }
    }

    public void interaction(LogicBacterium bacterium) {
        if (this.outerDistance(bacterium) < 0) {
            if (rng.nextDouble() <= this.getCrossoverRate()) {
                this.conjugate(bacterium);
            }
        }
    }

    public void conjugate(LogicBacterium bacterium) {
        int index = this.rng.nextInt(this.getClausesGenes().size());
        Gene gene = this.clausesGenes.get(index);
        bacterium.getClausesGenes().add(gene);
    }

    public List<Gene> getFitnessFunctionGenes() {
        return fitnessFunctionGenes;
    }

    public void setFitnessFunctionGenes(List<Gene> fitnessFunctionGenes) {
        this.fitnessFunctionGenes = fitnessFunctionGenes;
    }

    public List<Gene> getClausesGenes() {
        return clausesGenes;
    }

    public void setClausesGenes(List<Gene> clausesGenes) {
        this.clausesGenes = clausesGenes;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(double productionRate) {
        this.productionRate = productionRate;
    }
}
