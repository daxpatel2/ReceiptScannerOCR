import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class ReceiptFetcherAPI {
    private final File receiptImgPath;

    public ReceiptFetcherAPI(String receiptImgPath) {
        this.receiptImgPath = new File(receiptImgPath);
    }

    /**
     * Retrieves receipt information by making a network request to the specified API address.
     * This method sends a POST request to the API address with the provided parameters and a receipt image file.
     * @return A string containing the receipt information in JSON format.
     * @throws RuntimeException If an I/O exception occurs during the network request or response processing.
     */
    private String getReceiptInfo() throws IOException{
        try ( CloseableHttpClient client = HttpClients.createDefault()) {
            final String apiAddress = "https://ocr.asprise.com/api/v1/receipt";
            HttpPost post = new HttpPost(apiAddress);
                post.setEntity(MultipartEntityBuilder.create()
                        .addTextBody("api_key", "TEST")       // Use 'TEST' for testing purpose
                        .addTextBody("recognizer", "auto")       // can be 'US', 'CA', 'JP', 'SG' or 'auto'
                        .addTextBody("ref_no", "ocr_java_123'") // optional caller provided ref code
                        .addPart("file", new FileBody(receiptImgPath))    // the image file
                        .build());

                try (CloseableHttpResponse response = client.execute(post)) {
                    return EntityUtils.toString(response.getEntity());
                }
            } catch (IOException e) {
                throw new IOException(e);
        }
    }

    /**
     * Cleans and processes the JSON data obtained from the receipt information.
     * This method removes unnecessary fields and performs cleaning operations on the JSON data.
     *
     * @return A JSONObject containing the cleaned and processed receipt information.
     * @throws JSONException If there is an issue with processing the JSON data.
     * @throws RuntimeException If an I/O exception occurs during the cleaning process.
     */
    private JSONObject jsonCleaner() throws JSONException{
        try {
            JSONObject jsonObject = new JSONObject(getReceiptInfo());

            JSONArray receiptArrayFromJSON = jsonObject.getJSONArray("receipts");

            jsonObject.remove("image_height");
            jsonObject.remove("recognition_completed_on");
            jsonObject.remove("request_received_on");
            jsonObject.remove("file_name");
            jsonObject.remove("success");
            jsonObject.remove("ref_no");
            jsonObject.remove("ocr_java_123");
            jsonObject.remove("image_width");
            jsonObject.remove("request_id");
            jsonObject.remove("ocr_type");
            jsonObject.remove("image_rotation");

            for(int i=0; i<receiptArrayFromJSON.length(); i++) {

                JSONObject classObj = receiptArrayFromJSON.getJSONObject(i);

                classObj.remove("merchant_website");
                classObj.remove("merchant_tax_reg_no");
                classObj.remove("merchant_company_reg_no");
                classObj.remove("region");
                classObj.remove("mall");
                classObj.remove("service_charge");
                classObj.remove("tip");
                classObj.remove("payment_method");
                classObj.remove("payment_details");
                classObj.remove("credit_card_type");
                classObj.remove("credit_card_number");
                classObj.remove("source_locations");

                receiptArrayFromJSON.put(i, classObj);
            }

            return jsonObject;
        } catch (JSONException e) {
            throw new JSONException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a text file from a JSON object obtained by the `jsonCleaner` method.
     * The JSON content is written to the file specified by the 'jsonStringFromReceipt' file path.
     * If the file already exists, it will be overwritten.
     *
     * @throws IOException if there is an IO exception during file creation or writing.
     * @throws JSONException if the `jsonCleaner` method throws a JSONException.
     *
     */
    public void createTxtFileFromJson() throws IOException, JSONException {

        JSONObject json;

        try {
            json = jsonCleaner();
        } catch (JSONException jsonException) {
            throw new JSONException("jsonCleaner method threw JSONException");
        }

        String filePath = "jsonStringFromReceipt.txt";

        try{
            File textFile = new File(filePath);
            boolean creationStatus = textFile.createNewFile() || textFile.exists();

            if(creationStatus) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                    writer.write(json.toString());

                } catch (IOException e) {
                    throw new IOException("IO Exception occurred while writing json to text file");
                }
            } else {
                System.out.println("jsonStringFromReceipt.txt was not created successfully!");
                throw new IOException("IO Exception occurred while trying to create text file 'jsonStringFromReceipt'");
            }

        } catch (IOException io) {
            throw new IOException("IO Exception occurred while trying to create text file 'jsonStringFromReceipt'");
        }
    }
}