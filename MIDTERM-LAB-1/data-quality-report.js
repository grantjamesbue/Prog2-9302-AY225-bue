const fs = require("fs");

// ── Config ──────────────────────────────────────────────────────────────────
const SALES_COLUMNS = ["total_sales", "na_sales", "jp_sales", "pal_sales", "other_sales"];
const DATE_FORMATS  = [
  /^\d{4}-\d{2}-\d{2}$/,
  /^\d{2}\/\d{2}\/\d{4}$/,
  /^\d{2}-\d{2}-\d{4}$/,
  /^\w+ \d{2}, \d{4}$/,
];

// ── ANSI colours ─────────────────────────────────────────────────────────────
const R = "\x1b[0m";
const GREEN  = "\x1b[32m";
const YELLOW = "\x1b[33m";
const RED    = "\x1b[31m";
const CYAN   = "\x1b[36m";
const BOLD   = "\x1b[1m";

// ── Helpers ──────────────────────────────────────────────────────────────────
function section(title) {
  console.log(`\n${BOLD}  ▸ ${title}${R}\n`);
}

function isValidDate(value) {
  if (!value || value.trim() === "") return false;
  const d = new Date(value.trim());
  if (!isNaN(d.getTime())) return true;
  return DATE_FORMATS.some(re => re.test(value.trim()));
}

function parseCsv(content) {
  const lines = content.split(/\r?\n/).filter(l => l.trim() !== "");
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
}

// ── Main ─────────────────────────────────────────────────────────────────────
const csvFile = process.argv[2] || "data.csv";

if (!fs.existsSync(csvFile)) {
  console.error(`${RED}[ERROR] File not found: ${csvFile}${R}`);
  process.exit(1);
}

const { headers, rows } = parseCsv(fs.readFileSync(csvFile, "utf8"));
const n = rows.length;

console.log(`${BOLD}${CYAN}`);
console.log("╔══════════════════════════════════╗");
console.log("║     DATA QUALITY REPORT          ║");
console.log(`╚══════════════════════════════════╝${R}`);
console.log(`  File: ${csvFile}`);
console.log(`  Rows: ${n}  |  Columns: ${headers.length}`);

// ── 1. Missing Values ─────────────────────────────────────────────────────
section("1. MISSING VALUES");
console.log(`  ${"Column".padEnd(30)}  ${"Missing".padStart(8)}  ${"% Null".padStart(6)}`);
console.log("  " + "─".repeat(50));

let totalMissing = 0;
headers.forEach(col => {
  const count = rows.filter(r => r[col] === undefined || r[col] === "").length;
  totalMissing += count;
  const pct = n > 0 ? ((count / n) * 100).toFixed(1) : "0.0";
  const color = count === 0 ? GREEN : parseFloat(pct) > 20 ? RED : YELLOW;
  console.log(`  ${col.padEnd(30)}  ${color}${String(count).padStart(8)}  ${String(pct + "%").padStart(6)}${R}`);
});
console.log("  " + "─".repeat(50));
console.log(`  ${"TOTAL".padEnd(30)}  ${String(totalMissing).padStart(8)}\n`);

// ── 2. Negative Sales Check ───────────────────────────────────────────────
section("2. NEGATIVE SALES CHECK");
console.log(`  ${"Column".padEnd(20)}  ${"Negative #".padStart(12)}  Status`);
console.log("  " + "─".repeat(47));

SALES_COLUMNS.forEach(col => {
  if (!headers.includes(col)) {
    console.log(`  ${col.padEnd(20)}  ${"—".padStart(12)}  ${YELLOW}Column not found${R}`);
    return;
  }
  const neg = rows.filter(r => {
    const v = parseFloat(r[col]);
    return !isNaN(v) && v < 0;
  }).length;
  const color  = neg === 0 ? GREEN : RED;
  const status = neg === 0 ? "✓ Clean" : "✗ Issues found";
  console.log(`  ${col.padEnd(20)}  ${color}${String(neg).padStart(12)}  ${status}${R}`);
});

// ── 3. Invalid Release Dates ──────────────────────────────────────────────
section("3. INVALID RELEASE DATES");
if (!headers.includes("release_date")) {
  console.log(`  ${YELLOW}No 'release_date' column found.${R}\n`);
} else {
  const invalid = rows.filter(r => !isValidDate(r["release_date"])).length;
  const pct = n > 0 ? ((invalid / n) * 100).toFixed(1) : "0.0";
  const color = invalid === 0 ? GREEN : RED;
  console.log(`  Total rows:          ${n}`);
  console.log(`  Invalid dates:       ${color}${invalid}${R}`);
  console.log(`  Invalid rate:        ${color}${pct}%${R}\n`);
}

// ── 4. Duplicate Records ──────────────────────────────────────────────────
section("4. DUPLICATE RECORDS");
const seen = new Set();
let duplicates = 0;
rows.forEach(r => {
  const key = JSON.stringify(r);
  seen.has(key) ? duplicates++ : seen.add(key);
});
const unique = n - duplicates;
const dupColor = duplicates === 0 ? GREEN : YELLOW;
console.log(`  Total rows:          ${n}`);
console.log(`  Duplicate records:   ${dupColor}${duplicates}${R}`);
console.log(`  Unique rows:         ${GREEN}${unique}${R}\n`);

console.log(`${BOLD}${CYAN}${"═".repeat(38)}`);
console.log("  END OF REPORT");
console.log(`${"═".repeat(38)}${R}`);