// Base URL of your backend API
const API_BASE = 'http://localhost:8080/api';

// Login form submit handler
document.getElementById('loginForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value.trim();

  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(`Login failed: ${errorText}`);
    }

    const data = await res.json();
    console.log('Login successful:', data);

    localStorage.setItem('token', data.token);
    localStorage.setItem('role', data.role);

    // Redirect based on user role
    if (data.role === 'ADMIN') {
      window.location.href = 'admin.html';
    } else {
      window.location.href = 'dashboard.html';
    }

  } catch (err) {
    console.error('Login error:', err);
    alert('Server Error: ' + err.message);
  }
});
