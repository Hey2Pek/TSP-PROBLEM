import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

             System.out.print("Please enter the total number of landmarks : ");

                String input = reader.readLine();
                int totalLandmarks = Integer.parseInt(input);
                if (totalLandmarks<=2){System.out.println("invalid landmark number"); throw new RuntimeException();}
                System.out.println("Total landmarks: " + totalLandmarks);
                System.out.println("\nThree input files are read.\n" + "The tour planning is now processing…");


        Map<String, Landmark> LandmarkMap = new LinkedHashMap<>();
        Map<String, List<TourPlanner>> TourPlannerMap = new LinkedHashMap<>();
        String filename1 = "personal_interest.txt";
        String filename2 = "visitor_load.txt";
        String filename3 = "landmark_map_data.txt";

        readLandmarkData(filename1, LandmarkMap);
        readVisitorLoadData(filename2, LandmarkMap);
        readTourPlannerData(filename3, LandmarkMap, TourPlannerMap);
        // print landmark data
        //printLandmarkData(LandmarkMap);
        // print TourPlanner
        // printTourPlannerData(TourPlannerMap);

        String[] NameHolder = new String[LandmarkMap.size()];
        FillNameHolder(LandmarkMap, NameHolder);


        // Adjacency matrix oluştur
        double[][] adjacencyMatrix = createAdjacencyMatrix(LandmarkMap, TourPlannerMap);

        // Matrisi yazdır
       // printAdjacencyMatrix(adjacencyMatrix);


        // TSP algoritmasını çalıştır ve sonuçları yazdır
        int startNode = 0;
        TspDynamicApproachIterative solver = new TspDynamicApproachIterative(startNode, adjacencyMatrix,NameHolder);

        List<String> path = solver.getTourNames();
        double travelTime= calculateTotalTravelTime(path, TourPlannerMap);
        System.out.println();
        System.out.println("The visited landmarks:");
        System.out.println(solver.printTourNames(path));
        System.out.println("Total attractiveness score: "+ solver.getMaxTourCost());
        System.out.println("Total Travel Time: "+travelTime);
    }

    public static double[][] createAdjacencyMatrix(Map<String, Landmark> landmarkMap, Map<String, List<TourPlanner>> journeyMap) {
        int numLandmarks = landmarkMap.size();
        double[][] adjacencyMatrix = new double[numLandmarks][numLandmarks];

        // Tüm matris değerlerini başlangıçta sıfırla
        for (int i = 0; i < numLandmarks; i++) {
            for (int j = 0; j < numLandmarks; j++) {
                adjacencyMatrix[i][j] =0.0;
            }
        }

        // Landmark Map ve TourPlanner Map veri yapılarından yararlanarak matrisi doldur
        for (int i = 0; i < numLandmarks; i++) {
            String from = landmarkMap.keySet().toArray(new String[0])[i];
            List<TourPlanner> tourPlanners = journeyMap.get(from);

            if (tourPlanners != null) {
                for (TourPlanner tourPlanner : tourPlanners) {
                    String to = tourPlanner.getTo();
                    Landmark toLandmark = landmarkMap.get(to);
                    if (toLandmark != null) {
                        int toIndex = getIndex(to, landmarkMap);
                        double baseAttrScore = tourPlanner.getBaseScore();
                        double attractivenessScore = (1 - toLandmark.getVisitorLoad()) * toLandmark.getPersonalInterest() * baseAttrScore;
                        adjacencyMatrix[i][toIndex] = attractivenessScore;
                    }
                }
            }
        }

        return adjacencyMatrix;
    }
    static int getIndex(String landmarkName, Map<String, Landmark> landmarkMap) {
        int i = 0;
        for (Map.Entry<String, Landmark> entry : landmarkMap.entrySet()) {
            if (entry.getKey().equals(landmarkName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static void readLandmarkData(String filename, Map<String, Landmark> landmarkMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] tokens = line.split("\\s+");
                if (tokens.length >= 2) {
                    String name = tokens[0];
                    double personalInterest = Double.parseDouble(tokens[1]);
                    Landmark landmark = new Landmark();
                    landmark.setName(name);
                    landmark.setPersonalInterest(personalInterest);
                    landmarkMap.put(name, landmark);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading landmark data: " + e.getMessage());
        }
    }

    private static void readVisitorLoadData(String filename, Map<String, Landmark> landmarkMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] tokens = line.split("\\s+");
                if (tokens.length >= 2) {
                    String name = tokens[0];
                    double visitorLoad = Double.parseDouble(tokens[1]);
                    Landmark landmark = landmarkMap.get(name);
                    if (landmark != null) {
                        landmark.setVisitorLoad(visitorLoad);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading visitor load data: " + e.getMessage());
        }
    }

    private static void readTourPlannerData(String filename, Map<String, Landmark> landmarkMap, Map<String, List<TourPlanner>> journeyMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] tokens = line.split("\\s+");
                if (tokens.length >= 4) {
                    String from = tokens[0];
                    String to = tokens[1];
                    double baseScore = Double.parseDouble(tokens[2]);
                    double travelTime = Double.parseDouble(tokens[3]);

                    Landmark fromLandmark = landmarkMap.computeIfAbsent(from, k -> new Landmark());
                    fromLandmark.setName(from);

                    Landmark toLandmark = landmarkMap.computeIfAbsent(to, k -> new Landmark());
                    toLandmark.setName(to);

                    TourPlanner tourPlanner = new TourPlanner();
                    tourPlanner.setTo(to);
                    tourPlanner.setBaseScore(baseScore);
                    tourPlanner.setTravelTime(travelTime);

                    List<TourPlanner> tourPlanners = journeyMap.computeIfAbsent(from, k -> new ArrayList<>());
                    tourPlanners.add(tourPlanner);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading journey data: " + e.getMessage());
        }
    }

    private static void FillNameHolder(Map<String, Landmark> landmarkMap, String[] NameHolder) {
        int index = 0;
        for (String landmarkName : landmarkMap.keySet()) {
            NameHolder[index] = landmarkName;
            index++;
        }
    }

    public static double calculateTotalTravelTime(List<String> path, Map<String, List<TourPlanner>> journeyMap) {
        double totalTime = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);

            List<TourPlanner> tourPlanners = journeyMap.get(from);
            if (tourPlanners != null) {
                for (TourPlanner tourPlanner : tourPlanners) {
                    if (tourPlanner.getTo().equals(to)) {
                        totalTime += tourPlanner.getTravelTime();
                        break;
                    }
                }
            }
        }
        return totalTime;
    }


    private static void printLandmarkData(Map<String, Landmark> landmarkMap) {
        for (Landmark landmark : landmarkMap.values()) {
            System.out.println(landmark.getName() + ": Personal Interest = " + landmark.getPersonalInterest() + ", Visitor Load = " + landmark.getVisitorLoad());
        }
    }

    private static void printTourPlannerData(Map<String, List<TourPlanner>> journeyMap) {
        for (Map.Entry<String, List<TourPlanner>> entry : journeyMap.entrySet()) {
            String from = entry.getKey();
            List<TourPlanner> tourPlanners = entry.getValue();

            for (TourPlanner tourPlanner : tourPlanners) {
                System.out.println("From = " + from + ", To = " + tourPlanner.getTo() + ", Base Score = " + tourPlanner.getBaseScore() + ", Travel Time = " + tourPlanner.getTravelTime());
            }
        }
    }
    private static void printAdjacencyMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.print("{");
            for (int i = 0; i < row.length; i++) {
                if (Math.abs(row[i] - Double.POSITIVE_INFINITY) < 1e-9) {
                    System.out.print("INF");
                } else {
                    System.out.printf("%.2f", row[i]);
                }
                if (i < row.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }
    }







}
