const API_BASE_URL = "http://localhost:8080/api";

function getAuthHeaders() {
    const token = localStorage.getItem('jwt_token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
    };
}

// --- NEW: TOAST NOTIFICATION SYSTEM ---
function showToast(message, type = 'error') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerText = message;
    container.appendChild(toast);
    
    // Trigger the slide-in animation
    setTimeout(() => toast.classList.add('show'), 10);
    
    // Remove the toast after 3.5 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300); // Wait for animation to finish
    }, 3500);
}

// --- UI TOGGLES ---
function toggleAuthScreens(screen) {
    document.getElementById('login-screen').style.display = screen === 'login' ? 'flex' : 'none';
    document.getElementById('register-screen').style.display = screen === 'register' ? 'flex' : 'none';
}

function showDashboard() {
    document.getElementById('login-screen').style.display = 'none';
    document.getElementById('register-screen').style.display = 'none';
    document.getElementById('dashboard-screen').style.display = 'block';
    loadDashboard();
}

// --- UPGRADED AUTHENTICATION LOGIC ---
async function handleRegister(event) {
    event.preventDefault();
    const payload = {
        username: document.getElementById('regUsername').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        // 1. Check if the backend rejected us
        if (!response.ok) {
            const errorData = await response.json();
            
            // 2. Loop through the backend validation map and show a toast for EACH error!
            if (errorData.error) {
                showToast(errorData.error, 'error'); // Catches "Username already taken"
            } else {
                for (const field in errorData) {
                    showToast(errorData[field], 'error'); // Catches "Password must be 6 characters", etc.
                }
            }
            return; // Stop the function here so we don't log them in!
        }
        
        const data = await response.json();
        localStorage.setItem('jwt_token', data.token);
        localStorage.setItem('user_id', data.userId);
        showToast("Account created successfully!", "success");
        showDashboard();
    } catch (error) { showToast("Network error. Cannot reach backend."); }
}

async function handleLogin(event) {
    event.preventDefault();
    const payload = {
        username: document.getElementById('loginUsername').value,
        password: document.getElementById('loginPassword').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorData = await response.json();
            showToast(errorData.error || "Invalid credentials.", "error");
            return;
        }
        
        const data = await response.json();
        localStorage.setItem('jwt_token', data.token);
        localStorage.setItem('user_id', data.userId);
        showToast("Welcome back!", "success");
        showDashboard();
    } catch (error) { showToast("Network error. Cannot reach backend."); }
}

function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_id');
    
    document.getElementById('dashboard-screen').style.display = 'none';
    toggleAuthScreens('login');

    const aiResponseDiv = document.getElementById('ai-response');
    aiResponseDiv.innerText = '';
    aiResponseDiv.style.display = 'none';

    document.getElementById('loginUsername').value = '';
    document.getElementById('loginPassword').value = '';
    document.getElementById('regUsername').value = '';
    document.getElementById('regEmail').value = '';
    document.getElementById('regPassword').value = '';
    
    showToast("You have been signed out.", "success");
}

// --- DASHBOARD DATA LOGIC ---
async function loadDashboard() {
    const userId = localStorage.getItem('user_id');
    if (!userId) return logout();

    try {
        const response = await fetch(`${API_BASE_URL}/logs/user/${userId}`, { headers: getAuthHeaders() });
        
        if (response.status === 403 || response.status === 401) {
            showToast("Session expired. Please log in again.", "error");
            return logout();
        }

        const data = await response.json();
        
        document.getElementById('greeting').innerText = `Welcome back, ${data.user}`;
        document.getElementById('total-logs').innerText = data.totalLogs;
        document.getElementById('total-co2').innerHTML = `${data.totalCarbonFootprintKg.toFixed(2)} <span style="font-size: 1rem; color: var(--text-muted);">kg CO₂</span>`;
        
        const historyContainer = document.getElementById('history-container');
        historyContainer.innerHTML = ""; 
        
        if (data.activityHistory.length === 0) {
            historyContainer.innerHTML = "<li style='color: #64748b; justify-content: center;'>No activities logged yet.</li>";
        }
        
        data.activityHistory.reverse().forEach(log => {
            let li = document.createElement('li');
            let footprintText = log.carbonFootprint !== null ? `+${log.carbonFootprint.toFixed(2)} kg` : "Pending...";
            
            li.innerHTML = `
                <div class="log-details">
                    <span class="log-type">${log.activityType.replace('_', ' ')}</span>
                    <span class="log-amount">${log.amount} units</span>
                </div>
                <div style="display: flex; align-items: center; gap: 15px;">
                    <span class="log-footprint">${footprintText}</span>
                    <button class="btn-delete" onclick="deleteLog(${log.id})">Remove</button>
                </div>
            `;
            historyContainer.appendChild(li);
        });
    } catch (error) { console.error("Error:", error); }
}

// --- CRUD LOGIC ---
async function submitLog(event) {
    event.preventDefault(); 
    const userId = localStorage.getItem('user_id');
    const logData = { 
        activityType: document.getElementById('activityType').value, 
        amount: parseFloat(document.getElementById('amount').value) 
    };

    try {
        const response = await fetch(`${API_BASE_URL}/logs/user/${userId}`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(logData)
        });
        
        if (!response.ok) throw new Error("Failed to add activity");
        
        document.getElementById('amount').value = '';
        showToast("Activity logged successfully!", "success");
        loadDashboard();
    } catch (error) { showToast(error.message, "error"); }
}

async function deleteLog(logId) {
    if(!confirm("Remove this activity?")) return;
    try {
        await fetch(`${API_BASE_URL}/logs/${logId}`, { 
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        showToast("Activity removed.", "success");
        loadDashboard();
    } catch (error) { showToast("Failed to delete log.", "error"); }
}

// --- AI LOGIC ---
async function getAiAdvice() {
    const userId = localStorage.getItem('user_id');
    const btn = document.getElementById('ai-btn');
    const responseDiv = document.getElementById('ai-response');
    
    btn.disabled = true; btn.innerText = "Analyzing data..."; 
    responseDiv.style.display = "block"; responseDiv.innerText = "Consulting Google Gemini...";
    
    try {
        const response = await fetch(`${API_BASE_URL}/ai/suggestion/${userId}`, {
            headers: getAuthHeaders()
        });
        responseDiv.innerText = await response.text();
    } catch (error) { responseDiv.innerText = "Oops! Could not reach the AI."; } 
    finally { btn.disabled = false; btn.innerText = "Regenerate Plan"; }
}

window.onload = () => {
    if (localStorage.getItem('jwt_token')) {
        showDashboard();
    }
};