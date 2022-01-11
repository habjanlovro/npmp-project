package geneticAlgorithm.bacteria;

import bsim.BSim;
import bsim.particle.BSimBacterium;
import geneticAlgorithm.genes.GeneSAT;
import geneticAlgorithm.proteins.ProteinSAT;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LogicBacteriumSAT extends BSimBacterium {

    public enum Status {
        NOTHING_PRESENT,
        OK_PRESENT,
        NOK_PRESENT,
        BOTH_PRESENT
    }

    private List<GeneSAT> f1;
    private List<GeneSAT> f2;
    private GeneSAT f3;
    private List<GeneSAT> clausesGenes;
    
    private Integer numClauses;

    private final Random rng = new Random();
    private double crossoverRate;
    private double mutationRate;
    
    private boolean conjugated;
    private HashMap<String, ProteinSAT> solution;

    public Status bacteriaStatus;

    public LogicBacteriumSAT(
            BSim bSim,
            Vector3d vector3d,
            List<GeneSAT> f1, List<GeneSAT> f2, GeneSAT f3,
            List<GeneSAT> clausesGenes,
            int numClauses,
            double crossoverRate,
            double mutationRate,
            boolean conjugated,
            HashMap<String, ProteinSAT> solution) {
        super(bSim, vector3d);
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.clausesGenes = clausesGenes;
        this.numClauses = numClauses;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.conjugated = conjugated;
        this.solution = solution;
        this.bacteriaStatus = Status.NOTHING_PRESENT;
    }

    @Override
    public void action() {
        super.action();

        var clauseProteins = new HashMap<String, ProteinSAT>();
        for (GeneSAT clauseGene : this.getClausesGenes()) {
            ProteinSAT outputProtein = clauseGene.evaluateFunction(clauseProteins);
            if (outputProtein != null) {
                clauseProteins.put(outputProtein.getName(), outputProtein);
            }
        }
        
        this.setSolution(clauseProteins);
        
        boolean containsNok = false;
        for (GeneSAT f1 : this.getF1()) {
        	if (f1.evaluateFunction(clauseProteins) != null) {
        		containsNok = true;
        	}
        }
        
        ProteinSAT nok = new ProteinSAT("NOK", containsNok);
        
        boolean containsNull = false;
        for (GeneSAT f2 : this.getF2()) {
        	if (f2.evaluateFunction(clauseProteins) == null) {
        		containsNull = true;
        	}
        }
        
        ProteinSAT ok = new ProteinSAT("OK", !containsNull);

        this.setStatus(nok, ok);
    }

    public void interaction(LogicBacteriumSAT bacterium) {
        if (!this.equals(bacterium) && this.outerDistance(bacterium) < 0) {
            if (rng.nextDouble() <= this.getCrossoverRate()) {
                this.conjugate(bacterium);
                this.setConjugated(true);
            }
        }
    }

    public void conjugate(LogicBacteriumSAT bacterium) {
        if (this.getClausesGenes().size() > 0) {
            int index = this.rng.nextInt(this.getClausesGenes().size());
            GeneSAT gene = this.clausesGenes.get(index);
            if (!bacterium.getClausesGenes().contains(gene)) {
                bacterium.getClausesGenes().add(gene);
            }
        }
    }

    private void setStatus(ProteinSAT nok, ProteinSAT ok) {
        if (!nok.isPresent() && !ok.isPresent()) {
            this.bacteriaStatus = Status.NOTHING_PRESENT;
        } else if (nok.isPresent() && !ok.isPresent()) {
            this.bacteriaStatus = Status.NOK_PRESENT;
        } else if (!nok.isPresent() && ok.isPresent()) {
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

    public List<GeneSAT> getClausesGenes() {
        return clausesGenes;
    }

    public void setClausesGenes(List<GeneSAT> clausesGenes) {
        this.clausesGenes = clausesGenes;
    }
    
    public int getNumClauses() {
        return numClauses;
    }

    public void setNumClauses(int numClauses) {
        this.numClauses = numClauses;
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

    public List<GeneSAT> getF1() {
        return f1;
    }

    public void setF1(List<GeneSAT> f1) {
        this.f1 = f1;
    }

    public List<GeneSAT> getF2() {
        return f2;
    }

    public void setF2(List<GeneSAT> f2) {
        this.f2 = f2;
    }

    public GeneSAT getF3() {
        return f3;
    }

    public void setF3(GeneSAT f3) {
        this.f3 = f3;
    }
    
    public boolean getConjugated() {
        return conjugated;
    }
    
    public void setConjugated(boolean conjugated) {
        this.conjugated = conjugated;
    }
    
    public HashMap<String, ProteinSAT> getSolution() {
        return solution;
    }
    
    public void setSolution(HashMap<String, ProteinSAT> solution) {
        this.solution = solution;
    }
}
