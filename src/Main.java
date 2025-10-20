import java.io.File;

public class Main {
    public static void main(String[] args) {
        String inputFolder = "inputs";
        String outputFolder = "outputs";
        new File(inputFolder).mkdirs(); // creates inputs folder if not created yet
        new File(outputFolder).mkdirs(); // folder with the output files

        //Gets the input files with the  process and parameters
        File[] files = new File(inputFolder).listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No files found in " + inputFolder);
            return;
        }
        //iterates for each case file and creates a new MLQ Scheduler to handle the list of process
        for (File file : files) {
            try {
                System.out.println("Processing: " + file.getName());

                MLQScheduler scheduler = new MLQScheduler();
                scheduler.loadProcesses(file.getPath());
                scheduler.executeMLQ();

                String outputFile = outputFolder + File.separator + file.getName().replace(".txt", "_output.txt");
                scheduler.generateReport(outputFile);

            } catch (Exception e) {
                System.out.println("Error processing file: " + file.getName());
                e.printStackTrace();
            }
        }

        System.out.println("All files processed.");
    }
}
