// VALID ACCOUNTS
const validAccounts = [
    { username: "SWAT", password: "1234567" },
    { username: "OMOH", password: "1234567" },
    { username: "SAS", password: "1234567" },
    { username: "Edge (BR)", password: "1234567" },
    { username: "BlackBeast", password: "1234567" }
];

// Beep sound
const beep = new Audio("beep.mp3");

// Attendance records
let attendanceRecords = [];

// Handle login submission
function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const message = document.getElementById("message");
    const timestampDisplay = document.getElementById("timestamp");
    const downloadBtn = document.getElementById("downloadBtn");

    // Check if credentials are valid
    const isValid = validAccounts.some(
        acc => acc.username === username && acc.password === password
    );

    if (isValid) {
        const now = new Date();
        const timestamp = formatDateTime(now);

        // SUCCESS MESSAGE
        message.textContent = "ACCESS SUCCESSFUL";
        message.style.color = "#00ff00";
        message.style.backgroundColor = "rgba(255,255,255,0.1)";
        timestampDisplay.textContent = "Login Time: " + timestamp;

        // Save attendance
        attendanceRecords.push({ username, timestamp });

        // Update attendance table
        updateAttendanceDisplay();

        // Show download button
        downloadBtn.style.display = "block";
        downloadBtn.onclick = () => {
            generateAttendanceFile();
            downloadBtn.style.display = "none";
        };

    } else {
        // DENIED MESSAGE
        message.textContent = "ACCESS DENIED";
        message.style.color = "#ff4444";
        message.style.backgroundColor = "rgba(0,0,0,0.6)";
        timestampDisplay.textContent = "";
        downloadBtn.style.display = "none";
        beep.play();
    }
}

// Update attendance table display
function updateAttendanceDisplay() {
    const tableBody = document.querySelector("#attendanceTable tbody");
    tableBody.innerHTML = ""; // clear current rows

    attendanceRecords.forEach(record => {
        const row = document.createElement("tr");

        const userCell = document.createElement("td");
        userCell.textContent = record.username;

        const timeCell = document.createElement("td");
        timeCell.textContent = record.timestamp;

        const statusCell = document.createElement("td");
        statusCell.textContent = "Present"; // default status

        row.appendChild(userCell);
        row.appendChild(timeCell);
        row.appendChild(statusCell);

        tableBody.appendChild(row);
    });
}

// Format date MM/DD/YYYY HH:MM:SS
function formatDateTime(date) {
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const yyyy = date.getFullYear();
    const hh = String(date.getHours()).padStart(2, '0');
    const min = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');

    return `${mm}/${dd}/${yyyy} ${hh}:${min}:${ss}`;
}

// Generate attendance summary file (with status)
function generateAttendanceFile() {
    let data = "ATTENDANCE SUMMARY\n\n";

    attendanceRecords.forEach(record => {
        data += `Username: ${record.username}\n`;
        data += `Time In: ${record.timestamp}\n`;
        data += `Status: Present\n\n`;
    });

    const blob = new Blob([data], { type: "text/plain" });
    const link = document.createElement("a");

    link.href = URL.createObjectURL(blob);
    link.download = "attendance_summary.txt";
    link.click();
}
