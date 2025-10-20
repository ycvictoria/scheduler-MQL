public class Process {
    String label;
    int bt, at, queue, priority;
    int remainingBT;
    Integer startTime = null;
    Integer completionTime = null;
    int waitingTime, turnaroundTime, responseTime;

    //A Process has label, burst time, arrival time, which queue belongs and priority (not used in this case)
    public Process(String label, int bt, int at, int queue, int priority) {
        this.label = label;
        this.bt = bt;
        this.at = at;
        this.queue = queue;
        this.priority = priority;
        this.remainingBT = bt;
    }
}

