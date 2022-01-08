package geneticAlgorithm.proteins;

import bsim.BSimChemicalField;

import javax.vecmath.Vector3d;

public class Protein {

    private String name;
    private BSimChemicalField chemField;
    private boolean isExpressed;
    private double threshold;

    public Protein() {}

    public Protein(String name, BSimChemicalField chemField, boolean isExpressed, double threshold) {
        this.name = name;
        this.chemField = chemField;
        this.isExpressed = isExpressed;
        this.threshold = threshold;
    }

    public boolean satisfiesEquation(Vector3d position) {
        if (isExpressed()) {
            return getChemField().getConc(position) > getThreshold();
        } else {
            return getChemField().getConc(position) < getThreshold();
        }
    }

    public BSimChemicalField getChemField() {
        return chemField;
    }

    public void setChemField(BSimChemicalField chemField) {
        this.chemField = chemField;
    }

    public boolean isExpressed() {
        return isExpressed;
    }

    public void setExpressed(boolean expressed) {
        isExpressed = expressed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
