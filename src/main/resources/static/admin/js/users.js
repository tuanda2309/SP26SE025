document.addEventListener("DOMContentLoaded", loadUsers);

function loadUsers() {
    fetch("/admin/api/users", { credentials: "include" })
        .then(res => res.json())
        .then(users => {
            const tbody = document.getElementById("userTable");
            tbody.innerHTML = "";

            users.forEach(u => {
                tbody.innerHTML += `
                <tr>
                    <td>${u.id}</td>

                    <td>
                        <input type="text" value="${u.fullName || ''}">
                    </td>

                    <td>
                        <input type="email" value="${u.email || ''}">
                    </td>

                    <td>
                        <select>
                            <option value="CUSTOMER" ${u.role === "CUSTOMER" ? "selected" : ""}>CUSTOMER</option>
                            <option value="CONSULTANT" ${u.role === "CONSULTANT" ? "selected" : ""}>CONSULTANT</option>
                            <option value="STAFF" ${u.role === "STAFF" ? "selected" : ""}>STAFF</option>
                            <option value="MANAGER" ${u.role === "MANAGER" ? "selected" : ""}>MANAGER</option>
                            <option value="DOCTOR" ${u.role === "DOCTOR" ? "selected" : ""}>DOCTOR</option>
                            <option value="CLINIC" ${u.role === "CLINIC" ? "selected" : ""}>CLINIC</option>
                            <option value="ADMIN" ${u.role === "ADMIN" ? "selected" : ""}>ADMIN</option>
                        </select>
                    </td>

                    <td>${u.enabled ? "ACTIVE" : "DISABLED"}</td>

                    <td>
                        <button onclick="saveUser(${u.id}, this)">Lưu</button>
                        <button onclick="toggleUser(${u.id})">
                            ${u.enabled ? "Disable" : "Enable"}
                        </button>
                        <button onclick="deleteUser(${u.id})">Xóa</button>
                    </td>
                </tr>`;
            });
        })
        .catch(err => console.error("Load users error:", err));
}

function saveUser(id, btn) {
    const row = btn.closest("tr");
    const inputs = row.querySelectorAll("input");
    const select = row.querySelector("select");

    const data = {
        fullName: inputs[0].value.trim(),
        email: inputs[1].value.trim(),
        role: select.value // ENUM: ADMIN | DOCTOR | MANAGER | ...
    };

    fetch(`/admin/api/users/${id}`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(t => { throw new Error(t); });
        }
        alert("Cập nhật thành công");
        loadUsers();
    })
    .catch(err => {
        console.error("Update error:", err);
        alert("Lỗi cập nhật user: " + err.message);
    });
}

function toggleUser(id) {
    fetch(`/admin/api/users/${id}/toggle`, {
        method: "PUT",
        credentials: "include"
    })
    .then(() => loadUsers())
    .catch(err => console.error("Toggle error:", err));
}

function deleteUser(id) {
    if (!confirm("Bạn chắc chắn muốn xóa user này?")) return;

    fetch(`/admin/api/users/${id}`, {
        method: "DELETE",
        credentials: "include"
    })
    .then(() => loadUsers())
    .catch(err => console.error("Delete error:", err));
}
