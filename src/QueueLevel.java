import java.util.LinkedList;
import java.util.Queue;

public class QueueLevel {
    private Queue<Process> processes;
    public String type;  // "RR" or "FCFS"
    public int quantum;  // quantum time for RR, 0 for FCFS

    public QueueLevel(String type, int quantum) {
        this.type = type;
        this.quantum = quantum;
        this.processes = new LinkedList<>();
    }

    // Add process to the queue
    public void addProcess(Process p) {
        processes.offer(p);
    }

    // Get the next process from the queue
    public Process getNext() {
        return processes.poll();
    }

    // Reinsert a process at the end (for RR)
    public void reinsert(Process p) {
        processes.offer(p);
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return processes.isEmpty();
    }
}
