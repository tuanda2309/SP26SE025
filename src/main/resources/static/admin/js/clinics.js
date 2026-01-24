document.addEventListener("DOMContentLoaded", loadClinics);

function loadClinics() {
    fetch("/admin/api/clinics", { credentials: "include" })
        .then(res => res.json())
        .then(clinics => {
            const tbody = document.getElementById("clinicTable");
            tbody.innerHTML = "";

            clinics.forEach(c => {
                tbody.innerHTML += `
                <tr>
                    <td>${c.id}</td>
                    <td>${c.clinicName}</td>
                    <td>${c.address ?? ""}</td>
                    <td>${c.phone ?? ""}</td>
                    <td>${c.verificationStatus}</td>
                    <td>
                        ${c.verificationStatus === "PENDING" ? 
                            `<button onclick="approveClinic(${c.id})">Approve</button>` 
                            : ""
                        }
                        ${c.verificationStatus === "FULFILLED" ? 
                            `<button onclick="rejectClinic(${c.id})">Reject</button>` 
                            : ""
                        }
                    </td>
                </tr>`;
            });
        });
}

function approveClinic(id) {
    fetch(`/admin/api/clinics/${id}/approve`, {
        method: "PUT",
        credentials: "include"
    }).then(loadClinics);
}

function rejectClinic(id) {
    fetch(`/admin/api/clinics/${id}/reject`, {
        method: "PUT",
        credentials: "include"
    }).then(loadClinics);
}
