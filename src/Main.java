
public class Main {
    public static void main(String[] args) throws Exception {

        //fetch info from receipt
        ReceiptFetcherAPI receiptFetcherAPI = new ReceiptFetcherAPI("/Users/daxpatel/Desktop/Java Projects/testForRecipt/src/imggg.jpeg");
        receiptFetcherAPI.createTxtFileFromJson();

        //fetch ai generated files from python
        PythonExecutionClass pyExec1 = new PythonExecutionClass("/Users/daxpatel/opt/anaconda3/bin/python3","/Users/daxpatel/Desktop/PythonReceipt/openai_test_api.py");

        //add items from receipt to postgresql
        ExtractItemsFromTxtFile extractItemsFromTxtFile = new ExtractItemsFromTxtFile("/Users/daxpatel/Desktop/PythonReceipt/items.txt");
        extractItemsFromTxtFile.addItemsToDB();
        //fetch all items in pantry -> txt file
        extractItemsFromTxtFile.writeToItemsFromDBFile();
        
        //integrate this for the nutrition info
        //https://developer.edamam.com/edamam-nutrition-api-demo

    }

}