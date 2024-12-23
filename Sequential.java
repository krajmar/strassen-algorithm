import java.util.ArrayList;
import java.util.Random;

public class Sequential {
    private int[][] matrix1;
    private int[][] matrix2;
    private int[][] finalmatrix;

    public int[][] getMatrix1() {
        return matrix1;
    }

    public void setMatrix1(int[][] matrix1) {
        this.matrix1 = matrix1;
    }

    public int[][] getMatrix2() {
        return matrix2;
    }

    public void setMatrix2(int[][] matrix2) {
        this.matrix2 = matrix2;
    }

    public int[][] getFinalmatrix() {
        return finalmatrix;
    }

    public void setFinalmatrix(int[][] finalmatrix) {
        this.finalmatrix = finalmatrix;
    }

    public Sequential(){}

    public Sequential(int size){
        this.matrix1=new int[size][size];
        this.matrix2=new int[size][size];
        this.finalmatrix=new int[size][size];

        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                int r1=(int) (Math.random()*10);
                int r2=(int) (Math.random()*10);
                this.matrix1[i][j]=r1;
                this.matrix2[i][j]=r2;
            }
        }
    }
    public Sequential(int [][] matrix1, int [][] matrix2) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;

    }

    public ArrayList<int[][]> splitMatrix(int[][] matrix){
        int[][] submat1=new int[matrix.length/2][matrix.length/2];
        int[][] submat2=new int[matrix.length/2][matrix.length/2];
        int [][] submat3=new int[matrix.length/2][matrix.length/2];
        int [][] submat4=new int[matrix.length/2][matrix.length/2];
        //topleft
        for(int i=0;i<matrix.length/2;i++){
            for(int j=0;j<matrix.length/2;j++){
                submat1[i][j]=matrix[i][j];
            }
        }
        //bottomleft
        for(int i=matrix.length/2;i<matrix.length;i++){
            for(int j=0;j<matrix.length/2;j++){
                submat2[i-matrix.length/2][j]=matrix[i][j];
            }
        }
        //topright
        for(int i=0;i<matrix.length/2;i++){
            for(int j=matrix.length/2;j<matrix.length;j++){
                submat3[i][j-matrix.length/2]=matrix[i][j];
            }
        }
        //bottomright
        for(int i=matrix.length/2;i<matrix.length;i++){
            for(int j=matrix.length/2;j<matrix.length;j++){
                submat4[i-matrix.length/2][j-matrix.length/2]=matrix[i][j];
            }
        }


        ArrayList<int[][]>arrList=new ArrayList<>();
        arrList.add(submat1);
        arrList.add(submat2);
        arrList.add(submat3);
        arrList.add(submat4);
        return arrList;
    }
    public void mergeMatrix(ArrayList<int[][]>arrList){
        //topleft
        for(int i=0;i<arrList.get(0).length;i++){
            for(int j=0;j<arrList.get(0).length;j++){
                this.finalmatrix[i][j]=arrList.get(0)[i][j];
            }
        }
        //bottomleft
        for(int i=0;i<arrList.get(0).length;i++){
            for(int j=0;j<arrList.get(0).length;j++){
                this.finalmatrix[arrList.get(1).length+i][j]=arrList.get(1)[i][j];
            }
        }
        //topright
        for(int i=0;i<arrList.get(0).length;i++){
            for(int j=0;j<arrList.get(0).length;j++){
                this.finalmatrix[i][arrList.get(2).length+j]=arrList.get(2)[i][j];
            }
        }
        //bottomright
        for(int i=0;i<arrList.get(0).length;i++){
            for(int j=0;j<arrList.get(0).length;j++){
                this.finalmatrix[arrList.get(3).length+i][arrList.get(3).length+j]=arrList.get(3)[i][j];
            }
        }

        printMatrix(this.matrix1);
        System.out.println();
        printMatrix(this.matrix2);
        System.out.println();
        printMatrix(this.finalmatrix);
    }

    public int[][] sumMatrix(int [][] matrix1, int [][] matrix2){
        int [][] newMat = new int [matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for(int i=0;i<matrix1.length;i++){
            for(int j=0;j<matrix1.length;j++){
                newMat[i][j]+=matrix1[i][j];
                newMat[i][j]+=matrix2[i][j];
            }
        }
        return newMat;
    }

    public int[][] subtractMatrix(int [][] matrix1, int [][] matrix2){
        int [][] newMat = new int [matrix1.length][matrix1.length];
        //spored zadacata ako se dvete matrici so ista dolzina i sirina
        for(int i=0;i<matrix1.length;i++){
            for(int j=0;j<matrix1.length;j++){
                newMat[i][j]+=matrix1[i][j];
                newMat[i][j]-=matrix2[i][j];
            }
        }
        return newMat;
    }

    public int[][] multiplyMatrix(int [][] A, int [][] B) {
        int row1=A.length;
        int col1=A[0].length;
        int row2=B.length;
        int col2=B[0].length;

        if (row2 != col1) {
            return null;
        }
        int C[][] = new int[row1][col2];

        // Multiply the two matrices
        for (int i = 0; i < row1; i++) {
            for (int j = 0; j < col2; j++) {
                for (int k = 0; k < row2; k++)
                    C[i][j] += A[i][k] * B[k][j];
            }
        }
        return C;
    }
    public void printMatrix(int[][] mat){
        for(int i=0;i<mat.length;i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }
    }






}
