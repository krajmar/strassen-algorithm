import java.io.Serializable;

public class MatrixPair implements Serializable {
    public int[][] A;
    public int[][] B;

    public MatrixPair(int[][] A, int[][] B) {
        this.A = A;
        this.B = B;
    }
}