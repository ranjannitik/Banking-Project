const token = localStorage.getItem("token");
const userEmail = localStorage.getItem("email");

// Redirect to login if not logged in
if (!token) {
  window.location.href = "login.html";
}

document.getElementById("userEmail").innerText = userEmail;

// Fetch Balance
fetch("http://localhost:8080/api/user/balance", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then(res => res.json())
  .then(data => {
    document.getElementById("userBalance").innerText = data.balance;
  });

// Send Money
document.getElementById("sendMoneyForm").addEventListener("submit", async e => {
  e.preventDefault();
  const receiverEmail = document.getElementById("receiverEmail").value;
  const amount = document.getElementById("amount").value;

  const res = await fetch("http://localhost:8080/api/transactions", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ receiverEmail, amount, type: "TRANSFER" }),
  });

  const data = await res.text();
  document.getElementById("sendMoneyMsg").innerText = data;
});

// Fetch Transactions
fetch("http://localhost:8080/api/transactions", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then(res => res.json())
  .then(data => {
    const list = document.getElementById("transactionList");
    data.forEach(txn => {
      const li = document.createElement("li");
      li.innerText = `To: ${txn.receiverEmail}, Amount: ₹${txn.amount}, Type: ${txn.type}`;
      list.appendChild(li);
    });
  });

// Add Card
document.getElementById("addCardForm").addEventListener("submit", async e => {
  e.preventDefault();
  const cardNumber = document.getElementById("cardNumber").value;
  const cardType = document.getElementById("cardType").value;
  const expiry = document.getElementById("expiry").value;

  await fetch("http://localhost:8080/api/user/cards", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ cardNumber, cardType, expiry }),
  });

  location.reload();
});

// Get Cards
fetch("http://localhost:8080/api/user/cards", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then(res => res.json())
  .then(cards => {
    const list = document.getElementById("cardList");
    cards.forEach(card => {
      const li = document.createElement("li");
      li.innerText = `${card.cardNumber} - ${card.cardType} (Exp: ${card.expiry})`;
      list.appendChild(li);
    });
  });

// Apply Loan
document.getElementById("loanForm").addEventListener("submit", async e => {
  e.preventDefault();
  const loanType = document.getElementById("loanType").value;
  const amount = document.getElementById("loanAmount").value;
  const dueDate = document.getElementById("dueDate").value;

  await fetch("http://localhost:8080/api/user/loans", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ loanType, amount, dueDate, status: "APPROVED" }),
  });

  location.reload();
});

// Get Loans
fetch("http://localhost:8080/api/user/loans", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then(res => res.json())
  .then(loans => {
    const list = document.getElementById("loanList");
    loans.forEach(loan => {
      const li = document.createElement("li");
      li.innerText = `${loan.loanType}: ₹${loan.amount}, Due: ${loan.dueDate}, Status: ${loan.status}`;
      list.appendChild(li);
    });
  });

// Pay Bill
document.getElementById("billForm").addEventListener("submit", async e => {
  e.preventDefault();
  const billType = document.getElementById("billType").value;
  const billNumber = document.getElementById("billNumber").value;
  const amount = document.getElementById("billAmount").value;

  await fetch("http://localhost:8080/api/user/pay-bill", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ billType, billNumber, amount }),
  });

  location.reload();
});

// Get Bills
fetch("http://localhost:8080/api/user/bills", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then(res => res.json())
  .then(bills => {
    const list = document.getElementById("billList");
    bills.forEach(bill => {
      const li = document.createElement("li");
      li.innerText = `${bill.billType} Bill - ${bill.billNumber}: ₹${bill.amount}`;
      list.appendChild(li);
    });
  });

// Logout
function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}
