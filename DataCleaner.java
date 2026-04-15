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

   private static String fixName(String raw) {
        if (raw.isEmpty()) return "";

        // Разделяем слитные ИмяФамилия по заглавным буквам
        String withSpaces = raw.replaceAll("([а-яёa-z])([А-ЯЁA-Z])", "$1 $2");
        // Приводим к формату: первая буква заглавная, остальные строчные
        String[] words = withSpaces.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                String formatted = Character.toUpperCase(word.charAt(0)) +
                        word.substring(1).toLowerCase();
                result.append(formatted).append(" ");
            }
        }
        return result.toString().trim();
    }

    private static String fixAge(String raw) {
        if (raw.isEmpty()) return "";
        // Убираем всё кроме цифр и минуса
        String cleaned = raw.replaceAll("[^\\d-]", "");
        try {
            int age = Integer.parseInt(cleaned);
            age = Math.abs(age); // убираем минус
            if (age < 0 || age > 150) return "";
            return String.valueOf(age);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private static String fixPhone(String raw) {
        if (raw.isEmpty()) return "";
        // Оставляем только цифры и плюс в начале
        String cleaned = raw.replaceAll("[^\\d+]", "");
        // Убираем все плюсы кроме первого
        if (cleaned.startsWith("+")) {
            cleaned = "+" + cleaned.replaceAll("\\+", "");
        } else {
            cleaned = cleaned.replaceAll("\\+", "");
        }

        // Пытаемся привести к формату +X (XXX) XXX-XX-XX
        Pattern p = Pattern.compile("^(\\+?\\d{1,3})?(\\d{3})(\\d{3})(\\d{2})(\\d{2})$");
        Matcher m = p.matcher(cleaned.replaceAll("\\D", ""));

        if (m.find()) {
            String country = m.group(1) != null ? m.group(1) : "+7";
            if (!country.startsWith("+")) country = "+" + country;
            return String.format("%s (%s) %s-%s-%s",
                    country, m.group(2), m.group(3), m.group(4), m.group(5));
        }

        // Если не удалось распарсить — возвращаем как есть, но очищенное
        return cleaned.isEmpty() ? "" : cleaned;
    }
    
}
