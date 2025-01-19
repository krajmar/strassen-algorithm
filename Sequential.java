import java.util.ArrayList;

public class Sequential {
    private int[][] matrix1;
    private int[][] matrix2;
    private int[][] finalmatrix;

    public int[][] getMatrix1() {
        return matrix1;
    }

    public int[][] getMatrix2() {
        return matrix2;
    }

    public Sequential() {
    }

    public Sequential(int size) {
        this.matrix1 = new int[size][size];
        this.matrix2 = new int[size][size];
        this.finalmatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int r1 = (int) (Math.random() * 10);
                int r2 = (int) (Math.random() * 10);
                this.matrix1[i][j] = r1;
                this.matrix2[i][j] = r2;
            }
        }
    }

    //funkcija za zgolemuvanje na dimenzijata na matricata za 1
    public int[][] padMatrix(int[][] matrix) {
        int originalSize = matrix.length;
        int newSize = (originalSize % 2 == 0) ? originalSize : originalSize + 1;

        if (newSize == originalSize) {
            return matrix; // No padding needed
        }

        int[][] paddedMatrix = new int[newSize][newSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, paddedMatrix[i], 0, originalSize);
        }

        return paddedMatrix;
    }
    //funkcija za namaluvanje na dimenzijata na finalnata matrica za 1
    public int[][] unpadMatrix(int[][] matrix, int originalSize) {
        int[][] unpaddedMatrix = new int[originalSize][originalSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, unpaddedMatrix[i], 0, originalSize);
        }
        return unpaddedMatrix;
    }

    public ArrayList<int[][]> splitMatrix(int[][] matrix) {
        ArrayList<int[][]> arrList = new ArrayList<>();
        int size = matrix.length;
        int[][] pmatrix = matrix; // Default so orginalnata matrica

        // ako e neparna, zgolemi ja dolzinata i sirinata za 1 kolona i red
        if (size % 2 == 1) {
            pmatrix = padMatrix(matrix);
            size += 1; // se zgolemuva goleminata
        }

        // gi kreirame submatricite
        int[][] submat1 = new int[size / 2][size / 2];
        int[][] submat2 = new int[size / 2][size / 2];
        int[][] submat3 = new int[size / 2][size / 2];
        int[][] submat4 = new int[size / 2][size / 2];

        //top-left
        for (int i = 0; i < size / 2; i++) {
            System.arraycopy(pmatrix[i], 0, submat1[i], 0, size / 2);
        }

        //bottom-left
        for (int i = size / 2; i < size; i++) {
            System.arraycopy(pmatrix[i], 0, submat2[i - size / 2], 0, size / 2);
        }

        //top-right
        for (int i = 0; i < size / 2; i++) {
            System.arraycopy(pmatrix[i], size / 2, submat3[i], 0, size / 2);
        }

        //bottom-right
        for (int i = size / 2; i < size; i++) {
            System.arraycopy(pmatrix[i], size / 2, submat4[i - size / 2], 0, size / 2);
        }

        arrList.add(submat1);
        arrList.add(submat2);
        arrList.add(submat3);
        arrList.add(submat4);

        return arrList;
    }


    public void mergeMatrix(ArrayList<int[][]> arrList) {
        int size = arrList.get(0).length; //site se od ista golemina
        int finalSize = size * 2; // spojuvame 4 submatrici taka da dolzinata na edna *2 = dolzina na celata

        if (this.finalmatrix.length < finalSize) {
            this.finalmatrix = new int[finalSize][finalSize];
        }

        //top-left
        for (int i = 0; i < size; i++) {
            System.arraycopy(arrList.get(0)[i], 0, this.finalmatrix[i], 0, size);
        }

        //bottom-left
        for (int i = 0; i < size; i++) {
            System.arraycopy(arrList.get(1)[i], 0, this.finalmatrix[size + i], 0, size);
        }

        //top-right
        for (int i = 0; i < size; i++) {
            System.arraycopy(arrList.get(2)[i], 0, this.finalmatrix[i], size, size);
        }

        //bottom-right
        for (int i = 0; i < size; i++) {
            System.arraycopy(arrList.get(3)[i], 0, this.finalmatrix[size + i], size, size);
        }

        //ako sme dodale edna kolona i red na pocetnata, sega gi odzemame od finalnata
        if (this.matrix1.length % 2 != 0) {
            this.finalmatrix = unpadMatrix(this.finalmatrix, this.matrix1.length);
        }

        printMatrix(this.matrix1);
        System.out.println();
        printMatrix(this.matrix2);
        System.out.println();
        printMatrix(this.finalmatrix);
    }


    public int[][] sumMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                newMat[i][j] += matrix1[i][j];
                newMat[i][j] += matrix2[i][j];
            }
        }
        return newMat;
    }

    public int[][] subtractMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                newMat[i][j] += matrix1[i][j];
                newMat[i][j] -= matrix2[i][j];
            }
        }
        return newMat;
    }

    public int[][] multiplyMatrix(int[][] A, int[][] B) {
        int row1 = A.length;
        int col1 = A[0].length;
        int row2 = B.length;
        int col2 = B[0].length;

        if (row2 != col1) {
            return null;
        }
        int[][] C = new int[row1][col2];

        for (int i = 0; i < row1; i++) {
            for (int j = 0; j < col2; j++) {
                for (int k = 0; k < row2; k++)
                    C[i][j] += A[i][k] * B[k][j];
            }
        }
        return C;
    }

    public void printMatrix(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }

    }
}
