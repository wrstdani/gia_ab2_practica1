package es.urjc.grafo.ABII.Model;

public class Evaluator {

    public static double evaluate(Solution solution, Instance instance) {
        double totalDistance = 0;
        for (int i = 0; i < instance.getTotalNumberOfItems()-1; i++) {
            for (int j = i+1; j < instance.getTotalNumberOfItems(); j++) {
                totalDistance += instance.getDistance(i, j) * (solution.solution()[i] ? 1 : 0) * (solution.solution()[j] ? 1 : 0);
            }
        }
        return totalDistance;
    }

    public static boolean isFeasible(Solution solution, Instance instance) {
        int count = 0;
        for (boolean isItemSelected : solution.solution()) {
            if (isItemSelected) {
                count++;
            }
        }
        return count == instance.getNumberOfItemsToPick();
    }

    public static boolean isBetter(Solution solution1, Solution solution2, Instance instance) {
        if (solution1 == null) {
            return false;
        }
        if (solution2 == null) {
            return true;
        }
        return evaluate(solution1, instance) > evaluate(solution2, instance);
    }
}
