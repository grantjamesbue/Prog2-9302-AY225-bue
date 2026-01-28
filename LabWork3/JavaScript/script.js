/* ================= REAL-TIME INPUT FIXERS ================= */
function fixAttendance(input) {
    let value = Number(input.value);
    if (isNaN(value)) { input.value = ""; return; }
    if (!Number.isInteger(value)) value = Math.floor(value); // Floor decimals
    if (value > 5) value = 5; // Clamp max
    if (value < 0) value = 0; // Clamp min
    input.value = value;
}

function fixLabInput(input) {
    let value = Number(input.value);
    if (isNaN(value)) { input.value = ""; return; }
    if (!Number.isInteger(value)) value = Math.floor(value); // Floor decimals
    if (value > 100) value = 100; // Clamp max
    if (value < 0) value = 0; // Clamp min
    input.value = value;
}

/* ================= COMPUTE GRADES ================= */
function computeGrades() {
    let attendanceRaw = Number(document.getElementById("attendance").value);
    const lab1 = Number(document.getElementById("lab1").value);
    const lab2 = Number(document.getElementById("lab2").value);
    const lab3 = Number(document.getElementById("lab3").value);
    const result = document.getElementById("result");

    if ([attendanceRaw, lab1, lab2, lab3].some(isNaN)) {
        result.innerHTML = `<span class="warning">⚠ Please enter all valid numbers!</span>`;
        return;
    }

    // Ensure attendance is clamped in case user bypassed fixAttendance
    if (attendanceRaw > 5) attendanceRaw = 5;
    if (attendanceRaw < 0) attendanceRaw = 0;

    const attendancePercent = attendanceRaw * 20; // 0–5 → 0–100%
    const labAverage = (lab1 + lab2 + lab3) / 3;
    const classStanding = (attendancePercent * 0.4) + (labAverage * 0.6);

    let output = `<span class="bold">--- RESULTS ---</span><br><br>`;
    output += `Attendance: ${attendanceRaw} / 5<br>`;
    output += `Attendance Percentage: ${attendancePercent}%<br><br>`;
    output += `Lab Average: ${labAverage.toFixed(2)}<br>`;
    output += `<span class="bold">Class Standing:</span> ${classStanding.toFixed(2)}<br><br>`;

    // ===== Attendance logic =====
    if (attendanceRaw === 1) {
        output += `<span class="warning">⚠ FAILED DUE TO LOW ATTENDANCE (Only 1 day attended)</span><br><br>`;
        output += `<span class="bold">Remark:</span> FAILED<br><br>`;
        output += `<span class="bold">Required Prelim Exam for 75:</span> FAILED DUE TO LOW ATTENDANCE<br><br>`;
        output += `<span class="bold">Required Prelim Exam for 100:</span> FAILED DUE TO LOW ATTENDANCE`;
        result.innerHTML = output;
        return;
    } else if (attendanceRaw === 2 || attendanceRaw === 3) {
        output += `<span class="warning">⚠ WARNING: Attendance is low. High chance to fail.</span><br><br>`;
    }
    // 4-5 → safe, no warning

    // ===== Lab warning =====
    if (labAverage < 75) {
        output += `<span class="warning">⚠ WARNING: Your lab work average is low. You still have a chance to fail due to poor lab performance.</span><br><br>`;
    }

    // ===== Required Prelim Exam =====
    const required75 = (75 - classStanding * 0.7) / 0.3;
    const required100 = (100 - classStanding * 0.7) / 0.3;

    const required75Display = required75 <= 0 ? `0% – Already Passed` : `${required75.toFixed(2)}%`;
    const required100Display = required100 <= 0 ? `0% – Already Passed` : `${required100.toFixed(2)}%`;

    const remark = classStanding >= 75 ? "PASSED" : "FAILED";

    output += `<span class="bold">Remark:</span> ${remark}<br><br>`;
    output += `<span class="bold">Required Prelim Exam for 75:</span> ${required75Display}<br><br>`;
    output += `<span class="bold">Required Prelim Exam for 100:</span> ${required100Display}`;

    result.innerHTML = output;
}

/* ================= CLEAR FUNCTION ================= */
function clearAll() {
    document.getElementById("attendance").value = "";
    document.getElementById("lab1").value = "";
    document.getElementById("lab2").value = "";
    document.getElementById("lab3").value = "";
    document.getElementById("result").innerHTML = "";
}
