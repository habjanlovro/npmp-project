package geneticAlgorithm;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.BSimUtils;
import bsim.export.BSimLogger;
import geneticAlgorithm.bacteria.LogicBacterium;
import geneticAlgorithm.bacteria.LogicBacterium.Status;
import geneticAlgorithm.genes.AndGene;
import geneticAlgorithm.genes.Gene;
import geneticAlgorithm.genes.OrGene;
import geneticAlgorithm.genes.TrueGene;
import geneticAlgorithm.proteins.Protein;
import processing.core.PGraphics3D;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GeneticAlgorithm {

    private final static Random rng = new Random();

    public static void main(String[] args) {
        var sc = new Scanner(System.in);

        BSim sim = new BSim();
        sim.setDt(0.1);
        sim.setSolid(true, true, true);
        sim.setSimulationTime(5);

        System.out.println("Simple genetic algorithm with BSim.");
        System.out.println("Enter the number of proteins:");
        int numProteins = sc.nextInt();

        var clausesGenes = new ArrayList<TrueGene>();
        var expressedProteins = new ArrayList<Protein>();
        var notExpressedProteins = new ArrayList<Protein>();
        var initialAmounts = new ArrayList<Integer>();

        System.out.println("For each protein: is expressed (1 - YES / 0 - NO) and the initial amount:");
        for (int i = 0; i < numProteins; i++) {
            int isExpressed = sc.nextInt();
            int initialAmount = sc.nextInt();
            if (isExpressed < 0 || isExpressed > 1) {
                System.out.println("Incorrect is expressed! Must be 0 or 1.");
                System.exit(1);
            }
            if (initialAmount < 0) {
                System.out.println("Incorrect initial amount. Must be a positive number!");
                System.exit(1);
            }

            initialAmounts.add(initialAmount);
            var protein = new Protein("p" + i, true);
            var gene = new TrueGene(new ArrayList<>(), protein);
            clausesGenes.add(gene);
            if (isExpressed == 1) {
                expressedProteins.add(protein);
            } else {
                notExpressedProteins.add(protein);
            }
        }

        var nokProtein = new Protein("NOK", false);
        var okProtein = new Protein("OK", true);
        var gfpProtein = new Protein("GFP", true);

        System.out.println("Crossover rate [0.0-1.0]: ");
        double crossoverRate = sc.nextDouble();
        if (crossoverRate < 0 || crossoverRate > 1) {
            System.out.println("Incorrect crossover rate! Must be between 0.0 or 1.0.");
            System.exit(1);
        }

        System.out.println("Mutation rate [0.0-1.0]: ");
        double mutationRate = sc.nextDouble();
        if (mutationRate < 0 || mutationRate > 1) {
            System.out.println("Incorrect mutation rate! Must be between 0.0 or 1.0.");
            System.exit(1);
        }

        var bacteria = new ArrayList<LogicBacterium>();
        for (int geneIndex = 0; geneIndex < clausesGenes.size(); geneIndex++) {
            var clauseGene = new ArrayList<Gene>();
            clauseGene.add(clausesGenes.get(geneIndex));

            for (int i = 0; i < initialAmounts.get(geneIndex); i++) {
                var f1 = new OrGene(notExpressedProteins, nokProtein);
                var f2 = new AndGene(expressedProteins, okProtein);

                var f3InputProteins = new ArrayList<Protein>();
                f3InputProteins.add(nokProtein);
                f3InputProteins.add(okProtein);
                var f3 = new AndGene(f3InputProteins, gfpProtein);
                
                var conjugated = false;
                var solution = new HashMap<String, Protein>();

                var bacterium = new LogicBacterium(
                        sim,
                        getNextBacteriumPos(sim),
                        f1, f2, f3,
                        clauseGene,
                        crossoverRate,
                        mutationRate,
                        conjugated,
                        solution);
                bacterium.setSurfaceAreaGrowthRate();

                bacteria.add(bacterium);
            }
        }

        for (int i = 0; i < numProteins; i++) {
            var f1 = new OrGene(notExpressedProteins, nokProtein);
            var f2 = new AndGene(expressedProteins, okProtein);

            var f3InputProteins = new ArrayList<Protein>();
            f3InputProteins.add(nokProtein);
            f3InputProteins.add(okProtein);
            var f3 = new AndGene(f3InputProteins, gfpProtein);
            
            var conjugated = false;
            var solution = new HashMap<String, Protein>();

            var complement = new LogicBacterium(
                    sim,
                    getNextBacteriumPos(sim),
                    f1, f2, f3,
                    new ArrayList<>(),
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
                for (LogicBacterium bacterium : bacteria) {
                    bacterium.action();
                    bacterium.updatePosition();
                }

                for (LogicBacterium bacterium1 : bacteria) {
                    for (LogicBacterium bacterium2 : bacteria) {
                        bacterium1.interaction(bacterium2);
                    }
                }
            }
        });

        sim.setDrawer(new BSimP3DDrawer(sim, 800,600) {
            @Override
            public void scene(PGraphics3D p3d) {
                for (LogicBacterium bacterium : bacteria) {
                    draw(bacterium, bacterium.getColor());
                }
            }
        });
        
        String resultsDir = BSimUtils.generateDirectoryPath("./results/");
        
        BSimLogger loggerConjugations = new BSimLogger(sim, resultsDir + "conjugations.csv") {
			
			@Override
			public void before() {
				super.before();
				write("time,conjugations"); 
			}
			
			@Override
			public void during() {
				int collisions = 0;
				
				for (LogicBacterium bacterium : bacteria) {
					if (bacterium.getConjugated()) {
						collisions++;
						bacterium.setConjugated(false);
					}
				}
				
				write(sim.getFormattedTime() + "," + collisions);
			}
		};
		sim.addExporter(loggerConjugations);
		
		BSimLogger loggerSolutions = new BSimLogger(sim, resultsDir + "solutions.csv") {
			
			@Override
			public void before() {
				super.before();
				write("time,clauses"); 
			}
			
			@Override
			public void during() {
				for (LogicBacterium bacterium : bacteria) {
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
