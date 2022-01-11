package geneticAlgorithm;

import bsim.BSim;
import bsim.BSimTicker;
import bsim.draw.BSimP3DDrawer;
import bsim.BSimUtils;
import bsim.export.BSimLogger;
import geneticAlgorithm.bacteria.LogicBacterium;
import geneticAlgorithm.bacteria.LogicBacterium.Status;
import geneticAlgorithm.genes.Gene;
import geneticAlgorithm.genes.OrGene;
import geneticAlgorithm.genes.TrueGene;
import geneticAlgorithm.proteins.Protein;
import processing.core.PGraphics3D;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class GeneticAlgorithm {

    private final static Random rng = new Random();

    public static void main(String[] args) {
        System.out.println("Simple genetic algorithm with BSim.");

        var sc = new Scanner(System.in);

        BSim sim = new BSim();
        sim.setDt(0.1);
        sim.setSolid(true, true, true);
        sim.setSimulationTime(1 * 60); // run for 1 minute (if export == true)

        boolean export = false;
        System.out.println("Do you want to export data? [Y / y- yes, export data | N / n - no, run simulation]:");
        String ans = sc.nextLine();
        if (ans.equals("Y") || ans.equals("y")) {
            export = true;
        } else if (ans.equals("N") || ans.equals("n")) {
            export = false;
        }

        System.out.println("Enter the maximum population number:");
        int maxPopulation = Integer.parseInt(sc.nextLine());

        System.out.println("Enter the number of different proteins:");
        int numDiffProteins = Integer.parseInt(sc.nextLine());

        var clausesGenes = new ArrayList<TrueGene>();
        for (int i = 0; i < numDiffProteins; i++) {
            var protein = new Protein(String.valueOf(i + 1), true);
            var gene = new TrueGene(new ArrayList<>(), protein);
            clausesGenes.add(gene);
        }

        System.out.println("Enter the initial amount of each protein:");
        int initialAmount = Integer.parseInt(sc.nextLine());

        System.out.println("Enter the number of clauses:");
        int numClauses = Integer.parseInt(sc.nextLine());


        var okProtein = new Protein("OK", true);
        var equationClauses = new ArrayList<Gene>();
        System.out.println("Write each clause in SAT format [example: x1 x2 -x3 translates to (x1 OR x2 OR (NOT x3))]:");
        for (int i = 0; i < numClauses; i++) {
            String clause = sc.nextLine();
            var orClause = new ArrayList<Protein>();
            for (String literal : clause.split(" ")) {
                int lit = Integer.parseInt(literal);
                if (lit < 0) {
                    orClause.add(new Protein(String.valueOf(lit * (-1)), false));
                } else {
                    orClause.add(new Protein(String.valueOf(lit), true));
                }
            }
            var orGene = new OrGene(orClause, okProtein);
            equationClauses.add(orGene);
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

        var bacteria = new Vector<LogicBacterium>();
        final var children = new Vector<LogicBacterium>();
        for (int geneIndex = 0; geneIndex < clausesGenes.size(); geneIndex++) {
            var clauseGene = new ArrayList<Gene>();
            clauseGene.add(clausesGenes.get(geneIndex));

            for (int i = 0; i < initialAmount; i++) {
                var bacterium = new LogicBacterium(
                        sim,
                        getNextBacteriumPos(sim),
                        equationClauses,
                        clauseGene,
                        crossoverRate,
                        mutationRate,
                        0);
                bacterium.setRadius();
                bacterium.setSurfaceAreaGrowthRate(1);
                bacterium.setChildList(children);

                bacteria.add(bacterium);

                var complement = new LogicBacterium(
                        sim,
                        getNextBacteriumPos(sim),
                        equationClauses,
                        new ArrayList<>(),
                        crossoverRate,
                        mutationRate,
                        0);
                complement.setRadius();
                complement.setSurfaceAreaGrowthRate(1);
                complement.setChildList(children);
                bacteria.add(complement);
            }
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

                if (bacteria.size() < maxPopulation) {
                    bacteria.addAll(children);
                }
                children.clear();
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
				write("time,generation,literals");
			}

			@Override
			public void during() {
				for (LogicBacterium bacterium : bacteria) {
					if (bacterium.bacteriaStatus == Status.SATISFIES) {
						write(sim.getFormattedTime() +
                                "," + bacterium.getGenerationNum() +
                                "," + bacterium.getSolution().keySet());
					}
				}
			}
		};
		sim.addExporter(loggerSolutions);

        if (export) {
            sim.export();
        } else {
            sim.preview();
        }
    }

    public static Vector3d getNextBacteriumPos(BSim sim) {
        double newX = rng.nextDouble() * sim.getBound().x;
        double newY = rng.nextDouble() * sim.getBound().y;
        double newZ = rng.nextDouble() * sim.getBound().z;

        return new Vector3d(newX, newY, newZ);
    }

}
