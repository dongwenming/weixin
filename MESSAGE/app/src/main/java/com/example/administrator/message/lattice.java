package com.example.administrator.message;

public class lattice {
    public static final int DIRECTION_UP=0;
    public static final int DIRECTION_DOWN=1;
    public static final int DIRECTION_LEFT=2;
    public static final int DIRECTION_RIGHT=3;



   public static int[][] lattice;
   public static boolean[][] booleans_lattice;

   public static boolean is_init(){
       if (lattice==null)return false;
       else return true;
   }

   public static void init(){


           lattice=new int[9][9];
           for(int i=0;i<9;i++)
               for (int j=0;j<9;j++)
                   lattice[i][j]=0;



           booleans_lattice=new boolean[9][9];
           for(int i=0;i<9;i++)
               for (int j=0;j<9;j++)
                   booleans_lattice[i][j]=false;


   }
}
