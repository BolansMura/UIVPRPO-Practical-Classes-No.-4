import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class DataCleaner {

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(inputFile));
            List<String> correctedLines = new ArrayList<>();

            for (String line : lines) {
                String corrected = processLine(line);
                correctedLines.add(corrected);
            }

            Files.write(Paths.get(outputFile), correctedLines);
            System.out.println("Обработка завершена. Результат записан в " + outputFile);

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
        }
    }

    private static String processLine(String line) {
        String[] parts = line.split("\\|", -1); // -1 чтобы сохранить пустые поля
        if (parts.length < 4) {
            // Если полей меньше 4, дополняем пустыми
            parts = Arrays.copyOf(parts, 4);
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] == null) parts[i] = "";
            }
        }

        String name = fixName(parts[0].trim());
        String age = fixAge(parts[1].trim());
        String phone = fixPhone(parts[2].trim());
        String email = fixEmail(parts[3].trim());

        return String.join("|", name, age, phone, email);
    }

   
}
