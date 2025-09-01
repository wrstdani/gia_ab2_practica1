import es.urjc.grafo.ABII.Algorithms.Algorithm;
import es.urjc.grafo.ABII.Model.Evaluator;
import es.urjc.grafo.ABII.Model.Instance;
import es.urjc.grafo.ABII.Model.Solution;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmGeneralTest {

    private static Map<String, Double> expectedQuality = new HashMap<>();

    private static void fillMap(){
        expectedQuality.put("instancia_01_n10_m6.txt", 2106.73);
        expectedQuality.put("instancia_02_n10_m8.txt", 2720.61);
        expectedQuality.put("instancia_03_n15_m3.txt", 670.80);
        expectedQuality.put("instancia_04_n15_m9.txt", 5574.20);
        expectedQuality.put("instancia_05_n30_m6.txt", 1912.59);
        expectedQuality.put("instancia_06_n30_m24.txt", 34073.31);
        expectedQuality.put("instancia_07_n50_m15.txt", 10567.54);
        expectedQuality.put("instancia_08_n100_m10.txt", 5169.02);
        expectedQuality.put("instancia_09_n125_m37.txt", 106708.50);
        expectedQuality.put("instancia_10_n150_m15.txt", 21346.37);
        expectedQuality.put("instancia_11_n150_m45.txt", 126932.66);
        expectedQuality.put("instancia_12_n150_m45.txt", 188897.17);
        expectedQuality.put("instancia_13_n150_m45.txt", 135454.91);
        expectedQuality.put("instancia_14_n500_m50.txt", 16994.44);
        expectedQuality.put("instancia_15_n500_m50.txt", 17077.84);
    }

    public static void generalTest(String instancePath, Algorithm algorithm, long maxTimePerInstance) {
        try {
            fillMap();
            File folder = new File(instancePath);
            long numberOfInstances = 0;
            Instant instant = Instant.now();
            for (File fileEntry : folder.listFiles()) {
                numberOfInstances++;
                Instance instance = new Instance(instancePath + File.separator + fileEntry.getName());
                Solution solution = algorithm.run(instance);
                Assertions.assertTrue(Evaluator.isFeasible(solution, instance), "La solución no es factible");
                double score = Evaluator.evaluate(solution, instance);
                System.out.println("Para la instancia " + fileEntry.getName() +
                        ", la calidad de la solución generada por el algoritmo "
                        + algorithm.toString() + " es " + score);
                Assertions.assertTrue(
                        score >= expectedQuality.getOrDefault(fileEntry.getName(), 0.0),
                        "La calidad de la solución no es suficiente");
            }
            Duration elapsedTime = Duration.between(instant, Instant.now());
            Assertions.assertTrue(elapsedTime.getSeconds() <= (maxTimePerInstance * numberOfInstances),
                    "El algoritmo ha tardado más de un minuto de media");
        }
        catch (UnsupportedOperationException e) {
            Assertions.fail(algorithm.toString() + " no está implementado");
        }
        catch (Exception e) {
            Assertions.fail("Error en la ejecución del algoritmo");
        }
    }
}
