import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

/**
 * DataQualityReport.java
 *
 * Replicates the Python data quality checks:
 *   1. Missing values per column
 *   2. Negative sales check
 *   3. Invalid release dates
 *   4. Duplicate records
 *
 * Usage:
 *   javac DataQualityReport.java
 *   java DataQualityReport data.csv
 *   java DataQualityReport             (defaults to "data.csv")
 */
public class DataQualityReport {

    // ── Sales columns to check ──────────────────────────────────────────────
    private static final List<String> SALES_COLUMNS = Arrays.asList(
        "total_sales", "na_sales", "jp_sales", "pal_sales", "other_sales"
    );

    // ── Date formats to try when parsing release_date ───────────────────────
    private static final String[] DATE_FORMATS = {
        "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy",
        "yyyy/MM/dd", "dd-MM-yyyy", "MMM dd, yyyy"
    };

    // ── ANSI colour helpers ─────────────────────────────────────────────────
    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";

    // ── Main ────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        String csvFile = (args.length > 0) ? args[0] : "data.csv";

        System.out.println(BOLD + CYAN);
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     DATA QUALITY REPORT          ║");
        System.out.println("╚══════════════════════════════════╝" + RESET);
        System.out.println("  File: " + csvFile + "\n");

        // ── Parse CSV ───────────────────────────────────────────────────────
        List<String[]> rawRows = readCsv(csvFile);
        if (rawRows.isEmpty()) {
            System.out.println(RED + "  [ERROR] File is empty or not found." + RESET);
            return;
        }

        String[] headers = rawRows.get(0);
        List<String[]> dataRows = rawRows.subList(1, rawRows.size());
        int totalRows = dataRows.size();
        int totalCols = headers.length;

        System.out.printf("  Rows: %d  |  Columns: %d%n%n", totalRows, totalCols);

        // Build column-index map
        Map<String, Integer> colIndex = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            colIndex.put(headers[i].trim().toLowerCase(), i);
        }

        // ── 1. Missing Values ────────────────────────────────────────────────
        section("1. MISSING VALUES");
        System.out.printf("  %-30s  %8s  %6s%n", "Column", "Missing", "% Null");
        System.out.println("  " + "─".repeat(50));

        int totalMissing = 0;
        for (Map.Entry<String, Integer> entry : colIndex.entrySet()) {
            String col = entry.getKey();
            int idx    = entry.getValue();
            long missing = dataRows.stream()
                .filter(r -> idx >= r.length || r[idx] == null || r[idx].trim().isEmpty())
                .count();
            totalMissing += missing;
            double pct = totalRows > 0 ? (missing * 100.0 / totalRows) : 0;
            String color = missing == 0 ? GREEN : (pct > 20 ? RED : YELLOW);
            System.out.printf("  %-30s  %s%8d  %5.1f%%%s%n", col, color, missing, pct, RESET);
        }
        System.out.println("  " + "─".repeat(50));
        System.out.printf("  %-30s  %8d%n%n", "TOTAL", totalMissing);

        // ── 2. Negative Sales Check ──────────────────────────────────────────
        section("2. NEGATIVE SALES CHECK");
        System.out.printf("  %-20s  %12s  %10s%n", "Column", "Negative #", "Status");
        System.out.println("  " + "─".repeat(47));

        for (String salesCol : SALES_COLUMNS) {
            if (!colIndex.containsKey(salesCol)) {
                System.out.printf("  %-20s  %12s  %s%n",
                    salesCol, "—", YELLOW + "Column not found" + RESET);
                continue;
            }
            int idx = colIndex.get(salesCol);
            long negCount = dataRows.stream().filter(r -> {
                if (idx >= r.length || r[idx] == null || r[idx].trim().isEmpty()) return false;
                try { return Double.parseDouble(r[idx].trim()) < 0; }
                catch (NumberFormatException e) { return false; }
            }).count();
            String color  = negCount == 0 ? GREEN : RED;
            String status = negCount == 0 ? "✓ Clean" : "✗ Issues found";
            System.out.printf("  %-20s  %s%12d  %s%s%n",
                salesCol, color, negCount, status, RESET);
        }
        System.out.println();

        // ── 3. Invalid Release Dates ─────────────────────────────────────────
        section("3. INVALID RELEASE DATES");
        if (!colIndex.containsKey("release_date")) {
            System.out.println("  " + YELLOW + "No 'release_date' column found." + RESET + "\n");
        } else {
            int idx = colIndex.get("release_date");
            long invalid = dataRows.stream().filter(r -> {
                if (idx >= r.length || r[idx] == null || r[idx].trim().isEmpty()) return true;
                return !parseDate(r[idx].trim());
            }).count();
            double pct = totalRows > 0 ? (invalid * 100.0 / totalRows) : 0;
            String color = invalid == 0 ? GREEN : RED;
            System.out.printf("  Total rows:          %d%n", totalRows);
            System.out.printf("  Invalid dates:       %s%d%s%n", color, invalid, RESET);
            System.out.printf("  Invalid rate:        %s%.1f%%%s%n%n", color, pct, RESET);
        }

        // ── 4. Duplicate Records ─────────────────────────────────────────────
        section("4. DUPLICATE RECORDS");
        Set<String> seen = new HashSet<>();
        long duplicates = 0;
        for (String[] row : dataRows) {
            String key = Arrays.toString(row);
            if (!seen.add(key)) duplicates++;
        }
        long unique = totalRows - duplicates;
        String dupColor = duplicates == 0 ? GREEN : YELLOW;
        System.out.printf("  Total rows:          %d%n", totalRows);
        System.out.printf("  Duplicate records:   %s%d%s%n", dupColor, duplicates, RESET);
        System.out.printf("  Unique rows:         %s%d%s%n%n", GREEN, unique, RESET);

        // ── Footer ───────────────────────────────────────────────────────────
        System.out.println(BOLD + CYAN + "═".repeat(38));
        System.out.println("  END OF REPORT");
        System.out.println("═".repeat(38) + RESET);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /** Prints a bold section heading. */
    private static void section(String title) {
        System.out.println(BOLD + "  ▸ " + title + RESET);
        System.out.println();
    }

    /** Attempts to parse a date string with several common formats. */
    private static boolean parseDate(String value) {
        for (String fmt : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
                sdf.setLenient(false);
                sdf.parse(value);
                return true;
            } catch (ParseException ignored) {}
        }
        return false;
    }

    /**
     * Minimal RFC-4180-compatible CSV parser.
     * Handles quoted fields (including embedded commas and newlines).
     */
    private static List<String[]> readCsv(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return rows;

        String content = new String(Files.readAllBytes(path));
        List<String> lines = splitLines(content);
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    /** Splits raw content into logical CSV lines (handles quoted newlines). */
    private static List<String> splitLines(String content) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        for (char c : content.toCharArray()) {
            if (c == '"') inQuote = !inQuote;
            if ((c == '\n') && !inQuote) {
                lines.add(sb.toString().replaceAll("\r$", ""));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) lines.add(sb.toString());
        return lines;
    }

    /** Parses a single CSV line into fields, respecting quoted values. */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++; // escaped quote
                } else {
                    inQuote = !inQuote;
                }
            } else if (c == ',' && !inQuote) {
                fields.add(sb.toString()); sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}