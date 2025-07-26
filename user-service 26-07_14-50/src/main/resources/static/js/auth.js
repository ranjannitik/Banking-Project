const loginForm = document.getElementById('loginForm');
const loginMsg = document.getElementById('loginMsg');
const otpSection = document.getElementById('otpSection');

let loginEmail = '';

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  loginEmail = email;

  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });

  const text = await res.text();
  loginMsg.textContent = text;

  if (res.ok) otpSection.style.display = 'block';
});

async function verifyLoginOtp() {
  const otp = document.getElementById('otp').value;

  const res = await fetch('/api/auth/login/verify', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: loginEmail, otp })
  });

  if (res.ok) {
    const data = await res.json();
    loginMsg.textContent = '✅ Login successful!';
    localStorage.setItem('token', data.token);
    window.location.href = '/dashboard.html';
  } else {
    const err = await res.text();
    loginMsg.textContent = err || '❌ Invalid OTP.';
  }
}
