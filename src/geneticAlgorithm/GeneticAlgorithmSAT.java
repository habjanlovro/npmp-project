package geneticAlgorithm;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.BSimUtils;
import bsim.export.BSimLogger;
import geneticAlgorithm.bacteria.LogicBacteriumSAT;
import geneticAlgorithm.bacteria.LogicBacteriumSAT.Status;
import geneticAlgorithm.genes.AndGeneSAT;
import geneticAlgorithm.genes.GeneSAT;
import geneticAlgorithm.genes.OrGeneSAT;
import geneticAlgorithm.genes.TrueGeneSAT;
import geneticAlgorithm.proteins.ProteinSAT;
import processing.core.PGraphics3D;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GeneticAlgorithmSAT {

    private final static Random rng = new Random();

    public static void main(String[] args) {
        var sc = new Scanner(System.in);

        BSim sim = new BSim();
        sim.setDt(0.1);
        sim.setSolid(true, true, true);
        sim.setSimulationTime(5);

        System.out.println("Simple genetic algorithm with BSim.");
        
        System.out.println("Enter the number of different proteins:");
        int numProteins = Integer.parseInt(sc.nextLine());
        
        var clausesGenes = new ArrayList<TrueGeneSAT>();
        
        for (int i = 0; i < numProteins; i++) {
        	var protein = new ProteinSAT(String.valueOf(i + 1), true);
        	var gene = new TrueGeneSAT(new ArrayList<>(), protein);
        	clausesGenes.add(gene);
        }
        
        System.out.println("Enter the initial amount of each protein:");
        int initialAmount = Integer.parseInt(sc.nextLine());
        
        System.out.println("Enter the number of clauses:");
        int numClauses = Integer.parseInt(sc.nextLine());

        var f1 = new ArrayList<GeneSAT>();
        var f2 = new ArrayList<GeneSAT>();
        
        var nokProtein = new ProteinSAT("NOK", true);
        var okProtein = new ProteinSAT("OK", true);
        var gfpProtein = new ProteinSAT("GFP", true);

        System.out.println("For each clause: proteins and if they are expressed (1 2 - PROTEIN 1 OR PROTEIN 2 / -1 2 - NOT PROTEIN 1 OR PROTEIN 2):");
        for (int i = 0; i < numClauses; i++) {
        	String clause = sc.nextLine();
            
            var clauseOr = new ArrayList<ProteinSAT>();
            var clauseAnd = new ArrayList<ProteinSAT>();
            
            for (String literal : clause.split(" ")) {
            	int lit = Integer.valueOf(literal);
            	if (lit < 0) {
            		clauseOr.add(new ProteinSAT(String.valueOf(lit * (-1)), false));
            		clauseAnd.add(new ProteinSAT(String.valueOf(lit * (-1)), true));
            	} else {
            		clauseOr.add(new ProteinSAT(literal, true));
            		clauseAnd.add(new ProteinSAT(literal, false));
            	}
            }
            f1.add(new AndGeneSAT(clauseAnd, nokProtein));
            f2.add(new OrGeneSAT(clauseOr, okProtein));
        }
        
        System.out.println("Crossover rate [0.0-1.0]: ");
        double crossoverRate = Double.parseDouble(sc.nextLine());
        if (crossoverRate < 0 || crossoverRate > 1) {
            System.out.println("Incorrect crossover rate! Must be between 0.0 or 1.0.");
            System.exit(1);
        }

        System.out.println("Mutation rate [0.0-1.0]: ");
        double mutationRate = Double.parseDouble(sc.nextLine());
        if (mutationRate < 0 || mutationRate > 1) {
            System.out.println("Incorrect mutation rate! Must be between 0.0 or 1.0.");
            System.exit(1);
        }
        
        sc.close();

        var bacteria = new ArrayList<LogicBacteriumSAT>();
        for (int geneIndex = 0; geneIndex < clausesGenes.size(); geneIndex++) {
            var clauseGene = new ArrayList<GeneSAT>();
            clauseGene.add(clausesGenes.get(geneIndex));

            for (int i = 0; i < initialAmount; i++) {
                var f3InputProteins = new ArrayList<ProteinSAT>();
                f3InputProteins.add(new ProteinSAT("NOK", false));
                f3InputProteins.add(okProtein);
                var f3 = new AndGeneSAT(f3InputProteins, gfpProtein);
                // TODO: lahko uporabi stara f1 in f2 ali ne?
                
                var conjugated = false;
                var solution = new HashMap<String, ProteinSAT>();

                var bacterium = new LogicBacteriumSAT(
                        sim,
                        getNextBacteriumPos(sim),
                        f1, f2, f3,
                        clauseGene,
                        numClauses,
                        crossoverRate,
                        mutationRate,
                        conjugated,
                        solution);
                bacterium.setSurfaceAreaGrowthRate();
                bacteria.add(bacterium);
            }
        }

        for (int i = 0; i < numProteins * initialAmount; i++) {
            var f3InputProteins = new ArrayList<ProteinSAT>();
            f3InputProteins.add(nokProtein);
            f3InputProteins.add(okProtein);
            var f3 = new AndGeneSAT(f3InputProteins, gfpProtein);
            // TODO: lahko uporabi stara f1 in f2 ali ne?
            
            var conjugated = false;
            var solution = new HashMap<String, ProteinSAT>();

            var complement = new LogicBacteriumSAT(
                    sim,
                    getNextBacteriumPos(sim),
                    f1, f2, f3,
                    new ArrayList<>(),
                    numClauses,
                    crossoverRate,
                    mutationRate,
                    conjugated,
                    solution);
            complement.setSurfaceAreaGrowthRate();
            bacteria.add(complement);
        }

        sim.setTicker(new BSimTicker() {
            @Override
            public void tick() {
                for (LogicBacteriumSAT bacterium : bacteria) {
                    bacterium.action();
                    bacterium.updatePosition();
                }

                for (LogicBacteriumSAT bacterium1 : bacteria) {
                    for (LogicBacteriumSAT bacterium2 : bacteria) {
                        bacterium1.interaction(bacterium2);
                    }
                }
            }
        });

        sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
            @Override
            public void scene(PGraphics3D p3d) {
                for (LogicBacteriumSAT bacterium : bacteria) {
                    draw(bacterium, bacterium.getColor());
                }
            }
        });
        
        String resultsDir = BSimUtils.generateDirectoryPath("./results/");
        
        BSimLogger loggerConjugations = new BSimLogger(sim, resultsDir + "conjugationsSAT.csv") {
			
			@Override
			public void before() {
				super.before();
				write("time,conjugations"); 
			}
			
			@Override
			public void during() {
				int collisions = 0;
				
				for (LogicBacteriumSAT bacterium : bacteria) {
					if (bacterium.getConjugated()) {
						collisions++;
						bacterium.setConjugated(false);
					}
				}
				
				write(sim.getFormattedTime() + "," + collisions);
			}
		};
		sim.addExporter(loggerConjugations);
		
		BSimLogger loggerSolutions = new BSimLogger(sim, resultsDir + "solutionsSAT.csv") {
			
			@Override
			public void before() {
				super.before();
				write("time,literals(true)"); 
			}
			
			@Override
			public void during() {
				for (LogicBacteriumSAT bacterium : bacteria) {
					if (bacterium.bacteriaStatus == Status.OK_PRESENT) {
						write(sim.getFormattedTime() + "," + bacterium.getSolution().keySet());
						bacterium.setSolution(null);
					}
				}
			}
		};
		sim.addExporter(loggerSolutions);
		
		sim.export();
        sim.preview();
    }

    public static Vector3d getNextBacteriumPos(BSim sim) {
        double newX = rng.nextDouble() * sim.getBound().x;
        double newY = rng.nextDouble() * sim.getBound().y;
        double newZ = rng.nextDouble() * sim.getBound().z;

        return new Vector3d(newX, newY, newZ);
    }

}
