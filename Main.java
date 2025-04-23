import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        //spored zadacata, korisnikot ja zadava goleminata na matricite
        System.out.println("Specify sizes of matrices: ");
        Scanner sc = new Scanner(System.in);

        int sizeMat = sc.nextInt();
        System.out.println("Choose from the menu for which implementation you want to test: ");
        System.out.println("=============================================");
        System.out.println("1. SEQUENTIAL");
        System.out.println("2. PARALLEL");
        System.out.println("3. DISTRIBUTED");
        System.out.println("=============================================");
        Scanner sc2 = new Scanner(System.in);
        int chosenImp = sc2.nextInt();
        double startTime = System.currentTimeMillis();

        if(chosenImp==1){
            sequentialRealisation(sizeMat);
        }
        else if(chosenImp==2){
            parallelRealisation(sizeMat);
        }
        else if(chosenImp==3){
            distributedRealisation(sizeMat);
        }
        else
            return;

        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime; //presmetaj kolku vreme se executnuva algoritmot
        if (duration <= 600000) { //ako e povekje od 10min ne pishuvaj
            String fileName = "program_runtime.csv";
            try (FileWriter writer = new FileWriter(fileName, true)) {
                String csvData = duration + " ms";

                // Pishuva vo CSV fajlot
                writer.append("Size: ").append(String.valueOf(sizeMat)).append(" \n"); //kolku e size od matrica
                if(chosenImp==1)
                    writer.append("Sequential: ").append(csvData).append("\n"); //pishuva za sequential
                else if(chosenImp==2)
                    writer.append("Parallel: yet to be determined\n"); //pishuva za parallel
                else {
                    writer.append("Distributed: yet to be determined\n"); //pishuva za distributed
                }
                //ushte ne e izvedena implementacijata no treba vo eden run za site 3 da se pishuva
            } catch (IOException e) {
                System.err.println("Error writing to CSV file: " + e.getMessage());
            }
        } else System.out.println("More than 10mins, stop the testing.");
    }

    public static void sequentialRealisation(int sizeMat){
        Sequential seq = new Sequential(sizeMat);
        seq.runSequential();
    }

    public static void parallelRealisation(int sizeMat){

    }

    public static void distributedRealisation(int sizeMat){

    }

}
