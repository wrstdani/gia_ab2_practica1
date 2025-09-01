package es.urjc.grafo.ABII.Algorithms;

import es.urjc.grafo.ABII.Model.Evaluator;
import es.urjc.grafo.ABII.Model.Instance;
import es.urjc.grafo.ABII.Model.Solution;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Algorithm2 implements Algorithm {
    @Override
    public Solution run(Instance instance) {
        Instant instant = Instant.now();
        Solution bestSolution = null;
        Solution currentSolution = this.runGreedyRandomizedConstruction(instance);
        int k = 0;
        int maxK = 4;
        int maxKVND = 2;

        for (int i = 0; i < 10; i++) {
            Instant beginLoop = Instant.now();
            Solution solution = this.shake(instance, currentSolution, k, maxK);
            solution = this.runVariableNeighborhoodDescent(instance, solution, maxKVND);

            // Neighborhood change
            if (Evaluator.isBetter(solution, bestSolution, instance) && Evaluator.isFeasible(solution, instance)) {
                bestSolution = solution;
                k = 0;
            }
            else {
                k++;
                if (k >= maxK) {
                    currentSolution = this.runGreedyRandomizedConstruction(instance);
                    k = 0;
                }
            }

            Duration loopDuration = Duration.between(beginLoop, Instant.now());
            System.out.println("Iteracion " + (i + 1) + ": " + loopDuration);
            if (loopDuration.toSeconds() > 3) {
                break;
            }
        }
        System.out.println(Duration.between(instant, Instant.now()));
        return bestSolution;
    }

    public String toString() {
        return "GVNS";
    }


    // Greedy randomized construction (to get an initial solution)
    private Solution runGreedyRandomizedConstruction(Instance instance) {
        Solution bestSolution = null;

        int i = 0;
        while (i < 10) {
            Solution solution = new Solution(instance);
            int currentSpecie = new Random().nextInt(instance.getTotalNumberOfItems());
            solution.chooseSpecies(currentSpecie);
            for (int j = 1; j < instance.getNumberOfItemsToPick(); j++) {
                List<Integer> rcl = this.getRCL(instance.getDistancesFrom(currentSpecie), 0.3, instance.getNumberOfItemsToPick(), solution);
                if (rcl.isEmpty()) {
                    List<Integer> remainingSpecies = new ArrayList<>();
                    for (int k = 0; k < instance.getTotalNumberOfItems(); k++) {
                        if (!solution.isChosen(k)) {
                            remainingSpecies.add(k);
                        }
                    }
                    currentSpecie = remainingSpecies.get(new Random().nextInt(remainingSpecies.size()));
                }
                else {
                    currentSpecie = rcl.get(new Random().nextInt(rcl.size()));
                }
                solution.chooseSpecies(currentSpecie);
            }
            if (Evaluator.isBetter(solution, bestSolution, instance) && Evaluator.isFeasible(solution, instance)) {
                bestSolution = solution;
            }
            i++;
        }

        return bestSolution;
    }

    private List<Integer> getRCL(double[] distances, double alpha, int numberOfItemsToPick, Solution currentSolution) {
        List<Integer> rcl = new ArrayList<>();
        double[] rclRange = this.getRCLRange(distances);
        double minDistance = rclRange[0];
        double maxDistance = rclRange[1];
        for (int i = 0; i < distances.length; i++) {
            if (!currentSolution.isChosen(i) && distances[i] >= (maxDistance - (alpha * (maxDistance - minDistance)) *
                    ((double) numberOfItemsToPick / distances.length))) {
                rcl.add(i);
            }
        }
        return rcl;
    }

    private double[] getRCLRange(double[] distances) {
        double[] rclRange = new double[2];
        rclRange[0] = Double.POSITIVE_INFINITY;
        rclRange[1] = Double.NEGATIVE_INFINITY;
        for (double distance : distances) {
            if (distance < rclRange[0]) rclRange[0] = distance;
            if (distance > rclRange[1]) rclRange[1] = distance;
        }
        return rclRange;
    }


    // Variable Neighborhood Descent
    private Solution runVariableNeighborhoodDescent(Instance instance, Solution solution, int maxK) {
        Solution bestSolution = solution;
        int k = 0;

        while (k < maxK) {
            Instant beginLoop = Instant.now();
            boolean improved = false;

            for (int i = 0; i < maxK; i++) {
                Solution bestNeighbor = this.chooseBestNeighbor(instance, bestSolution, k, maxK);
                if (Evaluator.isBetter(bestNeighbor, bestSolution, instance)) {
                    bestSolution = bestNeighbor;
                    improved = true;
                    k = 0;
                }
            }

            if (!improved) {
                k++;
            }
            Duration loopDuration = Duration.between(beginLoop, Instant.now());
            if (loopDuration.toSeconds() > 3) {
                break;
            }
        }
        return bestSolution;
    }

    private Solution chooseBestNeighbor(Instance instance, Solution solution, int k, int maxK) {
        Solution bestSolution = solution;
        List<Solution> neighbors;
        if (k < (maxK / 2)) {
            neighbors = solution.getNeighborsSwapRegion();
        }
        else {
            neighbors = solution.getNeighborsSwapTwo();
        }

        for (Solution neighbor : neighbors) {
            if (Evaluator.isFeasible(neighbor, instance) && Evaluator.isBetter(neighbor, bestSolution, instance)) {
                bestSolution = neighbor;
            }
        }
        return bestSolution;
    }


    // Shake (alter solution)
    private Solution shake(Instance instance, Solution currentSolution, int k, int maxK) {
        Solution solution = currentSolution.clone();
        if (k < (maxK / 2)) {
            solution.swapRegion();
        }
        else {
            solution.swapTwoElements();
        }
        return solution;
    }
}
