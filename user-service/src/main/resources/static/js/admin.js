const BASE_URL = "http://localhost:8080/api";
const token = localStorage.getItem("token");

if (!token) {
  alert("Not logged in!");
  window.location.href = "login.html";
}

function setHeader() {
  return {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };
}

function getAllUsers() {
  fetch(`${BASE_URL}/admin/users`, {
    headers: setHeader(),
  })
    .then(res => res.json())
    .then(users => {
      const list = document.getElementById("userList");
      list.innerHTML = "";
      users.forEach(user => {
        const li = document.createElement("li");
        li.innerHTML = `
          ${user.name} (${user.email}) - ${user.status}
          <button onclick="toggleStatus('${user.id}', '${user.status}')">
            ${user.status === 'ACTIVE' ? 'Block' : 'Unblock'}
          </button>
          <button onclick="deleteUser('${user.id}')">Delete</button>
        `;
        list.appendChild(li);
      });
    });
}

function toggleStatus(userId, currentStatus) {
  const newStatus = currentStatus === "ACTIVE" ? "BLOCKED" : "ACTIVE";

  fetch(`${BASE_URL}/admin/users/${userId}/status`, {
    method: "PUT",
    headers: setHeader(),
    body: JSON.stringify({ status: newStatus }),
  })
    .then(res => res.json())
    .then(() => {
      alert(`User ${newStatus === "BLOCKED" ? "blocked" : "unblocked"}!`);
      getAllUsers();
    });
}

function deleteUser(userId) {
  if (!confirm("Are you sure you want to delete this user?")) return;

  fetch(`${BASE_URL}/admin/users/${userId}`, {
    method: "DELETE",
    headers: setHeader(),
  })
    .then(res => res.json())
    .then(() => {
      alert("User deleted!");
      getAllUsers();
    });
}

function getAllLogs() {
  fetch(`${BASE_URL}/admin/logs`, {
    headers: setHeader(),
  })
    .then(res => res.json())
    .then(logs => {
      const list = document.getElementById("logList");
      list.innerHTML = "";
      logs.forEach(log => {
        const li = document.createElement("li");
        li.innerText = `[${log.timestamp}] ${log.message}`;
        list.appendChild(li);
      });
    });
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

// Load admin dashboard data
if (window.location.pathname.includes("admin.html")) {
  getAllUsers();
  getAllLogs();
}
