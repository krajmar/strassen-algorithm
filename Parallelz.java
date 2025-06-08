import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
public class Parallelz {

    private int [][] A,B;
    public static final int threshold = 64; //koga matricite se dovolno mali pa ne ni treba vekje paralelna implementacija
    public Parallelz(int size) {
        this.A = new int[size][size];
        this.B = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int r1 = (int) (Math.random() * 10);
                int r2 = (int) (Math.random() * 10);
                this.A[i][j] = r1;
                this.B[i][j] = r2;
            }
        }
    }
    public int[][] parallelStrassen() {
        int size = nextPowerOfTwo(Math.max(A.length, Math.max(B.length, B[0].length))); //najdi sleden power of 2 za da raboti rekurzijata so matricite
        int[][] A_padded = padMatrix(A);
        int[][] B_padded = padMatrix(B);

        ForkJoinPool pool = new ForkJoinPool(); //threads koi imaat zavrseno so rabota ja kradat rabotata od drugite threads koi se zafateni

        int[][] C_padded = pool.invoke(new StrassenTask(A_padded, B_padded)); //rezultat od Strassen mnozenjeto

        return unpadMatrix(C_padded, A.length);
    }

    private static class StrassenTask extends RecursiveTask<int[][]>{
        private final int[][] A, B;

        public StrassenTask(int[][] A, int[][] B) {
            this.A = A;
            this.B = B;
        }

        @Override
        protected int[][] compute(){
            int n = A.length;
            if (n <= threshold) return multiplyMatrix(A,B);
            else {
                ArrayList<int[][]>As = splitMatrix(A);
                ArrayList<int[][]>Bs = splitMatrix(B);

                StrassenTask m1 = new StrassenTask(sumMatrix(As.get(0), As.get(3)), sumMatrix(Bs.get(0), Bs.get(3)));
                StrassenTask m2 = new StrassenTask(sumMatrix(As.get(2), As.get(3)), Bs.get(0));
                StrassenTask m3 = new StrassenTask(As.get(0), subtractMatrix(Bs.get(1), Bs.get(3)));
                StrassenTask m4 = new StrassenTask(As.get(3), subtractMatrix(Bs.get(2), Bs.get(0)));
                StrassenTask m5 = new StrassenTask(sumMatrix(As.get(0), As.get(1)), Bs.get(3));
                StrassenTask m6 = new StrassenTask(subtractMatrix(As.get(2), As.get(0)), sumMatrix(Bs.get(0), Bs.get(1)));
                StrassenTask m7 = new StrassenTask(subtractMatrix(As.get(1), As.get(3)), sumMatrix(Bs.get(2), Bs.get(3)));

                m1.fork(); m2.fork(); m3.fork(); m4.fork(); m5.fork(); m6.fork();
                int[][] M7 = m7.compute();
                int[][] M1 = m1.join();
                int[][] M2 = m2.join();
                int[][] M3 = m3.join();
                int[][] M4 = m4.join();
                int[][] M5 = m5.join();
                int[][] M6 = m6.join();
                int[][] C11 = sumMatrix(subtractMatrix(sumMatrix(M1, M4), M5), M7);
                int[][] C12 = sumMatrix(M3, M5);
                int[][] C21 = sumMatrix(M2, M4);
                int[][] C22 = sumMatrix(subtractMatrix(sumMatrix(M1, M3), M2), M6);

                return mergeMatrix(C11, C12, C21, C22,this.A,this.B);
            }
        }
    }


    public static ArrayList<int[][]> splitMatrix(int[][] matrix) {
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
            //System.arraycopy(pmatrix[i], 0, submat1[i], 0, size / 2);
            for (int j = 0; j < size / 2; j++)
                submat1[i][j]=pmatrix[i][j];
        }

        //bottom-left
        for (int i = size / 2; i < size; i++) {
            //System.arraycopy(pmatrix[i], 0, submat2[i - size / 2], 0, size / 2);
            for (int j = 0; j < size / 2; j++)
                submat2[i-size/2][j]=pmatrix[i][j];
        }

        //top-right
        for (int i = 0; i < size / 2; i++) {
            //System.arraycopy(pmatrix[i], size / 2, submat3[i], 0, size / 2);
            for (int j = size / 2; j < size; j++)
                submat3[i][j-size/2]=pmatrix[i][j];
        }

        //bottom-right
        for (int i = size / 2; i < size; i++) {
           // System.arraycopy(pmatrix[i], size / 2, submat4[i - size / 2], 0, size / 2);
            for (int j = size / 2; j < size; j++)
                submat4[i-size/2][j-size/2]=pmatrix[i][j];
        }

        arrList.add(submat1);
        arrList.add(submat2);
        arrList.add(submat3);
        arrList.add(submat4);

        return arrList;
    }


    public static int[][] mergeMatrix(int [][] C11, int [][] C12, int [][] C21, int [][] C22, int [][] A, int [][] B) {
        int size = C11.length; //site se od ista golemina
        int finalSize = size * 2; // spojuvame 4 submatrici taka da dolzinata na edna *2 = dolzina na celata
        int [][] finalMatrix = new int[finalSize][finalSize];

        //top-left
        for (int i = 0; i < size; i++) {
            //System.arraycopy(C11, 0, finalMatrix[i], 0, size);
            for (int j = 0; j < size; j++)
                finalMatrix[i][j] = C11[i][j];
        }

        //bottom-left
        for (int i = 0; i < size; i++) {
            //System.arraycopy(C12, 0, finalMatrix[size + i], 0, size);
            for (int j = 0; j < size; j++)
                finalMatrix[i+size][j] = C21[i][j];
        }

        //top-right
        for (int i = 0; i < size; i++) {
            //System.arraycopy(C21, 0, finalMatrix[i], size, size);
            for (int j = 0; j < size; j++)
                finalMatrix[i][j+size] = C12[i][j];
        }

        //bottom-right
        for (int i = 0; i < size; i++) {
            //System.arraycopy(C22, 0, finalMatrix[size + i], size, size);
            for (int j = 0; j < size; j++)
                finalMatrix[i+size][j+size] = C22[i][j];
        }

        //ako sme dodale edna kolona i red na pocetnata, sega gi odzemame od finalnata
            finalMatrix = unpadMatrix(finalMatrix, A.length);

        /*printMatrix(A);
        System.out.println();
        printMatrix(B);
        System.out.println();*/
        //printMatrix(finalMatrix);
        //System.out.println();
        return finalMatrix;
    }


    public static int[][] sumMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                newMat[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return newMat;
    }

    public static int[][] subtractMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                newMat[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return newMat;
    }
    /*public static int[][] padMatrix(int[][] matrix) {
        int originalSize = matrix.length;
        int newSize;
        if ((originalSize & (originalSize-1))==0)
            newSize = originalSize;
        else
            newSize=nextPowerOfTwo(originalSize);


        if (newSize == originalSize) {
            return matrix; // No padding needed
        }

        int[][] paddedMatrix = new int[newSize][newSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, paddedMatrix[i], 0, originalSize);
        }

        return paddedMatrix;
    }*/
    //funkcija za namaluvanje na dimenzijata na finalnata matrica za 1
    public static int[][] unpadMatrix(int[][] matrix, int originalSize) {
        int[][] unpaddedMatrix = new int[originalSize][originalSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, unpaddedMatrix[i], 0, originalSize);
        }
        return unpaddedMatrix;
    }
    public static int[][] padMatrix(int[][] matrix) {
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
    /*public static int[][] unpadMatrix(int[][] matrix, int originalSize) {
        int[][] unpaddedMatrix = new int[originalSize][originalSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, unpaddedMatrix[i], 0, originalSize);
        }
        return unpaddedMatrix;
    }*/
    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        n--;  // decrement n to handle the case when n is already a power of two
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
    public static int[][] multiplyMatrix(int[][] A, int[][] B) {
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

    public static void printMatrix(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }

    }
}


