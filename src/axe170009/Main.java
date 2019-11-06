package axe170009;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        //read the input file
        Scanner sc = new Scanner(new File("src/axe170009/connectivity_10.txt"));
        //Scanner sc = new Scanner(new File("src/axe170009/connectivity.txt"));
        if(args.length >= 1){
            sc = new Scanner(new File(args[0]));
        }

        //no. of process and uid for each process
        int n = sc.nextInt();
        int[] uids = new int[n];
        System.out.println("UID's==============");
        for(int i=0; i<n; i++){
            uids[i] = sc.nextInt();
            System.out.print(uids[i]+" ");
        }
        System.out.println();

        //reading about edges of graph
        boolean[][] graph = new boolean[n][n];
        System.out.println("Graph==============");
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                int isEdgePresent = sc.nextInt();
                if(isEdgePresent == 1) {
                    graph[i][j] = true;
                }
                System.out.print(isEdgePresent+" ");
            }
            System.out.println();
        }

        //finding diameter of the graph
        int diameter = findMaxDiameter(graph, n);
        System.out.println("Max Diameter of given graph=========="+diameter);
        Process[]  processes = new Process[n];
        for(int i=0; i<n; i++){
            processes[i] = new Process(uids[i], diameter);
        }

        //creating childThreads
        for(int i=0; i<n; i++){
            Process p = processes[i];
            for(int j=0; j<n; j++){
                if(graph[i][j]){
                    p.addNeighbour(processes[j]);
                }
            }
        }

        for(int i=0; i<n; i++){
            processes[i].start();
        }
        boolean areProcessRunning = false;
        int totalNumberOfMessages = 0;
        while(true){

            areProcessRunning = false;
            for(Process p : processes){
                if(p.getLeaderStatus()==Status.UNKNOWN){
                    areProcessRunning = true;
                }
            }

            if(!areProcessRunning){

                for(Process p : processes){
                    totalNumberOfMessages+=p.getNumOfMessages();
                    p.setTerminate(true);
                }
                System.out.println("Total Number of Messages : "+totalNumberOfMessages);
                System.out.println("==========Leader election completed===========");
                return;
            }
        }
    }

    /**
     * Method to find maximum diameter in graph topology
     * @param graph representing network topology
     * @param n no. of vertices
     * @return maximum diameter
     */
    private static int findMaxDiameter(boolean[][] graph, int n){
        int maxDiameter = 0;
        for(int i=0; i<n; i++){
            maxDiameter = Math.max(maxDiameter, findDiameter(i, graph, n));
        }
        return maxDiameter;
    }

    /**
     * Method to find diameter with vertex as a source in graph topology
     * @param vertex source vertex
     * @param graph representing network topology
     * @param n no. of vertices
     * @return diameter with vertex as source
     */
    private static int findDiameter(int vertex, boolean[][] graph, int n){
        int diameter = -1;
        Deque<Integer> q = new ArrayDeque<>();
        boolean[] seen = new boolean[n];
        q.addFirst(vertex);
        seen[vertex] = true;
        while(!q.isEmpty()){
            int size = q.size();
            diameter++;
            for(int i=0; i<size; i++){
                int curVertex = q.removeLast();
                for(int j=0; j<n; j++){
                    if(graph[curVertex][j] && !seen[j]){
                        q.addFirst(j);
                        seen[j] = true;
                    }
                }
            }
        }
        return diameter;
    }
}
