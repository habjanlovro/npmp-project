package geneticAlgorithm.bacteria;

import bsim.BSim;
import bsim.particle.BSimBacterium;
import geneticAlgorithm.genes.Gene;
import geneticAlgorithm.proteins.Protein;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LogicBacterium extends BSimBacterium {

    public enum Status {
        NOT_SATISFIES,
        SATISFIES
    }
    private final Random rng = new Random();

    private List<Gene> fitness;
    private List<Gene> clausesGenes;

    private double crossoverRate;
    private double mutationRate;
    
    private boolean conjugated;
    private HashMap<String, Protein> solution;

    public Status bacteriaStatus;
    private int generationNum;

    public LogicBacterium(
            BSim bSim,
            Vector3d vector3d,
            List<Gene> fitness,
            List<Gene> clausesGenes,
            double crossoverRate,
            double mutationRate,
            int generationNum) {
        super(bSim, vector3d);
        this.fitness = fitness;
        this.clausesGenes = clausesGenes;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.generationNum = generationNum;
        this.bacteriaStatus = Status.NOT_SATISFIES;
        this.conjugated = false;
        this.solution = new HashMap<String, Protein>();
    }

    @Override
    public void action() {
        this.setSolution(null);
        this.setConjugated(false);

        super.action();

        var clauseProteins = new HashMap<String, Protein>();
        for (Gene clauseGene : this.getClausesGenes()) {
            Protein outputProtein = clauseGene.evaluateFunction(clauseProteins);
            if (outputProtein != null) {
                clauseProteins.put(outputProtein.getName(), outputProtein);
            }
        }
        
        this.setSolution(clauseProteins);

        boolean satisfies = true;
        for (Gene orClause : this.getFitness()) {
            Protein output = orClause.evaluateFunction(this.getSolution());
            if (output == null || !output.getName().equals("OK")) {
                satisfies = false;
                break;
            }
        }
        this.setStatus(satisfies);
    }

    public void interaction(LogicBacterium bacterium) {
        if (!this.equals(bacterium) && this.outerDistance(bacterium) < 0) {
            if (rng.nextDouble() <= this.getCrossoverRate()) {
                this.conjugate(bacterium);
                this.setConjugated(true);
            }
        }
    }

    public void conjugate(LogicBacterium bacterium) {
        if (this.getClausesGenes().size() > 0) {
            int index = this.rng.nextInt(this.getClausesGenes().size());
            Gene gene = this.clausesGenes.get(index);
            if (!bacterium.getClausesGenes().contains(gene)) {
                bacterium.getClausesGenes().add(gene);
            }
        }
    }

    @Override
    public void replicate() {
        this.setRadiusFromSurfaceArea(this.surfaceArea(this.replicationRadius) / 2.0D);
        LogicBacterium var1 = new LogicBacterium(
                this.sim,
                new Vector3d(this.position),
                this.getFitness(),
                this.mutateGenes(),
                this.getCrossoverRate(), this.getMutationRate(),
                this.getGenerationNum() + 1);
        var1.setRadius(this.radius);
        var1.setSurfaceAreaGrowthRate(this.surfaceAreaGrowthRate);
        var1.setChildList(this.childList);
        this.childList.add(var1);
    }

    public List<Gene> mutateGenes() {
        var genes = new ArrayList<Gene>();
        for (Gene gene : this.getClausesGenes()) {
            if (rng.nextDouble() > this.getMutationRate()) {
                genes.add(gene);
            }
        }
        return genes;
    }

    private void setStatus(boolean satisfies) {
        if (satisfies) {
            this.bacteriaStatus = Status.SATISFIES;
        } else {
            this.bacteriaStatus = Status.NOT_SATISFIES;
        }
    }

    public Color getColor() {
        switch (this.bacteriaStatus) {
            case SATISFIES: return Color.GREEN;
            case NOT_SATISFIES: return Color.RED;
            default: return Color.GRAY;
        }
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
    
    public boolean getConjugated() {
        return conjugated;
    }
    
    public void setConjugated(boolean conjugated) {
        this.conjugated = conjugated;
    }
    
    public HashMap<String, Protein> getSolution() {
        return solution;
    }
    
    public void setSolution(HashMap<String, Protein> solution) {
        this.solution = solution;
    }

    public int getGenerationNum() {
        return generationNum;
    }

    public void setGenerationNum(int generationNum) {
        this.generationNum = generationNum;
    }


    public List<Gene> getFitness() {
        return fitness;
    }

    public void setFitness(List<Gene> fitness) {
        this.fitness = fitness;
    }
}
