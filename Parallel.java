import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class Parallel{

    public static final int threshold = 64;
    private int[][] matrix1;
    private int[][] matrix2;
    private int[][] finalmatrix;

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public int[][] getMatrix1() {
        return matrix1;
    }

    public int[][] getMatrix2() {
        return matrix2;
    }

    public Parallel() {
    }

    public Parallel(int size) {
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

    public void runParallel(){
        ArrayList<int[][]> arrList1 = splitMatrix(getMatrix1());
        //ja deli vtorata matrica na cetvrtini (4 pomali kvadratni matrici)
        ArrayList<int[][]> arrList2 = splitMatrix(getMatrix2());

        //ovde ke se zacuvaat finalnite cetvrtini (4 pomali kvadratni matrici)
        //potoa samo se spojuvaat vo krajnata matrica
        ArrayList<int[][]> finalArrayList = new ArrayList<>();

        //strassen algorithm matrici spored wikipedia
        int[][] M1 = multiplyMatrix(sumMatrix(arrList1.get(0), arrList1.get(3)),
                sumMatrix(arrList2.get(0), arrList2.get(3)));
        int[][] M2 = multiplyMatrix(sumMatrix(arrList1.get(1), arrList1.get(3)),
                arrList2.get(0));
        int[][] M3 = multiplyMatrix(arrList1.get(0), subtractMatrix(arrList2.get(2), arrList2.get(3)));
        int[][] M4 = multiplyMatrix(arrList1.get(3), subtractMatrix(arrList2.get(1), arrList2.get(0)));
        int[][] M5 = multiplyMatrix(sumMatrix(arrList1.get(0), arrList1.get(2)), arrList2.get(3));
        int[][] M6 = multiplyMatrix(subtractMatrix(arrList1.get(1), arrList1.get(0)), sumMatrix(arrList2.get(0), arrList2.get(2)));
        int[][] M7 = multiplyMatrix(subtractMatrix(arrList1.get(2), arrList1.get(3)), sumMatrix(arrList2.get(1), arrList2.get(3)));


        int[][] matI = sumMatrix(subtractMatrix(sumMatrix(M1, M4), M5), M7); //topleft za final matrix
        int[][] matJ = sumMatrix(M2, M4); //bottomleft za final matrix
        int[][] matK = sumMatrix(M3, M5); //topright za final matrix
        int[][] matL = sumMatrix(sumMatrix(subtractMatrix(M1, M2), M3), M6); //bottomright za final matrix

        finalArrayList.add(matI);
        finalArrayList.add(matJ);
        finalArrayList.add(matK);
        finalArrayList.add(matL);

        mergeMatrix(finalArrayList);
    }

    public int[][] sumMatrix(int[][] matrix1, int[][] matrix2) {
        int size = matrix1.length;
        int[][] result = new int[size][size];
        int numThreads = Runtime.getRuntime().availableProcessors();
        List<Future<?>> futures = new ArrayList<>();

        int chunkSize = size / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;

            futures.add(executor.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        result[i][j] = matrix1[i][j] + matrix2[i][j];
                    }
                }
            }));
        }

        waitFutures(futures);
        return result;
    }

    private void waitFutures(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }



    /*public int[][] sumMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];

        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread [] threads = new Thread[numThreads]; //inicijalizacija na niza od Threads
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        int size = matrix1.length;
        int chunkSize = size / numThreads;

        for (int t = 0; t < numThreads; t++) {
            final int startRow = t * chunkSize;
            final int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;

            threads[t] = new Thread(() -> {
                //sekoj thread raboti na odreden chunk od redovi dovolno za da ne se nadmine brojot na cores
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        newMat[i][j] = matrix1[i][j] + matrix2[i][j];
                    }
                }
            });

            threads[t].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join(); //spoi gi Threads ovde
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at sumMatrix: " + e.getMessage());
            }
        }
        return newMat;
    }*/
    /*public int[][] subtractMatrix(int[][] matrix1, int[][] matrix2) {
        int[][] newMat = new int[matrix1.length][matrix1.length];
        int size = matrix1.length;

        int numThreads = Runtime.getRuntime().availableProcessors();

        Thread [] threadsSub = new Thread[numThreads];
        int chunkSize = size / numThreads;

        //spored zadacata ako se dvete matrici so ista dolzina i sirina

        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;
            threadsSub[t] = new Thread(() -> { //specificiraj zadaca za Threadot vo slucajov gi odzema redovite koi shto se corresponding
                //sekoj thread raboti na odreden chunk od redovi dovolno za da ne se nadmine brojot na cores
                for (int i = startRow; i < endRow; i++) {
                        for (int j = 0; j < size; j++) {
                            newMat[i][j] += matrix1[i][j];
                            newMat[i][j] -= matrix2[i][j];
                        }
                    }
            });
            threadsSub[t].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threadsSub[i].join(); //spoi gi Threads ovde
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at subtractMatrix: " + e.getMessage());
            }
        }
        return newMat;
    }*/

    public int[][] subtractMatrix(int[][] matrix1, int[][] matrix2) {
        int size = matrix1.length;
        int[][] result = new int[size][size];
        int numThreads = Runtime.getRuntime().availableProcessors();

        List<Future<?>> futures = new ArrayList<>();

        int chunkSize = size / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;

            futures.add(executor.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        result[i][j] = matrix1[i][j] - matrix2[i][j];
                    }
                }
            }));
        }

        waitFutures(futures);
        return result;
    }


    /*public int[][] multiplyMatrix(int[][] A, int[][] B) {
        int row1 = A.length;
        int col1 = A[0].length;
        int row2 = B.length;
        int col2 = B[0].length;

        if (row2 != col1) {
            return null;
        }
        int[][] C = new int[row1][col2];

        int numThreads = Runtime.getRuntime().availableProcessors();
        int chunkSize = row1 / numThreads;

        Thread [] threadsMul = new Thread[numThreads];

        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? row1 : startRow + chunkSize;
            threadsMul[t] = new Thread(()-> { //pravime Thread taka da sekoj thread obrabotuva chunk redovi, da ne go nadmineme limitot na cores
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < col2; j++) {
                        for (int k = 0; k < row2; k++)
                            C[i][j] += A[i][k] * B[k][j];
                    }
                }
            });
            threadsMul[t].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threadsMul[i].join(); //spoi gi Threads ovde
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at multiplyMatrix: " + e.getMessage());
            }
        }
        return C;
    }*/

    public int[][] multiplyMatrix(int[][] A, int[][] B) {
        int row1 = A.length, col1 = A[0].length, row2 = B.length, col2 = B[0].length;
        if (col1 != row2) return null;

        int[][] result = new int[row1][col2];
        int numThreads = Runtime.getRuntime().availableProcessors();

        List<Future<?>> futures = new ArrayList<>();

        int chunkSize = row1 / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? row1 : startRow + chunkSize;

            futures.add(executor.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < col2; j++) {
                        for (int k = 0; k < col1; k++) {
                            result[i][j] += A[i][k] * B[k][j];
                        }
                    }
                }
            }));
        }

        waitFutures(futures);
        return result;
    }


    public void mergeMatrix(ArrayList<int[][]> arrList) {
        int size = arrList.get(0).length; //site se od ista golemina
        int finalSize = size * 2; // spojuvame 4 submatrici taka da dolzinata na edna *2 = dolzina na celata

        if (this.finalmatrix.length < finalSize) {
            this.finalmatrix = new int[finalSize][finalSize];
        }

        //top-left
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread [] threads = new Thread[numThreads];
        int chunkSize = size / numThreads;

        List<Future<?>> futures = new ArrayList<>();

        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;
                futures.add(executor.submit(()-> {
                for (int i = startRow; i < endRow; i++) {
                    System.arraycopy(arrList.get(0)[i], 0, this.finalmatrix[i], 0, size);
                }
            }));
                //threads[t].start();
        }

        /*for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join(); // Wait for each thread to finish
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at mergeMatrix top left: " + e.getMessage());
            }
        }*/

        //bottom-left
        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;
            futures.add(executor.submit(()-> {
                for (int i = startRow; i < endRow; i++) {
            System.arraycopy(arrList.get(1)[i], 0, this.finalmatrix[size + i], 0, size);
                }
            }));
            //threads[t].start();
        }

        /*for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join(); // Wait for each thread to finish
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at mergeMatrix bottom left: " + e.getMessage());
            }
        }*/

        //top-right
        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;
            futures.add(executor.submit(()-> {
                for (int i = startRow; i < endRow; i++) {
                System.arraycopy(arrList.get(2)[i], 0, this.finalmatrix[i], size, size);
                }
            }));
               // threads[t].start();


                /*for (int i = 0; i < numThreads; i++) {
                try {
                threads[i].join(); // Wait for each thread to finish
                } catch (InterruptedException e) {
                System.err.println("Error joining threads at mergeMatrix top right: " + e.getMessage());
                }*/
        }

        //bottom-right
        for(int t=0; t<numThreads; t++) {
            int startRow = t * chunkSize;
            int endRow = (t == numThreads - 1) ? size : startRow + chunkSize;
            futures.add(executor.submit(()-> {
                for (int i = startRow; i < endRow; i++) {
                    System.arraycopy(arrList.get(3)[i], 0, this.finalmatrix[size + i], 0, size);
                }
            }));
            //threads[t].start();
        }

        /*for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join(); // Wait for each thread to finish
            } catch (InterruptedException e) {
                System.err.println("Error joining threads at mergeMatrix bottom right: " + e.getMessage());
            }
        }*/

        waitFutures(futures);


        //ako sme dodale edna kolona i red na pocetnata, sega gi odzemame od finalnata
        if (this.matrix1.length % 2 != 0) {
            this.finalmatrix = unpadMatrix(this.finalmatrix, this.matrix1.length);
        }


        //printMatrix(this.matrix1);
        //System.out.println();
        //printMatrix(this.matrix2);
        //System.out.println();
        //printMatrix(this.finalmatrix);
    }

    public void printMatrix(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
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

    public int[][] unpadMatrix(int[][] matrix, int originalSize) {
        int[][] unpaddedMatrix = new int[originalSize][originalSize];
        for (int i = 0; i < originalSize; i++) {
            System.arraycopy(matrix[i], 0, unpaddedMatrix[i], 0, originalSize);
        }
        return unpaddedMatrix;
    }

    public ArrayList<int[][]> splitMatrix(int[][] matrix) {
        int size = matrix.length;
        int[][] pmatrix;

        // Pad if size is odd
        if (size % 2 == 1) {
            pmatrix = padMatrix(matrix);
            size += 1;
        } else {
            pmatrix = matrix;
        }

        int halfSize = size / 2;
        List<Future<int[][]>> futures = new ArrayList<>();

        // Define quadrant extraction tasks
        futures.add(executor.submit(() -> {
            int[][] submat1 = new int[halfSize][halfSize]; // Top-left
            for (int i = 0; i < halfSize; i++) {
                System.arraycopy(pmatrix[i], 0, submat1[i], 0, halfSize);
            }
            return submat1;
        }));

        futures.add(executor.submit(() -> {
            int[][] submat2 = new int[halfSize][halfSize]; // Bottom-left
            for (int i = 0; i < halfSize; i++) {
                System.arraycopy(pmatrix[i + halfSize], 0, submat2[i], 0, halfSize);
            }
            return submat2;
        }));

        futures.add(executor.submit(() -> {
            int[][] submat3 = new int[halfSize][halfSize]; // Top-right
            for (int i = 0; i < halfSize; i++) {
                System.arraycopy(pmatrix[i], halfSize, submat3[i], 0, halfSize);
            }
            return submat3;
        }));

        futures.add(executor.submit(() -> {
            int[][] submat4 = new int[halfSize][halfSize]; // Bottom-right
            for (int i = 0; i < halfSize; i++) {
                System.arraycopy(pmatrix[i + halfSize], halfSize, submat4[i], 0, halfSize);
            }
            return submat4;
        }));

        // Gather results
        ArrayList<int[][]> arrList = new ArrayList<>(4);
        try {
            for (Future<int[][]> f : futures) {
                arrList.add(f.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return arrList;
    }
}

