import java.io.*;
import java.util.*;

public class MLQScheduler {
    private List<Process> processes;
    private QueueLevel queue1;
    private QueueLevel queue2;
    private QueueLevel queue3;
    private List<Process> completedProcess;
    private int time;

    public MLQScheduler() {
        processes = new ArrayList<>();
        queue1 = new QueueLevel("RR", 3);
        queue2 = new QueueLevel("RR", 5);
        queue3 = new QueueLevel("FCFS", 0);
        completedProcess = new ArrayList<>();
        time = 0;
    }

    // Load processes from a file and creates a new process
    public void loadProcesses(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            String[] parts = line.split(";");
            String label = parts[0].trim();
            int bt = Integer.parseInt(parts[1].trim());
            int at = Integer.parseInt(parts[2].trim());
            int queue = Integer.parseInt(parts[3].trim());
            int priority = Integer.parseInt(parts[4].trim());

            //Creates process with the input parameters of each line
            Process p = new Process(label, bt, at, queue, priority);
            processes.add(p);

            //verifies a process belongs to which queue
            switch (queue) {
                case 1 -> queue1.addProcess(p);
                case 2 -> queue2.addProcess(p);
                case 3 -> queue3.addProcess(p);
            }
        }
        br.close();
    }

    // -Algorithm Round Robin -
    private void executeRR(QueueLevel queue) {
        //amount of quantum time
        int qt = queue.quantum;

        Process p = queue.getNext();

        //if first time in queue, start time is the accumulated time in attribute time and response time is startTime - arrive time
        if (p.startTime == null) {
            p.startTime = time;
            p.responseTime = time - p.at;
        }
        // if the burst time is less than the quantum, then complete and not entering again in the process queue, add the bt to global time
        if (p.remainingBT <= qt) {
            time += p.remainingBT;
            p.remainingBT = 0;
        } else {

            //remains time for  next turn at queue, adds bt used to global time
            p.remainingBT -= qt;
            time += qt;
        }

        //if theres still bt left, add process again to the queue
        if (p.remainingBT > 0) {
            queue.reinsert(p);
        } else {
            //bt cero, completed process, pass to the list of completed process
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.at;
            p.waitingTime = p.turnaroundTime - p.bt;
            completedProcess.add(p);
        }
    }

    // - Algorithm  First Come First Serve -
    private void executeFCFS(QueueLevel queue) {
        Process p = queue.getNext();
        if (p.startTime == null) {
            p.startTime = time;
            p.responseTime = time - p.at;
        }

        time += p.remainingBT;
        p.remainingBT = 0;
        p.completionTime = time;
        p.turnaroundTime = p.completionTime - p.at;
        p.waitingTime = p.turnaroundTime - p.bt;
        completedProcess.add(p);
    }

    // ----------------- MLQ Execution -----------------

    public void executeMLQ() {

    //while before queue is not empty, does not pass to the next queue
        while (!queue1.isEmpty() || !queue2.isEmpty() || !queue3.isEmpty()) {
            boolean executed = false;

            if (!queue1.isEmpty()) {
                executeRR(queue1);
                executed = true;
            } else if (!queue2.isEmpty()) {
                executeRR(queue2);
                executed = true;
            } else if (!queue3.isEmpty()) {
                executeFCFS(queue3);
                executed = true;
            }

            if (!executed) time++;
        }
    }

    // Generate report
    public void generateReport(String outputFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        bw.write("# label; BT; AT; Q; Pr; WT; CT; RT; TAT\n");

        double totalWT = 0, totalCT = 0, totalRT = 0, totalTAT = 0;
        int n = completedProcess.size();

        for (Process p : completedProcess) {
            bw.write(String.format("%s;%d;%d;%d;%d;%d;%d;%d;%d\n",
                    p.label, p.bt, p.at, p.queue, p.priority,
                    p.waitingTime, p.completionTime, p.responseTime, p.turnaroundTime));
            totalWT += p.waitingTime;
            totalCT += p.completionTime;
            totalRT += p.responseTime;
            totalTAT += p.turnaroundTime;
        }

        bw.write(String.format("WT=%.1f; CT=%.1f; RT=%.1f; TAT=%.1f;\n",
                totalWT/n, totalCT/n, totalRT/n, totalTAT/n));

        bw.close();
    }
}
