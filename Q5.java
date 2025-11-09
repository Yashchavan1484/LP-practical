// Q5.java
import java.util.*;

class Process {
    String id;
    int arrival, burst, priority;
    int waiting = 0, turnaround = 0;
}

public class Q5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            processes[i] = new Process(); 
            System.out.print("Enter Process ID: ");
            processes[i].id = sc.next();
            System.out.print("Enter Arrival Time: ");
            processes[i].arrival = sc.nextInt();
            System.out.print("Enter Burst Time: ");
            processes[i].burst = sc.nextInt();
            System.out.print("Enter Priority (lower = higher): ");
            processes[i].priority = sc.nextInt();
        }
                fcfs(processes, n);
                priority(processes, n);   
    }

    static void fcfs(Process[] processes, int n) {
        Arrays.sort(processes, Comparator.comparingInt(a -> a.arrival));
        int time = 0, totalWaiting = 0, totalTurnaround = 0;

        for (int i = 0; i < n; i++) {
            if (time < processes[i].arrival)
                time = processes[i].arrival;
            processes[i].waiting = time - processes[i].arrival;
            time += processes[i].burst;
            processes[i].turnaround = processes[i].waiting + processes[i].burst;
            totalWaiting += processes[i].waiting;
            totalTurnaround += processes[i].turnaround;
        }

        System.out.println("\n--- FCFS Scheduling ---");
        System.out.println("Process\tWaiting\tTurnaround");
        for (Process p : processes)
            System.out.println(p.id + "\t" + p.waiting + "\t" + p.turnaround);

        System.out.println("Average Waiting Time: " + (float)totalWaiting / n);
        System.out.println("Average Turnaround Time: " + (float)totalTurnaround / n);
    }

    static void priority(Process[] processes, int n) {
        int time = 0, totalWaiting = 0, totalTurnaround = 0;
        boolean[] done = new boolean[n];

        System.out.println("\n--- Priority Scheduling ---");
        System.out.println("Process\tWaiting\tTurnaround");

        for (int count = 0; count < n; count++) {
            int idx = -1, minPriority = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!done[i] && processes[i].arrival <= time && processes[i].priority < minPriority) {
                    minPriority = processes[i].priority;
                    idx = i;
                }
            }
            if (idx == -1) {
                time++;
                count--;
                continue;
            }
            processes[idx].waiting = time - processes[idx].arrival;
            time += processes[idx].burst;
            processes[idx].turnaround = processes[idx].waiting + processes[idx].burst;
            totalWaiting += processes[idx].waiting;
            totalTurnaround += processes[idx].turnaround;
            done[idx] = true;
            System.out.println(processes[idx].id + "\t" + processes[idx].waiting + "\t" + processes[idx].turnaround);
        }
        System.out.println("Average Waiting Time: " + (float)totalWaiting / n);
        System.out.println("Average Turnaround Time: " + (float)totalTurnaround / n);
    }
}


