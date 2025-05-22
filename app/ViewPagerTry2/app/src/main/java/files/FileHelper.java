
package files;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * This class provides utility methods for reading from and writing to internal storage files
 * within the application's private directory.
 */
public class FileHelper {

    /**
     * Creates a new file or overwrites an existing file with the provided data.
     * The file is stored in the application's internal storage, accessible only to this app.
     *
     * @param context  The application context.
     * @param data     The string data to write to the file.
     * @param fileName The name of the file to create or write to.
     */
    public static void writeToFile(Context context, String data, String fileName) {
        try {
            // Get the File object representing the file in the app's internal storage.
            File file = new File(context.getFilesDir(), fileName);

            // Check if the file doesn't exist and create it if necessary.
            if (!file.exists()) {
                file.createNewFile();
            }

            // Open a FileOutputStream to write data to the file in private mode.
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            // Use OutputStreamWriter to write characters to the output stream.
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            // Write the provided data to the file.
            outputStreamWriter.write(data);
            // Close the writer to ensure all data is flushed to the file.
            outputStreamWriter.close();
        } catch (Exception e) {
            // Print the stack trace if any exception occurs during file writing.
            e.printStackTrace();
        }
    }

    /**
     * Reads the content of a file from the application's internal storage.
     *
     * @param context  The application context.
     * @param fileName The name of the file to read from.
     * @return The content of the file as a String, or an empty string if an error occurs.
     */
    public static String readFromFile(Context context, String fileName) {
        // Use StringBuilder for efficient string concatenation in a loop.
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Open a FileInputStream to read data from the file.
            FileInputStream fis = context.openFileInput(fileName);
            // Use InputStreamReader to read characters from the input stream.
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            // Create a buffer to hold chunks of data read from the file.
            char[] buffer = new char[1024];
            int read;
            // Read from the file into the buffer until the end of the file is reached (-1).
            while ((read = inputStreamReader.read(buffer)) != -1) {
                // Append the read portion of the buffer to the StringBuilder.
                stringBuilder.append(buffer, 0, read);
            }
            // Close the reader to release resources.
            inputStreamReader.close();
        } catch (Exception e) {
            // Print the stack trace if any exception occurs during file reading.
            e.printStackTrace();
        }
        // Return the accumulated string from the file.
        return stringBuilder.toString();
    }
}
