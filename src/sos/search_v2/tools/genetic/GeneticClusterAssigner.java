package sos.search_v2.tools.genetic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import sos.base.SOSWorldModel;
import sos.base.entities.Human;
import sos.base.util.genetic.ExchangeMutation;
import sos.base.util.genetic.SOSGeneticAlgorithm;
import sos.base.util.genetic.SOSListPopulation;
import sos.base.util.genetic.SOSSelectBest;
import sos.base.util.genetic.SOSTournamentSelection;
import sos.search_v2.tools.cluster.ClusterData;

/**
 * Genetic Assigner is using genetic to assign clusters to agent due to their distance and some other parameters to agents
 * 
 * @author Salim :D
 */
public class GeneticClusterAssigner {
	/**
	 * The pointer to word model
	 */
	private SOSWorldModel model;
	/**
	 * Population size of the genetic
	 */
	public int GENERATIONS = 700;
	/**
	 * Population size of the genetic
	 */
	public int POPULATION_SIZE = 150;
	/**
	 * Max Time in milliseconds
	 */
	public static final int MAX_TIME = 200;
	/**
	 * The ratio that best chromsomes are passed to the next generation
	 */
	public static final double ELITISM_RATE = 0.2;
	/**
	 * Mutation rate shows the percent of mutated chromosomes
	 */
	public static final double MUTATION_RATE = 0.7;
	/**
	 * See mutation rate
	 */
	public static final double CROSSOVER_RATE = 0.0;
	/**
	 * agents that want to assign cluster
	 */
	public static final int TOURNOMENT_ARITY = 2;

	/**
	 * The rate of mutation for exchanging genes
	 */
	public static final float EXCHANGE_MUTATTION_RATE = 0.3f;
	/**
	 * The agents wants assign cluster
	 */
	private List<? extends Human> agents;
	private RandomGenerator random;

	public GeneticClusterAssigner(SOSWorldModel model, List<Human> agents) {
		this.model = model;
		this.agents = agents;
		random = new JDKRandomGenerator();
		random.setSeed(1);
		if (model.sosAgent().getMapInfo().isBigMap() || model.sosAgent().getMapInfo().isMediumMap()) {
			GENERATIONS = (int) (GENERATIONS * 1.5);
			POPULATION_SIZE = POPULATION_SIZE * 2;
		}
	}

	/**
	 * Assigns clusters to fire brigades. Each Cluster may be assigned to more than one fire brigade. Chromose's lenght is equal to number of the agents and shows that the ith agent is assigned to the ith cluaster of the lsit
	 * 
	 * @param zones
	 * @return
	 */
	public List<ClusterData> decide(List<ClusterData> zones) {
		long t1 = System.currentTimeMillis();
		GCAChromosome decision = (GCAChromosome) doGenetic(agents, zones);
		long t2 = System.currentTimeMillis();
		System.out.println("Genetic Time=" + (t2 - t1));
		return checkForErrors(agents, decision);
	}

	/**
	 * only checks errors in the result and returns the input list
	 * 
	 * @param agents
	 * @param decision
	 * @return
	 */
	private List<ClusterData> checkForErrors(List<? extends Human> agents, GCAChromosome chromosome) {
		if (chromosome == null || agents.size() != chromosome.getRepresentation().size())
			throw new Error("There is some error in cluster assigning");
		return chromosome.getRepresentation();
	}

	/**
	 * Instantiates Aima genetic and runs the aglorithm
	 * 
	 * @param agents
	 * @param zones
	 * @return
	 */
	private Chromosome doGenetic(List<? extends Human> agents, List<ClusterData> zones) {
		int length = agents.size();
		SOSGeneticAlgorithm ga = new SOSGeneticAlgorithm(new OnePointCrossover<GCAChromosome>(), CROSSOVER_RATE, new ExchangeMutation<ClusterData>(EXCHANGE_MUTATTION_RATE, random), MUTATION_RATE, new SOSTournamentSelection(TOURNOMENT_ARITY, random), new SOSSelectBest());
		ga.setRandomGenerator(random);
		Population bestPop = ga.evolve(createInitialPopulation(POPULATION_SIZE, length, agents, zones), new GCACondition(ga, (List<Human>) agents, GENERATIONS));

		return bestPop.getFittestChromosome();
	}

	/**
	 * Initial population is generated using a random variable. Each ClusterData may be selected more than once and their possibility is equal.
	 * 
	 * @param size
	 * @param length
	 * @param agents
	 * @param zones
	 * @return
	 */
	private Population createInitialPopulation(int size, int length, List<? extends Human> agents, List<ClusterData> zones) {

		Population pop = new SOSListPopulation(size);
		for (int i = 0; i < size; i++) {
			ClusterData tmp[] = new ClusterData[length];
			ArrayList<ClusterData> newZone = new ArrayList<ClusterData>(zones);
			for (int j = 0; j < length; j++) {
				tmp[j] = getNextRandomCluster(newZone, random);
			}
			pop.addChromosome(new GCAChromosome(tmp, agents));
		}
		return pop;
	}

	/**
	 * Returns a random task as the gene of the chromosome.
	 * 
	 * @param validTasks
	 *            list of task to be handled by agents
	 * @param randomGenerator
	 * @param totalPriority
	 *            is the sum of pririty of all tasks to be used in Rollete wheel
	 * @param noTaskPriority
	 *            is the possibility to have a no task assigned to an agent
	 * @return
	 */
	private ClusterData getNextRandomCluster(List<ClusterData> validTasks, RandomGenerator randomGenerator) {
		int rnd = Math.abs(randomGenerator.nextInt() % validTasks.size());
		return validTasks.remove(rnd);
	}

	public static void main(String[] args) {

	}

}
