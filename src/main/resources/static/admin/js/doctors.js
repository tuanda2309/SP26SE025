const doctors = [
    {
        id: 1,
        name: "Dr. Nguyen Van A",
        email: "doctorA@gmail.com",
        clinic: "Vision Care",
        patients: 120,
        status: "ACTIVE"
    },
    {
        id: 2,
        name: "Dr. Tran Thi B",
        email: "doctorB@gmail.com",
        clinic: "Eye Health",
        patients: 85,
        status: "INACTIVE"
    }
];

const table = document.getElementById("doctorTable");

doctors.forEach(d => {
    table.innerHTML += `
        <tr>
            <td>${d.id}</td>
            <td>${d.name}</td>
            <td>${d.email}</td>
            <td>
                <select>
                    <option>${d.clinic}</option>
                    <option>Vision Care</option>
                    <option>Eye Health</option>
                </select>
            </td>
            <td>${d.patients}</td>
            <td class="status ${d.status === "ACTIVE" ? "active" : "inactive"}">
                ${d.status}
            </td>
            <td>
                <button onclick="toggleDoctor(${d.id})">Toggle</button>
            </td>
        </tr>
    `;
});

function toggleDoctor(id) {
    alert("Toggle doctor status: ID " + id);
}
