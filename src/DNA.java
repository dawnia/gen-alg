import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/**
 * Created by dawnia on 8/15/17.
 */

public class DNA {
    static Random rnd = new Random();
    static String target;
    static float mutationRate;
    static int popSize;
    static DNA[] population;
    static float totalFitness;
    static ArrayList<DNA> matingPool = new ArrayList<>();
    static DNA[] nextGen;
    static float nextTotalFitness;

    char[] genes = new char[target.length()];
    float fitness;
    float fitnessNorm;

    /* Construct array of random chars and set fitness. */
    DNA(boolean init) {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = (char) (rnd.nextInt(26) + 97);
        }
        setFitness(true);
    }

    /* Constructor for children. */
    DNA() { }

    /* Calculate fitness: total correct chars/total characters */
    void setFitness(boolean isInit) {
        int matches = 0;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] == target.charAt(i)) {
                matches++;
            }
        }
        fitness = ((float) matches)/target.length();
//        fitness = (float) Math.pow(2, matches);
        if (isInit)    totalFitness += fitness;
        else           nextTotalFitness += fitness;
    }

    /* Make child with random crossover from parent DNA. */
    DNA crossover(DNA partner) {
        DNA child = new DNA();
        for (int i = 0; i < this.genes.length; i++) {
            boolean useA = rnd.nextBoolean();
            if (useA)    child.genes[i] = this.genes[i];
            else         child.genes[i] = partner.genes[i];
        }
        return child;
    }

    /* Mutate child's genes. */
    void mutate() {
        for (int i = 0; i < genes.length; i++) {
            if (rnd.nextFloat() <= mutationRate) {
                genes[i] = (char) (rnd.nextInt(26) + 97);
            }
        }
    }

    String geneString() {
        return new String(genes);
    }

    /// STATIC METHODS ///

    static void initializePopulation() {
        for (int i = 0; i < population.length; i++) {
            population[i] = new DNA(true);
        }
    }

    /* Add each parent N times, N based on fitness */
    static void setMatingPool(boolean isInit) {
//        normalizeFitnesses(isInit);
        matingPool.clear();
        for (DNA d : population) {
            for (int i = 0; i < d.fitness * 100; i++) {   // change to fitnessNorm
                matingPool.add(d);
            }
        }
    }

    static void normalizeFitnesses(boolean isInit) {
        if (isInit) {
            for (DNA d : population) {
                d.fitnessNorm = d.fitness / totalFitness;
            }
        } else {
            for (DNA d : population) {
                d.fitnessNorm = d.fitness / nextTotalFitness;
            }
        }
    }

    static DNA reproduce() {
        int a = 0;
        int b = 0;
        while (a == b) {
            a = rnd.nextInt(matingPool.size());
            b = rnd.nextInt(matingPool.size());
        }
        DNA parentA = matingPool.get(a);
        DNA parentB = matingPool.get(b);
        DNA child = parentA.crossover(parentB);
        child.mutate();
        child.setFitness(false);
        return child;
    }

    static void run() {
        System.out.println("total fitness start: " + totalFitness);
        for (int gen = 0; gen <= 50; gen++) {   // for each generation
            if (gen % 10 == 0) {
                System.out.print("gen " + gen + ": ");
                for (int i = 0; i < 20; i++) {
                    System.out.print(population[i].geneString() + "  ");
                }
                System.out.println();
            }
            for (int i = 0; i < population.length; i++) {
                nextGen[i] = reproduce();
            }
            population = nextGen.clone();
            totalFitness = nextTotalFitness;
            nextGen = new DNA[popSize];
            nextTotalFitness = (float) 0;
            setMatingPool(false); // also try with monte carlo and ordinals method
        }
        System.out.println("total fitness end: " + totalFitness);
    }

    public static void main(String[] args){
        target = "tobeornottobe";
        popSize = 50000;
        population = new DNA[popSize];
        initializePopulation();
        setMatingPool(true); // also try with monte carlo and ordinals method
        mutationRate = (float) 0.02;
        nextGen = new DNA[popSize];
        run();
    }
}


