import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        //spored zadacata, korisnikot ja zadava goleminata na matricite
        System.out.println("Specify sizes of matrices: ");
        Scanner sc = new Scanner(System.in);
        double startTime = System.currentTimeMillis();

        int sizeMat = sc.nextInt();

        Sequential seq = new Sequential(sizeMat);


        //ja deli prvata matrica na cetvrtini (4 pomali kvadratni matrici)
        ArrayList<int[][]> arrList1 = seq.splitMatrix(seq.getMatrix1());
        //ja deli vtorata matrica na cetvrtini (4 pomali kvadratni matrici)
        ArrayList<int[][]> arrList2 = seq.splitMatrix(seq.getMatrix2());

        //ovde ke se zacuvaat finalnite cetvrtini (4 pomali kvadratni matrici)
        //potoa samo se spojuvaat vo krajnata matrica
        ArrayList<int[][]> finalArrayList = new ArrayList<>();

        //strassen algorithm matrici spored wikipedia
        int[][] M1 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(0), arrList1.get(3)),
                seq.sumMatrix(arrList2.get(0), arrList2.get(3)));
        int[][] M2 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(1), arrList1.get(3)),
                arrList2.get(0));
        int[][] M3 = seq.multiplyMatrix(arrList1.get(0), seq.subtractMatrix(arrList2.get(2), arrList2.get(3)));
        int[][] M4 = seq.multiplyMatrix(arrList1.get(3), seq.subtractMatrix(arrList2.get(1), arrList2.get(0)));
        int[][] M5 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(0), arrList1.get(2)), arrList2.get(3));
        int[][] M6 = seq.multiplyMatrix(seq.subtractMatrix(arrList1.get(1), arrList1.get(0)), seq.sumMatrix(arrList2.get(0), arrList2.get(2)));
        int[][] M7 = seq.multiplyMatrix(seq.subtractMatrix(arrList1.get(2), arrList1.get(3)), seq.sumMatrix(arrList2.get(1), arrList2.get(3)));


        int[][] matI = seq.sumMatrix(seq.subtractMatrix(seq.sumMatrix(M1, M4), M5), M7); //topleft za final matrix
        int[][] matJ = seq.sumMatrix(M2, M4); //bottomleft za final matrix
        int[][] matK = seq.sumMatrix(M3, M5); //topright za final matrix
        int[][] matL = seq.sumMatrix(seq.sumMatrix(seq.subtractMatrix(M1, M2), M3), M6); //bottomright za final matrix

        finalArrayList.add(matI);
        finalArrayList.add(matJ);
        finalArrayList.add(matK);
        finalArrayList.add(matL);

        seq.mergeMatrix(finalArrayList);

        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime; //presmetaj kolku vreme se executnuva algoritmot
        if (duration <= 600000) { //ako e povekje od 10min ne pishuvaj
            String fileName = "program_runtime.csv";
            try (FileWriter writer = new FileWriter(fileName, true)) {
                String csvData = duration + " ms";

                // Pishuva vo CSV fajlot
                writer.append("Size: ").append(String.valueOf(sizeMat)).append(" \n"); //kolku e size od matrica
                writer.append("Sequential: ").append(csvData).append("\n"); //pishuva za sequential
                writer.append("Parallel: yet to be determined\n"); //pishuva za parallel
                writer.append("Distributed: yet to be determined\n"); //pishuva za distributed
                //ushte ne e izvedena implementacijata no treba vo eden run za site 3 da se pishuva
            } catch (IOException e) {
                System.err.println("Error writing to CSV file: " + e.getMessage());
            }
        } else System.out.println("More than 10mins, stop the testing.");
    }
}
