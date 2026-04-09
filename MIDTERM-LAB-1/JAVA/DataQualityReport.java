import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

public class DataQualityReport {

    private static final List<String> SALES_COLUMNS = Arrays.asList(
        "total_sales", "na_sales", "jp_sales", "pal_sales", "other_sales"
    );

    private static final String[] DATE_FORMATS = {
        "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd"
    };

    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path filePath = null;
        List<DataRecord> records = new ArrayList<>();
        String[] headers = null;

        while (true) {
            System.out.println(BOLD + "Enter the FULL FILE PATH of the CSV dataset:" + RESET);
            System.out.print("> ");
            String inputPath = scanner.nextLine().replace("\"", "");
            filePath = Paths.get(inputPath);

            if (!Files.exists(filePath)) {
                System.out.println(RED + "[ERROR] File does not exist." + RESET);
            } else if (!Files.isReadable(filePath)) {
                System.out.println(RED + "[ERROR] File is not readable." + RESET);
            } else if (!inputPath.toLowerCase().endsWith(".csv")) {
                System.out.println(RED + "[ERROR] Must be a .csv file." + RESET);
            } else {
                try {
                    List<DataRecord> tempRecords = loadCSV(filePath);
                    if (tempRecords.isEmpty()) {
                        System.out.println(RED + "[ERROR] The file is empty." + RESET);
                        continue;
                    }
                    headers = tempRecords.get(0).getFields();
                    for (int i = 1; i < tempRecords.size(); i++) {
                        records.add(tempRecords.get(i));
                    }
                    break; 
                } catch (IOException e) {
                    System.out.println(RED + "[ERROR] Failed to read file: " + e.getMessage() + RESET);
                }
            }
        }

        runReport(filePath.getFileName().toString(), headers, records);
        scanner.close(); 
    }

    private static List<DataRecord> loadCSV(Path path) throws IOException {
        List<DataRecord> records = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(new DataRecord(parseCsvLine(line)));
                }
            }
        }
        return records;
    }

    private static void runReport(String fileName, String[] headers, List<DataRecord> records) {
        int totalRows = records.size();
        Map<String, Integer> colMap = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            colMap.put(headers[i].trim().toLowerCase(), i);
        }

        System.out.println(BOLD + CYAN + "\n╔══════════════════════════════════╗");
        System.out.println("║     DATA QUALITY REPORT          ║");
        System.out.println("╚══════════════════════════════════╝" + RESET);
        System.out.println(" File: " + fileName + " | Rows: " + totalRows + "\n");

        System.out.println(BOLD + " ▸ 1. MISSING VALUES" + RESET);
        for (String col : colMap.keySet()) {
            int idx = colMap.get(col);
            long missing = 0;
            for(DataRecord r : records) { if(r.getField(idx).isEmpty()) missing++; }
            double pct = (missing * 100.0 / totalRows);
            String color = missing == 0 ? GREEN : (pct > 20 ? RED : YELLOW);
            System.out.printf(" %-30s %s%8d %7.1f%%%s\n", col, color, missing, pct, RESET);
        }

        System.out.println("\n" + BOLD + " ▸ 2. NEGATIVE SALES CHECK" + RESET);
        for (String saleCol : SALES_COLUMNS) {
            if (colMap.containsKey(saleCol)) {
                int idx = colMap.get(saleCol);
                long negatives = 0;
                for(DataRecord r : records) {
                    try { if(Double.parseDouble(r.getField(idx)) < 0) negatives++; } catch(Exception e){}
                }
                System.out.printf(" %-20s %8d  %s%s\n", saleCol, negatives, (negatives == 0 ? GREEN + "✓ Clean" : RED + "✗ Issues"), RESET);
            }
        }

        System.out.println("\n" + BOLD + " ▸ 3. DUPLICATE CHECK" + RESET);
        Set<String> uniqueCheck = new HashSet<>();
        long dups = 0;
        for (DataRecord r : records) { if (!uniqueCheck.add(r.toString())) dups++; }
        System.out.println(" Duplicate Rows: " + (dups > 0 ? RED : GREEN) + dups + RESET);

        System.out.println("\n" + BOLD + " ▸ 4. DATE VALIDATION" + RESET);
        if (colMap.containsKey("release_date")) {
            int dIdx = colMap.get("release_date");
            long invalid = 0;
            for(DataRecord r : records) {
                String val = r.getField(dIdx);
                boolean valid = false;
                if (!val.isEmpty()) {
                    for (String fmt : DATE_FORMATS) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                            sdf.setLenient(false);
                            sdf.parse(val);
                            valid = true; break;
                        } catch (Exception e) {}
                    }
                }
                if(!valid) invalid++;
            }
            System.out.println(" Invalid Dates: " + (invalid == 0 ? GREEN : RED) + invalid + RESET);
        }

        System.out.println("\n" + BOLD + CYAN + "══════════════════════════════════════");
        System.out.println(" END OF REPORT");
        System.out.println("══════════════════════════════════════" + RESET);
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) { fields.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}