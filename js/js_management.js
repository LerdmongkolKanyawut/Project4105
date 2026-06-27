var SESSION_KEY = 'idx_user';

function init() {
    var user = getUser();
    updateNav(user);
    if (!user) {
        location.href = 'login.html';
        return;
    }
    if (user.role !== 'Admin') {
        document.getElementById('access-denied').style.display = 'flex';
        return;
    }
    document.getElementById('mgmt-content').style.display = 'block';
    document.getElementById('admin-name').textContent = user.firstname || user.username;
}

function getUser() {
    try {
        return JSON.parse(sessionStorage.getItem(SESSION_KEY));
    } catch (e) {
        return null;
    }
}

function updateNav(user) {
    document.getElementById('nav-welcome').textContent = user ? 'Welcome, ' + user.firstname : 'Welcome, Guest';
    document.getElementById('btn-auth').textContent = user ? 'Logout' : 'Login';
    document.getElementById('btn-profile').style.display = user ? 'inline-block' : 'none';
    document.getElementById('btn-mgmt').style.display = (user && user.role === 'Admin') ? 'inline-block' : 'none';
}

function handleAuth() {
    if (getUser()) {
        fetch('LogoutServlet', {method: 'POST'}).finally(function () {
            sessionStorage.removeItem(SESSION_KEY);
            location.href = 'login.html';
        });
    } else {
        location.href = 'login.html';
    }
}

init();