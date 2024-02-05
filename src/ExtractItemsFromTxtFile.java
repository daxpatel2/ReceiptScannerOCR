import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class ExtractItemsFromTxtFile {

    private final File itemsTxtFilePath;
    private final SQLAggregator sqlConnection;

    public ExtractItemsFromTxtFile(String itemsTxtFilePath){
        this.itemsTxtFilePath = new File(itemsTxtFilePath);
        this.sqlConnection = new SQLAggregator("jdbc:postgresql://localhost/RECIPT_ORC","postgres","0000");
    }

    public void addItemsToDB() throws IOException, RuntimeException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(itemsTxtFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String sqlSmnt = String.format("INSERT into pantry (items) VALUES ('%s')",line);
                sqlConnection.updateDeleteInsertQuery(sqlSmnt);
                System.out.println("Added: " + line + " to DB");
            }
        } catch (IOException io) {
            System.err.println("IOException occurred: "+io);
            throw new IOException(io);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void createDBTxtFile() {
        try {
            File fileObj = new File("/Users/daxpatel/Desktop/PythonReceipt/itemsFromDB.txt");
            if (fileObj.createNewFile()) {
                System.out.println("File created: " + fileObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    public void writeToItemsFromDBFile() {
        createDBTxtFile();
        try {
            FileWriter myWriter = new FileWriter("/Users/daxpatel/Desktop/PythonReceipt/itemsFromDB.txt");
            List<String> queryResult = sqlConnection.getDatabaseValues("SELECT * FROM pantry", 1);
            for (String item : queryResult) {
                myWriter.write(item + "\n");
            }
            myWriter.close();
            System.out.println("done");
        } catch (IOException | SQLException e) {
            System.out.println("An error occurred: " + e);
        }
    }

}
