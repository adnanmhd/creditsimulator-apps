package creditsimulator.apps.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileUtil {

    public static List<Map<String, String>> readData(String fileName) throws IOException {

        List<Map<String, String>> listEachLine = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String headerLine = br.readLine(); // read first line as header
            if (headerLine == null) {
                throw new IOException("Empty file: " + fileName);
            }

            List<String> header = Arrays.stream(headerLine.split("\\|"))
                    .map(String::trim)
                    .toList();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\|");

                Map<String, String> eachLineMap = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    eachLineMap.put(header.get(i), values[i].trim());
                }
                listEachLine.add(eachLineMap);
            }
        }
        return listEachLine;
    }
}
