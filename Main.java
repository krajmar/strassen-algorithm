import java.util.*;
public class Main {
    public static void main(String[] args) {
        //spored zadacata, korisnikot ja zadava goleminata na matricite
        System.out.println("Specify sizes of matrices: ");
        Scanner sc = new Scanner(System.in);
        int sizeMat = sc.nextInt();
        //int [][] matrix1 = new int[sizeMat][sizeMat];
        //int [][] matrix2 = new int[sizeMat][sizeMat];
        Sequential seq = new Sequential(sizeMat);

        //ja deli prvata matrica na cetvrtini (4 pomali kvadratni matrici)
        ArrayList<int[][]>arrList1=seq.splitMatrix(seq.getMatrix1());
        //ja deli vtorata matrica na cetvrtini (4 pomali kvadratni matrici)
        ArrayList<int[][]>arrList2=seq.splitMatrix(seq.getMatrix2());

        //ovde ke se zacuvaat finalnite cetvrtini (4 pomali kvadratni matrici)
        //potoa samo se spojuvaat vo krajnata matrica
        ArrayList<int[][]>finalArrayList=new ArrayList<>();

        //strassen algorithm matrici spored wikipedia
        int [][] M1 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(0),arrList1.get(3)),
                seq.sumMatrix(arrList2.get(0),arrList2.get(3)));
        int [][] M2 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(1),arrList1.get(3)),
                arrList2.get(0));
        int [][] M3 = seq.multiplyMatrix(arrList1.get(0),seq.subtractMatrix(arrList2.get(2),arrList2.get(3)));
        int [][] M4 = seq.multiplyMatrix(arrList1.get(3),seq.subtractMatrix(arrList2.get(1),arrList2.get(0)));
        int [][] M5 = seq.multiplyMatrix(seq.sumMatrix(arrList1.get(0),arrList1.get(2)),arrList2.get(3));
        int [][] M6 = seq.multiplyMatrix(seq.subtractMatrix(arrList1.get(1),arrList1.get(0)),seq.sumMatrix(arrList2.get(0),arrList2.get(2)));
        int [][] M7 = seq.multiplyMatrix(seq.subtractMatrix(arrList1.get(2),arrList1.get(3)),seq.sumMatrix(arrList2.get(1),arrList2.get(3)));


        int [][] matI=seq.sumMatrix(seq.subtractMatrix(seq.sumMatrix(M1,M4),M5),M7); //topleft za final matrix
        int [][] matJ=seq.sumMatrix(M2,M4); //bottomleft za final matrix
        int [][] matK=seq.sumMatrix(M3,M5); //topright za final matrix
        int [][] matL=seq.sumMatrix(seq.sumMatrix(seq.subtractMatrix(M1,M2),M3),M6); //bottomright za final matrix

        finalArrayList.add(matI);
        finalArrayList.add(matJ);
        finalArrayList.add(matK);
        finalArrayList.add(matL);

        seq.mergeMatrix(finalArrayList);

    }
    public static void printMatrix(int[][] mat){
        for(int i=0;i<mat.length;i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }
    }
}
