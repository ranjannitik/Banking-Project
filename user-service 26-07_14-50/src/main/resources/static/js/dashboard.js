const API = "http://localhost:8080/api";
const token = localStorage.getItem("token");

if (!token) {
  alert("Unauthorized. Please login.");
  window.location.href = "/login.html";
}

const headers = {
  "Content-Type": "application/json",
  Authorization: "Bearer " + token
};

document.addEventListener("DOMContentLoaded", () => {
  loadProfile();
  loadCards();
  loadLoans();
  loadBills();
  loadTransactions();
});

// ✅ Load profile and balance
async function loadProfile() {
  const res = await fetch(`${API}/user/profile`, { headers });
  if (res.ok) {
    const data = await res.json();
    document.getElementById("userEmail").innerText = data.name;
    document.getElementById("userBalance").innerText = data.balance.toFixed(2);
  }
}

// ✅ Add card
document.getElementById("addCardForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const cardNumber = document.getElementById("cardNumber").value;
  const cardType = document.getElementById("cardType").value;
  const expiry = document.getElementById("expiry").value;

  await fetch(`${API}/user/cards`, {
    method: "POST",
    headers,
    body: JSON.stringify({ cardNumber, cardType, expiry })
  });

  loadCards();
});

// ✅ Load cards
async function loadCards() {
  const res = await fetch(`${API}/user/cards`, { headers });
  const list = document.getElementById("cardList");
  list.innerHTML = "";
  const cards = await res.json();
  cards.forEach(card => {
    const li = document.createElement("li");
    li.innerText = `${card.cardType} - ****${card.cardNumber.slice(-4)} (exp: ${card.expiry})`;
    list.appendChild(li);
  });
}

// ✅ Apply loan
document.getElementById("loanForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const loanType = document.getElementById("loanType").value;
  const loanAmount = document.getElementById("loanAmount").value;
  const dueDate = document.getElementById("dueDate").value;

  await fetch(`${API}/user/loans`, {
    method: "POST",
    headers,
    body: JSON.stringify({ loanType, amount: loanAmount, dueDate })
  });

  loadLoans();
});

// ✅ Load loans
async function loadLoans() {
  const res = await fetch(`${API}/user/loans`, { headers });
  const loans = await res.json();
  const list = document.getElementById("loanList");
  list.innerHTML = "";
  loans.forEach(loan => {
    const li = document.createElement("li");
    li.innerText = `${loan.loanType} - ₹${loan.amount} (due: ${loan.dueDate})`;
    list.appendChild(li);
  });
}

// ✅ Pay bill
document.getElementById("billForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const billType = document.getElementById("billType").value;
  const billNumber = document.getElementById("billNumber").value;
  const billAmount = document.getElementById("billAmount").value;

  const res = await fetch(`${API}/user/pay-bill`, {
    method: "POST",
    headers,
    body: JSON.stringify({ billType, billNumber, amount: billAmount })
  });

  const msg = await res.text();
  alert(msg);
  loadBills();
  loadProfile(); // refresh balance
});

// ✅ Load bills
async function loadBills() {
  const res = await fetch(`${API}/user/bills`, { headers });
  const bills = await res.json();
  const list = document.getElementById("billList");
  list.innerHTML = "";
  bills.forEach(bill => {
    const li = document.createElement("li");
    li.innerText = `${bill.billType} - ₹${bill.amount} (${bill.billNumber})`;
    list.appendChild(li);
  });
}

// ✅ Send money
document.getElementById("sendMoneyForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const receiverEmail = document.getElementById("receiverEmail").value;
  const amount = document.getElementById("amount").value;

  const res = await fetch(`${API}/user/transfer`, {
    method: "POST",
    headers,
    body: JSON.stringify({ receiverEmail, amount })
  });

  const msg = await res.text();
  alert(msg);
  loadProfile();
  loadTransactions();
});

// ✅ Load transactions
async function loadTransactions() {
  const res = await fetch(`${API}/user/transactions`, { headers });
  const txList = await res.json();
  const list = document.getElementById("transactionList");
  list.innerHTML = "";
  txList.forEach(tx => {
    const li = document.createElement("li");
    li.innerText = `${tx.senderEmail} ➡ ${tx.receiverEmail} - ₹${tx.amount} [${tx.timestamp}]`;
    list.appendChild(li);
  });
}

// ✅ Logout
function logout() {
  localStorage.removeItem("token");
  window.location.href = "/login.html";
}
