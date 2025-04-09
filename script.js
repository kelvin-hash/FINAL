function showRegisterForm() {
  document.getElementById("loginForm").style.display = "none";
  document.getElementById("registerForm").style.display = "block";
  document.getElementById("formTitle").textContent = "Register";
}

function showLoginForm() {
  document.getElementById("registerForm").style.display = "none";
  document.getElementById("loginForm").style.display = "block";
  document.getElementById("formTitle").textContent = "Login";
}

document.getElementById("loginForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const role = document.getElementById("role").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const backendURL = "http://localhost:8080";
  const loginEndpoint = role === "User"
    ? `${backendURL}/api/users/login`
    : `${backendURL}/api/admins/find?email=${email}`;

  try {
    const response = await fetch(loginEndpoint, {
      method: role === "User" ? "POST" : "GET",
      headers: { "Content-Type": "application/json" },
      body: role === "User" ? JSON.stringify({ email, password }) : undefined
    });

    if (role === "User") {
      const text = await response.text();
      if (response.ok && text.includes("Login successful")) {
        window.location.href = "userDashboard.html";
      } else {
        document.getElementById("loginMessage").textContent = "Invalid email or password.";
      }
    } else {
      const data = await response.json();
      if (response.ok && data.email === email) {
        window.location.href = "adminPanel.html";
      } else {
        document.getElementById("loginMessage").textContent = "Invalid credentials or role.";
      }
    }
  } catch (error) {
    console.error("Login error:", error);
    document.getElementById("loginMessage").textContent = "Server error. Try again.";
  }
});

document.getElementById("registerForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const first_name = document.getElementById("first_name").value;
  const last_name = document.getElementById("last_name").value;
  const email = document.getElementById("registerEmail").value;
  const phone_number = document.getElementById("phone_number").value;
  const password = document.getElementById("registerPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (password !== confirmPassword) {
    document.getElementById("registerMessage").textContent = "Passwords do not match!";
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/users/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ first_name, last_name, email, phone_number, password })
    });

    const data = await response.text();
    if (response.ok) {
      document.getElementById("registerMessage").style.color = "green";
      document.getElementById("registerMessage").textContent = "Registration successful! Please login.";
      setTimeout(showLoginForm, 2000);
    } else {
      document.getElementById("registerMessage").style.color = "red";
      document.getElementById("registerMessage").textContent = data;
    }
  } catch (error) {
    console.error("Registration error:", error);
    document.getElementById("registerMessage").textContent = "Registration failed. Try again.";
  }
});