package es.urjc.grafo.ABII.Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public record Solution(boolean[] solution) {
    public Solution(Instance instance) {
        this(new boolean[instance.getTotalNumberOfItems()]);
    }

    public boolean isChosen(int index) {
        return this.solution()[index];
    }

    public void chooseSpecies(int index) {
        solution[index] = true;
    }

    public List<Solution> getNeighborsSwapTwo() {
        List<Solution> neighbors = new ArrayList<>();
        for (int i = 0; i < solution.length; i++) {
            for (int j = i + 1; j < solution.length; j++) {
                if (this.isChosen(i) != this.isChosen(j)) {
                    Solution neighbor = this.clone();
                    neighbor.swap(i, j);
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    public List<Solution> getNeighborsSwapRegion() {
        List<Solution> neighbors = new ArrayList<>();
        for (int i = 0; i < solution.length; i++) {
            for (int j = i + 1; j < solution.length; j++) {
                Solution neighbor = this.clone();
                int first = i;
                int last = j;
                while (first < last) {
                    neighbor.swap(first, last);
                    first++;
                    last--;
                }
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public Solution clone() {
        return new Solution(solution().clone());
    }

    public void swap(int i, int j) {
        boolean temp = solution[i];
        solution[i] = solution[j];
        solution[j] = temp;
    }


    // Solution alterations
    public void swapTwoElements() {
        int index1 = new Random().nextInt(solution().length);
        int index2 = new Random().nextInt(solution().length);

        while (index1 == index2) {
            index2 = new Random().nextInt(solution().length);
        }

        this.swap(index1, index2);
    }

    public void swapRegion() {
        int first = new Random().nextInt(solution().length);
        int last = new Random().nextInt(first, solution().length);
        while (first < last) {
            this.swap(first, last);
            first++;
            last--;
        }
    }
}
