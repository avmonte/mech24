public class AutoAssociator {
    private int[][] weightMatrix;
    private int size;

    // Constructor for Hopfield Network
    public AutoAssociator(int size) {
        this.size = size;
        this.weightMatrix = new int[size][size];
    }

    // Method to train the network with a pattern
    public void train(int[] pattern) {
        if (pattern.length != size) {
            throw new IllegalArgumentException("Pattern size must match the size of the network");
        }

        // Outer product and addition to the weight matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    weightMatrix[i][j] += pattern[i] * pattern[j];
                }
            }
        }
    }

    // Method to recall a pattern
    public int[] recall(int[] pattern) {
        if (pattern.length != size) {
            throw new IllegalArgumentException("Pattern size must match the size of the network");
        }

        int[] result = new int[size];
        System.arraycopy(pattern, 0, result, 0, pattern.length);

        boolean change = true;
        while (change) {
            change = false;
            for (int i = 0; i < size; i++) {
                int sum = 0;
                for (int j = 0; j < size; j++) {
                    sum += weightMatrix[i][j] * result[j];
                }

                int updatedValue = sum >= 0 ? 1 : -1;
                if (updatedValue != result[i]) {
                    result[i] = updatedValue;
                    change = true;
                }
            }
        }
        return result;
    }
}
