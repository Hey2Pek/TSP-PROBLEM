import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TspDynamicApproachIterative {
    // Time Complexity= O(N^2 * 2^N) = O(N^2 * 2^N) +O(N^2) +O(n choose r)+ O(N)

    private final int N, start;
    private final double[][] TSP_Matrix;
    private final String[] nameHolder;
    private List<Integer> City = new ArrayList<>();
    private double maxTourCost = Double.MIN_VALUE; // Change: Double.MAX_VALUE -> Double.MIN_VALUE
    private boolean ranSolver = false;
    public TspDynamicApproachIterative(int start, double[][] TSP_Matrix, String[] nameHolder) {
        N = TSP_Matrix.length;
        if (N <= 2) throw new IllegalStateException("N <= 2 not yet supported.");
        if (N != TSP_Matrix[0].length) throw new IllegalStateException("Matrix must be square (n x n)");
        if (start < 0 || start >= N) throw new IllegalArgumentException("Invalid start node.");
        if (N > 32)
            throw new IllegalArgumentException(
                    "Matrix too large! A matrix that size for the DP TSP problem with a time complexity of"
                            + "O(n^2*2^n) requires way too much computation for any modern home computer to handle");

        this.start = start;
        this.TSP_Matrix = TSP_Matrix;
        this.nameHolder=nameHolder;

    }
    // Returns the maximal City cost.
    // Time Complexity: O(N^2)
    public double getMaxTourCost() {
        if (!ranSolver) solve();
        return maxTourCost;
    }

    // Solves the traveling salesman problem and caches solution.
    // Time Complexity: O(N^2 * 2^N)

    public void solve() {

        if (ranSolver) return;

        final int END_STATE = (1 << N) - 1;
        Double[][] memo = new Double[N][1 << N];

        // Add all outgoing edges from the starting node to memo table.
        for (int end = 0; end < N; end++) {     //O(n)
            if (end == start) continue;
            memo[end][(1 << start) | (1 << end)] = TSP_Matrix[start][end];
        }

        for (int r = 3; r <= N; r++) { //O(n)
            for (int subset : combinations(r, N)) {
                if (notIn(start, subset)) continue;
                for (int next = 0; next < N; next++) { //O(n*n)=O(n^2)
                    if (next == start || notIn(next, subset)) continue;
                    int subsetWithoutNext = subset ^ (1 << next);
                    double maxDist = Double.MIN_VALUE; // Change: Double.MAX_VALUE -> Double.MIN_VALUE
                    for (int end = 0; end < N; end++) {
                        if (end == start || end == next || notIn(end, subset)) continue;
                        double newDistance = memo[end][subsetWithoutNext] + TSP_Matrix[end][next];
                        if (newDistance > maxDist) { //Change: newDistance < maxDist -> newDistance > maxDist
                            maxDist = newDistance;
                        }
                    }
                    memo[next][subset] = maxDist;
                }
            }
        }

        // Connect City back to starting node and maximize cost.
        for (int i = 0; i < N; i++) {
            if (i == start) continue;
            double tourCost = memo[i][END_STATE] + TSP_Matrix[i][start];
            if (tourCost > maxTourCost) { // Change: tourCost < maxTourCost -> tourCost > maxTourCost
                maxTourCost = tourCost;
            }
        }

        int lastIndex = start;
        int state = END_STATE;
        City.add(start);

        // Reconstruct TSP path from memo table.
        for (int i = 1; i < N; i++) {
            int bestIndex = -1;
            double bestDist = Double.MIN_VALUE; // Change: Double.MAX_VALUE -> Double.MIN_VALUE
            for (int j = 0; j < N; j++) {
                if (j == start || notIn(j, state)) continue;
                double newDist = memo[j][state] + TSP_Matrix[j][lastIndex];
                if (newDist > bestDist) { // Change: newDist < bestDist -> newDist > bestDist
                    bestIndex = j;
                    bestDist = newDist;
                }
            }

            City.add(bestIndex);
            state = state ^ (1 << bestIndex);
            lastIndex = bestIndex;
        }

        City.add(start);
        Collections.reverse(City);

        ranSolver = true;
    }

    private static boolean notIn(int elem, int subset) {
        return ((1 << elem) & subset) == 0;
    }

    // This method generates all bit sets of size n where r bits
    // are set to one. The result is returned as a list of integer masks.
    public static List<Integer> combinations(int r, int n) {
        List<Integer> subsets = new ArrayList<>();
        combinations(0, 0, r, n, subsets);
        return subsets;
    }

    // To find all the combinations of size r we need to recurse until we have
    // selected r elements (aka r = 0), otherwise if r != 0 then we still need to select
    // an element which is found after the position of our last selected element
    // Time Complexity: O(n choose r) combination (n of r)
    private static void combinations(int set, int at, int r, int n, List<Integer> subsets) {

        // Return early if there are more elements left to select than what is available.
        int elementsLeftToPick = n - at;
        if (elementsLeftToPick < r) return;

        // We selected 'r' elements so we found a valid subset!
        if (r == 0) {
            subsets.add(set);
        } else {
            for (int i = at; i < n; i++) {
                // Try including this element
                set ^= (1 << i);

                combinations(set, i + 1, r - 1, n, subsets);

                // Backtrack and try the instance where we did not include this element
                set ^= (1 << i);
            }
        }
    }


    // Returns the optimal City for the traveling salesman problem.
    // Time Complexity: O(N)
    public List<String> getTourNames() {
        if (!ranSolver) solve();
        List<String> tourNames = new ArrayList<>();
        for (int index : City) {
            tourNames.add(nameHolder[index]);
        }
        return tourNames;
    }

    public static StringBuilder printTourNames(List<String> tourNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tourNames.size(); i++) {
            sb.append((i + 1)+"-"+ tourNames.get(i)+"\n");
        }
        return sb;
    }


}
