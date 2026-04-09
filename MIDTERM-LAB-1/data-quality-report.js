const fs = require("fs");
const readline = require("readline");

// ── Config ──────────────────────────────────────────────────────────────────
const SALES_COLUMNS = ["total_sales", "na_sales", "jp_sales", "pal_sales", "other_sales"];
const DATE_FORMATS = [/^\d{4}-\d{2}-\d{2}$/, /^\d{2}\/\d{2}\/\d{4}$/, /^\d{2}-\d{2}-\d{4}$/, /^\w+ \d{2}, \d{4}$/];

const R = "\x1b[0m", GREEN = "\x1b[32m", YELLOW = "\x1b[33m", RED = "\x1b[31m", CYAN = "\x1b[36m", BOLD = "\x1b[1m";

// ── 7. Modular Design (Helper Functions) ─────────────────────────────────────
function section(title) {
    console.log(`\n${BOLD}  ▸ ${title}${R}\n`);
}

function isValidDate(value) {
    if (!value || value.trim() === "") return false;
    const d = new Date(value.trim());
    return !isNaN(d.getTime()) || DATE_FORMATS.some(re => re.test(value.trim()));
}

function parseCsv(content) {
    // 8. Try-Catch for Format Validation
    try {
        const lines = content.split(/\r?\n/).filter(l => l.trim() !== "");
        if (lines.length === 0) throw new Error("File is empty.");

        const parse = line => {
            const fields = [];
            let cur = "", inQ = false;
            for (let i = 0; i < line.length; i++) {
                const c = line[i];
                if (c === '"') {
                    if (inQ && line[i + 1] === '"') { cur += '"'; i++; }
                    else inQ = !inQ;
                } else if (c === "," && !inQ) { fields.push(cur); cur = ""; }
                else cur += c;
            }
            fields.push(cur);
            return fields;
        };

        const headers = parse(lines[0]).map(h => h.trim().toLowerCase());
        const rows = lines.slice(1).map(l => {
            const vals = parse(l);
            const obj = {};
            headers.forEach((h, i) => { obj[h] = vals[i] !== undefined ? vals[i].trim() : ""; });
            return obj;
        });
        return { headers, rows };
    } catch (e) {
        throw new Error("Invalid CSV format: " + e.message);
    }
}

// ── 1 & 3. Input Loop & Validation ───────────────────────────────────────────
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

function askForFile() {
    rl.question(`${BOLD}Enter dataset file path:${R} `, (filePath) => {
        try {
            // 2. Validate existence, readability, and format
            if (!fs.existsSync(filePath)) {
                throw new Error("File does not exist.");
            }
            
            // Check readability
            fs.accessSync(filePath, fs.constants.R_OK);

            const content = fs.readFileSync(filePath, "utf8");
            const { headers, rows } = parseCsv(content);

            runReport(filePath, headers, rows);
            rl.close();
        } catch (err) {
            // 3. Display appropriate error and loop
            console.log(`${RED}[ERROR] ${err.message}${R}`);
            askForFile(); 
        }
    });
}

// ── 9. Formatted Output ──────────────────────────────────────────────────────
function runReport(csvFile, headers, rows) {
    const n = rows.length;
    console.log(`\n${BOLD}${CYAN}╔══════════════════════════════════╗`);
    console.log("║    DATA QUALITY REPORT           ║");
    console.log(`╚══════════════════════════════════╝${R}`);
    console.log(`  File: ${csvFile}`);
    console.log(`  Rows: ${n}  |  Columns: ${headers.length}`);

    // Section 1: Missing Values
    section("1. MISSING VALUES");
    console.log(`  ${"Column".padEnd(30)}  ${"Missing".padStart(8)}  ${"% Null".padStart(6)}`);
    console.log("  " + "─".repeat(50));
    headers.forEach(col => {
        const count = rows.filter(r => !r[col]).length;
        const pct = n > 0 ? ((count / n) * 100).toFixed(1) : "0.0";
        const color = count === 0 ? GREEN : parseFloat(pct) > 20 ? RED : YELLOW;
        console.log(`  ${col.padEnd(30)}  ${color}${String(count).padStart(8)}  ${String(pct + "%").padStart(6)}${R}`);
    });

    // Section 2: Negative Sales
    section("2. NEGATIVE SALES CHECK");
    SALES_COLUMNS.forEach(col => {
        if (!headers.includes(col)) return;
        const neg = rows.filter(r => parseFloat(r[col]) < 0).length;
        console.log(`  ${col.padEnd(20)}  ${neg === 0 ? GREEN : RED}${String(neg).padStart(12)}  ${neg === 0 ? "✓ Clean" : "✗ Issues"}${R}`);
    });

    console.log(`\n${BOLD}${CYAN}${"═".repeat(38)}\n  END OF REPORT\n${"═".repeat(38)}${R}`);
}

// Start the program
askForFile();