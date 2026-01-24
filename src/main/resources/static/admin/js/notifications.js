document.addEventListener("DOMContentLoaded", () => {
    loadUsers();
    loadNotifications();
});

// ================= LOAD USERS =================
function loadUsers() {
    fetch("/admin/api/users", { credentials: "include" })
        .then(res => res.json())
        .then(users => {
            const select = document.getElementById("userSelect");
            select.innerHTML = `<option value="">-- Chọn User --</option>`;

            users.forEach(u => {
                select.innerHTML += `
                    <option value="${u.id}">
                        ${u.fullName ?? u.username} (ID: ${u.id})
                    </option>
                `;
            });
        })
        .catch(() => alert("Không tải được danh sách user"));
}

// ================= LOAD NOTIFICATIONS =================
function loadNotifications() {
    fetch("/admin/api/notifications", { credentials: "include" })
        .then(res => res.json())
        .then(list => {
            const tbody = document.getElementById("notificationTable");
            tbody.innerHTML = "";

            list.forEach(n => {
                tbody.innerHTML += `
                <tr>
                    <td>${n.id}</td>
                    <td>${n.user?.id ?? ""}</td>
                    <td><input value="${n.title ?? ""}"></td>
                    <td><textarea>${n.message ?? ""}</textarea></td>
                    <td>
                        <select>
                            <option value="SUCCESS" ${n.type==="SUCCESS"?"selected":""}>SUCCESS</option>
                            <option value="INFO" ${n.type==="INFO"?"selected":""}>INFO</option>
                            <option value="WARNING" ${n.type==="WARNING"?"selected":""}>WARNING</option>
                            <option value="CRITICAL" ${n.type==="CRITICAL"?"selected":""}>CRITICAL</option>
                        </select>
                    </td>
                    <td>${n.read ? "READ" : "UNREAD"}</td>
                    <td>
                        <button onclick="saveNotification(${n.id}, this)">Save</button>
                        <button onclick="markRead(${n.id})">Read</button>
                        <button class="danger" onclick="deleteNotification(${n.id})">Delete</button>
                    </td>
                </tr>`;
            });
        });
}

// ================= ADD ONE =================
function addNotification() {
    const userId = document.getElementById("userSelect").value;

    if (!userId) {
        alert("Vui lòng chọn user");
        return;
    }

    const data = {
        user: { id: Number(userId) },
        title: "New Notification",
        message: "Notification content",
        type: "INFO"
    };

    fetch("/admin/api/notifications", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) throw new Error();
        loadNotifications();
    })
    .catch(() => alert("Lỗi tạo notification"));
}

// ================= ADD ALL =================
function addNotificationForAll() {
    if (!confirm("Gửi notification cho TẤT CẢ users?")) return;

    fetch("/admin/api/users", { credentials: "include" })
        .then(res => res.json())
        .then(users => {
            const requests = users.map(u => {
                const data = {
                    user: { id: u.id },
                    title: "System Notification",
                    message: "This is a system-wide notification",
                    type: "INFO"
                };

                return fetch("/admin/api/notifications", {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                });
            });

            return Promise.all(requests);
        })
        .then(() => {
            alert("Đã gửi cho tất cả users");
            loadNotifications();
        })
        .catch(() => alert("Lỗi gửi ALL"));
}

// ================= UPDATE =================
function saveNotification(id, btn) {
    const row = btn.closest("tr");
    const inputs = row.querySelectorAll("input, textarea, select");

    const data = {
        title: inputs[0].value,
        message: inputs[1].value,
        type: inputs[2].value
    };

    fetch(`/admin/api/notifications/${id}`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    }).then(loadNotifications);
}

// ================= MARK READ =================
function markRead(id) {
    fetch(`/admin/api/notifications/${id}/read`, {
        method: "PUT",
        credentials: "include"
    }).then(loadNotifications);
}

// ================= DELETE =================
function deleteNotification(id) {
    if (!confirm("Xóa notification?")) return;

    fetch(`/admin/api/notifications/${id}`, {
        method: "DELETE",
        credentials: "include"
    }).then(loadNotifications);
}
