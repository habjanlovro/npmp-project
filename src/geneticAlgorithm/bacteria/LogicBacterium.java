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
        NOTHING_PRESENT,
        OK_PRESENT,
        NOK_PRESENT,
        BOTH_PRESENT
    }

    private Gene f1;
    private Gene f2;
    private Gene f3;
    private List<Gene> clausesGenes;

    private final Random rng = new Random();
    private double crossoverRate;
    private double mutationRate;
    
    private boolean conjugated;
    private HashMap<String, Protein> solution;

    public Status bacteriaStatus;
    private int generationNum;

    public LogicBacterium(
            BSim bSim,
            Vector3d vector3d,
            Gene f1, Gene f2, Gene f3,
            List<Gene> clausesGenes,
            double crossoverRate,
            double mutationRate,
            int generationNum) {
        super(bSim, vector3d);
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.clausesGenes = clausesGenes;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.generationNum = generationNum;
        this.bacteriaStatus = Status.NOTHING_PRESENT;
        this.conjugated = false;
        this.solution = new HashMap<String, Protein>();
    }

    @Override
    public void action() {
        super.action();

        var clauseProteins = new HashMap<String, Protein>();
        for (Gene clauseGene : this.getClausesGenes()) {
            Protein outputProtein = clauseGene.evaluateFunction(clauseProteins);
            if (outputProtein != null) {
                clauseProteins.put(outputProtein.getName(), outputProtein);
            }
        }
        
        this.setSolution(clauseProteins);

        Protein nok = this.getF1().evaluateFunction(clauseProteins);
        Protein ok = this.getF2().evaluateFunction(clauseProteins);

        this.setStatus(nok, ok);
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
                this.getF1(), this.getF2(), this.getF3(),
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

    private void setStatus(Protein nok, Protein ok) {
        if (nok == null && ok == null) {
            this.bacteriaStatus = Status.NOTHING_PRESENT;
        } else if (nok != null && ok == null) {
            this.bacteriaStatus = Status.NOK_PRESENT;
        } else if (nok == null && ok != null) {
            this.bacteriaStatus = Status.OK_PRESENT;
        } else {
            this.bacteriaStatus = Status.BOTH_PRESENT;
        }
    }

    public Color getColor() {
        switch (this.bacteriaStatus) {
            case NOTHING_PRESENT: return Color.GRAY;
            case OK_PRESENT: return Color.GREEN;
            case NOK_PRESENT: return Color.RED;
            case BOTH_PRESENT: return Color.YELLOW;
            default: return Color.WHITE;
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

    public Gene getF1() {
        return f1;
    }

    public void setF1(Gene f1) {
        this.f1 = f1;
    }

    public Gene getF2() {
        return f2;
    }

    public void setF2(Gene f2) {
        this.f2 = f2;
    }

    public Gene getF3() {
        return f3;
    }

    public void setF3(Gene f3) {
        this.f3 = f3;
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
}
