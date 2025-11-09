// Q10.java
import java.util.*;

public class Q10 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of frames: ");
        int frames = sc.nextInt();
        System.out.print("Enter length of reference string: ");
        int n = sc.nextInt();
        int[] ref = new int[n];
        System.out.print("Enter reference string (space separated): ");
        for (int i = 0; i < n; i++)
            ref[i] = sc.nextInt();

        System.out.println("\nFIFO Page Replacement:");
        pageReplacementFIFO(ref, n, frames);

        System.out.println("\nOptimal Page Replacement:");
        pageReplacementOptimal(ref, n, frames);
    }

    static void pageReplacementFIFO(int[] ref, int n, int frames) {
        Set<Integer> frame = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        int faults = 0;

        System.out.println("Ref\tFrames\t\tHit/Fault");
        for (int i = 0; i < n; i++) {
            if (frame.contains(ref[i])) {
                printFrames(queue, frames, ref[i], "Hit");
            } else {
                faults++;
                if (frame.size() < frames) {
                    frame.add(ref[i]);
                    queue.add(ref[i]);
                } else {
                    frame.remove(queue.peek());
                    queue.poll();
                    frame.add(ref[i]);
                    queue.add(ref[i]);
                }
                printFrames(queue, frames, ref[i], "Fault");
            }
        }
        System.out.println("Total Page Faults: " + faults);
    }

    static void pageReplacementOptimal(int[] ref, int n, int frames) {
        List<Integer> frame = new ArrayList<>();
        int faults = 0;

        System.out.println("Ref\tFrames\t\tHit/Fault");
        for (int i = 0; i < n; i++) {
            if (frame.contains(ref[i])) {
                printFrames(frame, frames, ref[i], "Hit");
            } else {
                faults++;
                if (frame.size() < frames) {
                    frame.add(ref[i]);
                } else {
                    int idxToReplace = -1, farthestIdx = -1;
                    for (int j = 0; j < frames; j++) {
                        int nextUse = Integer.MAX_VALUE;
                        for (int k = i + 1; k < n; k++) {
                            if (ref[k] == frame.get(j)) {
                                nextUse = k;
                                break;
                            }
                        }
                        if (nextUse > farthestIdx) {
                            farthestIdx = nextUse;
                            idxToReplace = j;
                        }
                    }
                    frame.set(idxToReplace, ref[i]);
                }
                printFrames(frame, frames, ref[i], "Fault");
            }
        }
        System.out.println("Total Page Faults: " + faults);
    }

    static void printFrames(Collection<Integer> frame, int frames, int ref, String status) {
        System.out.print(ref + "\t");
        ArrayList<Integer> list = new ArrayList<>(frame);
        while (list.size() < frames)
            list.add(-1);
        for (int i = 0; i < frames; i++) {
            if (list.get(i) == -1)
                System.out.print("- ");
            else
                System.out.print(list.get(i) + " ");
        }
        System.out.println("\t" + status);
    }
}
