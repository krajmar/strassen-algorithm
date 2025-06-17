import mpi.MPI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Distributed {

    private final int [][] A,B;
    public Distributed(int size) {
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
    public void runDistributed(int sizeMat, int rank, int size){

        final int ROOT = 0;

        if (rank == ROOT) {
            int [][] A = this.A;
            int [][] B = this.B;

            // Podeli gi A i B na submatricite M1–M7
            int newSize = sizeMat / 2;
            int[][] A11 = new int[newSize][newSize];
            int[][] A12 = new int[newSize][newSize];
            int[][] A21 = new int[newSize][newSize];
            int[][] A22 = new int[newSize][newSize];
            int[][] B11 = new int[newSize][newSize];
            int[][] B12 = new int[newSize][newSize];
            int[][] B21 = new int[newSize][newSize];
            int[][] B22 = new int[newSize][newSize];

            //gi delime vrednostite od glavnite matrici vo submatricite
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    A11[i][j] = A[i][j];
                    A12[i][j] = A[i][j + newSize];
                    A21[i][j] = A[i + newSize][j];
                    A22[i][j] = A[i + newSize][j + newSize];
                    B11[i][j] = B[i][j];
                    B12[i][j] = B[i][j + newSize];
                    B21[i][j] = B[i + newSize][j];
                    B22[i][j] = B[i + newSize][j + newSize];
                }
            }

            // slednava niza se koristi za da isprakja informacii do workers za M1–M7
            //koristime MatrixPair da napravime parovi od matrici
            MatrixPair[] sendPairs = new MatrixPair[7];
                    sendPairs[0]=new MatrixPair(sumMatrix(A11, A22), sumMatrix(B11, B22)); // M1
                    sendPairs[1]=new MatrixPair(sumMatrix(A21, A22), B11);                      // M2
                    sendPairs[2]=new MatrixPair(A11, subtractMatrix(B12, B22));                 // M3
                    sendPairs[3]=new MatrixPair(A22, subtractMatrix(B21, B11));                 // M4
                    sendPairs[4]=new MatrixPair(sumMatrix(A11, A12), B22);                      // M5
                    sendPairs[5]=new MatrixPair(subtractMatrix(A21, A11), sumMatrix(B11, B12)); // M6
                    sendPairs[6]=new MatrixPair(subtractMatrix(A12, A22), sumMatrix(B21, B22));  // M7


            for (int i = 1; i <=7; i++) {//promena da e <=7 zoshto imame 7 procesi (workers)
                MPI.COMM_WORLD.Send(sendPairs, i-1, 1, MPI.OBJECT, i, 0); //gi isprakjame za sekoj worker po eden par
            }

            // niza koja gi prima rezultatite - niza od matrici
            int[][][] results = new int[7][][];
            for (int i = 1; i <= 7; i++) {
                Object[] recvBuf = new Object[1];
                MPI.COMM_WORLD.Recv(recvBuf, 0, 1, MPI.OBJECT, i, 1);
                results[i - 1] = (int[][]) recvBuf[0]; //gi fetchnuvame matricite
            }

            // Kreiraj ja finalnata matrica od matricite M1-M7
            int[][] M1 = results[0];
            int[][] M2 = results[1];
            int[][] M3 = results[2];
            int[][] M4 = results[3];
            int[][] M5 = results[4];
            int[][] M6 = results[5];
            int[][] M7 = results[6];

            //strassen algorithm ovde se primenuva
            int[][] C11 = sumMatrix(subtractMatrix(sumMatrix(M1, M4), M5), M7);
            int[][] C12 = sumMatrix(M3, M5);
            int[][] C21 = sumMatrix(M2, M4);
            int[][] C22 = sumMatrix(subtractMatrix(sumMatrix(M1, M3), M2), M6);

            //So kombiniranje se dobiva krajnata matrica
            int[][] C = new int[sizeMat][sizeMat];
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    C[i][j] = C11[i][j];
                    C[i][j + newSize] = C12[i][j];
                    C[i + newSize][j] = C21[i][j];
                    C[i + newSize][j + newSize] = C22[i][j];
                }
            }

            //ispishi potvrda
            System.out.println("Distributed multiplication done.");

            //ako sme vo workers, togash sekoj worker si ja izvrsuva svojata zadacha, mnozi matrici koi mu se dadeni
            //pogore spored funkciite ushte koga sme vo ROOT
        } else if (rank >= 1 && rank <=7) {//istata promena na rank da e 7 bidejki ima 7 worker procesi
            MatrixPair[] recvBuf = new MatrixPair[1];
            MPI.COMM_WORLD.Recv(recvBuf, 0, 1, MPI.OBJECT, ROOT, 0);
            int[][] A = recvBuf[0].A;
            int[][] B = recvBuf[0].B;

            int[][] C = multiplyMatrix(A, B);

            Object[] sendBuf = new Object[1];
            sendBuf[0] = C;
            MPI.COMM_WORLD.Send(sendBuf, 0, 1, MPI.OBJECT, ROOT, 1);
        }
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
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size(); //total number na procesite
        int[] sizeMatArray = new int[1]; //buffer za da ja ima goleminata na matricata.
        // za MPI. Bcast mora da bide niza

        if (rank == 0) { //samo root procesot prima input bidejki e main proces
            //drugite se workers
            sizeMatArray[0] = 1000;
        }
        //Broadcast na golemina na matrica od rank 0 do site drugi procesi
        //site procesi ucestvuvaat vo broadcastot
        MPI.COMM_WORLD.Bcast(sizeMatArray, 0, 1, MPI.INT, 0);

        int sizeMat = sizeMatArray[0];

        double startTime = System.currentTimeMillis();
        Distributed dis = new Distributed(sizeMat);

        dis.runDistributed(sizeMat, rank, size);
        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime; //presmetaj kolku vreme se executnuva algoritmot
        if (rank == 0) {
            //samo main procesot mozhe da pisuva vo CSV fajlot a ne site
            //za da nema concurrent access
            if (duration <= 600000) { //ako e povekje od 10min ne pishuvaj
                String fileName = "program_runtime.csv";
                try (FileWriter writer = new FileWriter(fileName, true)) {
                    String csvData = duration + " ms";

                    // Pishuva vo CSV fajlot
                    writer.append("Size: ").append(String.valueOf(sizeMat)).append(" \n"); //kolku e size od matrica

                    writer.append("Distributed:\n").append(csvData).append("\n"); //pishuva za distributed
                    //ushte ne e izvedena implementacijata no treba vo eden run za site 3 da se pishuva
                } catch (IOException e) {
                    System.err.println("Error writing to CSV file: " + e.getMessage());
                }
            } else System.out.println("More than 10mins, stop the testing.");

        }
        MPI.Finalize();
    }
}
