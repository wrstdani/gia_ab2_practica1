package es.urjc.grafo.ABII.Algorithms;

import es.urjc.grafo.ABII.Model.Instance;
import es.urjc.grafo.ABII.Model.Solution;
import es.urjc.grafo.ABII.Model.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.Instant;
import java.time.Duration;

public class Algorithm1 implements Algorithm {
    @Override
    public Solution run(Instance instance) {
        Instant instant = Instant.now();
        Solution bestSolution = null;
        for (int i = 0; i < 10; i++) {
            Instant beginLoop = Instant.now();
            Solution solution = this.runGreedyRandomizedConstruction(instance);
            solution = this.runLocalSearchBI(instance, solution);
            if (Evaluator.isBetter(solution, bestSolution, instance) && Evaluator.isFeasible(solution, instance)) {
                bestSolution = solution;
            }
            Duration loopDuration = Duration.between(beginLoop, Instant.now());
            System.out.println(loopDuration);
            if (loopDuration.toSeconds() > 3) {
                break;
            }
        }
        System.out.println(Duration.between(instant, Instant.now()));
        return bestSolution;
    }

    public String toString() {
        return "GRASP";
    }


    // Greedy randomized construction
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


    // Best improvement local search
    private Solution runLocalSearchBI(Instance instance, Solution currentSolution) {
        Solution bestSolution = currentSolution;
        boolean improved = true;

        while (improved) {
            List<Solution> neighbors = bestSolution.getNeighborsSwapTwo();
            improved = false;
            for (Solution neighbor : neighbors) {
                if (Evaluator.isFeasible(neighbor, instance) && Evaluator.isBetter(neighbor, bestSolution, instance)) {
                    bestSolution = neighbor;
                    improved = true;
                }
            }
        }

        return bestSolution;
    }
}
