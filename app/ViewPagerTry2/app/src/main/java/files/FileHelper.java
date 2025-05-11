package files;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {


    // Method to create and write to a file
    public static void writeToFile(Context context, String data,String fileName) {
        try {
            // Check if the file exists
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) {
                // Create the file
                file.createNewFile();
            }

            // Write to the file
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to read from a file
    public static String readFromFile(Context context,String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            char[] buffer = new char[1024];
            int read;
            while ((read = inputStreamReader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, read);
            }
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}

