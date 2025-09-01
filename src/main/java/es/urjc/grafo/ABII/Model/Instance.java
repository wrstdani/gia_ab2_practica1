package es.urjc.grafo.ABII.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Instance {

    private double[][] distances;
    private int numberOfItemsToPick;
    private int totalNumberOfItems;

    /**
     * Reads the file line by line
     * @param filePath
     */
    public Instance(String filePath) {
        File instance = new File(filePath);
        try {
            Scanner scanner = new Scanner(instance);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(" ");
                totalNumberOfItems = Integer.parseInt(splitLine[0]);
                numberOfItemsToPick = Integer.parseInt(splitLine[1]);
                distances = new double[totalNumberOfItems][totalNumberOfItems];
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(" ");
                int firstItem = Integer.parseInt(splitLine[0]);
                int secondItem = Integer.parseInt(splitLine[1]);
                double distance = Double.parseDouble(splitLine[2]);
                if (firstItem >= distances.length || secondItem >= distances[0].length) {
                    throw new IllegalArgumentException("Invalid instance file");
                }
                this.distances[firstItem][secondItem] = distance;
                this.distances[secondItem][firstItem] = distance;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double[][] getDistances() {
        return this.distances;
    }

    public double[] getDistancesFrom(int i) { return this.distances[i]; }

    public double getDistance(int i, int j) {
        return this.distances[i][j];
    }

    public int getNumberOfItemsToPick() {
        return this.numberOfItemsToPick;
    }

    public int getTotalNumberOfItems() {
        return this.totalNumberOfItems;
    }

}
