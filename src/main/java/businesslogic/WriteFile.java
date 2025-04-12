package businesslogic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {

    private static final String FILE_NAME = "simulation_logs.txt";

    public static void writeSimulationLogs(String logs) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write(logs);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error at writing in file");
        }
    }

}
