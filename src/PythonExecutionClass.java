import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PythonExecutionClass {
    String pythonPath;
    String scriptPath;

    public PythonExecutionClass(String pythonPath, String scriptPath) throws Exception {
        this.pythonPath = pythonPath;
        this.scriptPath = scriptPath;
    }

    /**
     * Starts and executes an external process, combining a Python script with a given path.
     * This method launches an external process using the specified Python script and path.
     * It then waits for the process to complete and checks its exit code to ensure it
     * completed successfully (exit code 0). If the process exits abnormally, an exception
     * is thrown otherwise an instance of the process is returned.
     *
     * @return The process that was started and executed.
     * @throws Exception If an error occurs while starting or executing the process,
     *         or if the process exits abnormally with a non-zero exit code.
     */
    private Process startExecProcess() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath,scriptPath);
        processBuilder.redirectErrorStream(true);
        Process p = processBuilder.start();

        // Check exit value for errors
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new Exception("Process exited abnormally with code " + exitCode);
        }

        return p;
    }

    /**
     * Reads and returns the first line of standard output from an executed process.
     * This method starts an external process, reads its standard output, and returns
     * the first line of the output as a string. It also closes the input streams once
     * it has read the data.
     *
     * @return The first line of standard output from the executed process as a string.
     * @throws Exception If an error occurs while executing the process or reading the output.
     */
    public String readProcessInput() throws Exception {

        Process p = startExecProcess();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String line = stdInput.readLine();

        if (line != null) {
            line = new String(line.getBytes(), StandardCharsets.UTF_8);
        }

        stdInput.close();
        stdError.close();
        return line;
    }

}