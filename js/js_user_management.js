var SESSION_KEY = 'idx_user';

// สร้าง base URL สำหรับเรียก Servlet
// หน้านี้อยู่ใน /admin/ จึงต้อง go up 1 ระดับ
// ใช้ document.location.pathname เพื่อหา context path จริง
function getServletBase() {
    // pathname เช่น /Project_4105_Ver1-1/admin/user_management.html
    var pathname = window.location.pathname;
    // ตัด /admin/filename.html ออก → เหลือ /Project_4105_Ver1-1
    var base = pathname.substring(0, pathname.lastIndexOf('/admin/'));
    console.log('[Admin Page] pathname:', pathname);
    console.log('[Admin Page] servletBase:', base);
    return base;
}
var CTX = getServletBase();
var allUsers = [];

function init() {
    var user = getUser();
    updateNav(user);
    if (!user) {
        location.href = '../login.html';
        return;
    }
    if (user.role !== 'Admin') {
        document.getElementById('access-denied').style.display = 'flex';
        return;
    }
    document.getElementById('main-content').style.display = 'block';
    loadUsers();
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
        fetch(CTX + '/LogoutServlet', {method: 'POST'}).finally(function () {
            sessionStorage.removeItem(SESSION_KEY);
            location.href = '../login.html';
        });
    } else {
        location.href = '../login.html';
    }
}

function loadUsers() {
    (function () {
        console.log('[fetch] URL:', CTX + '/GetAllUsersServlet');
        return fetch(CTX + '/GetAllUsersServlet');
    })()
            .then(function (r) {
                return r.json();
            })
            .then(function (data) {
                allUsers = data;
                renderStats(allUsers);
                renderTable(allUsers);
            })
            .catch(function () {
                document.getElementById('table-wrap').innerHTML =
                        '<div class="error-msg">ไม่สามารถโหลดข้อมูลผู้ใช้ได้</div>';
            });
}

function renderStats(users) {
    var counts = {};
    users.forEach(function (u) {
        counts[u.role] = (counts[u.role] || 0) + 1;
    });
    var html = '<div class="stat-item">ทั้งหมด <b>' + users.length + '</b> คน</div>';
    Object.keys(counts).forEach(function (r) {
        html += '<div class="stat-item">' + esc(r) + ' <b>' + counts[r] + '</b></div>';
    });
    document.getElementById('stat-bar').innerHTML = html;
}

function renderTable(users) {
    var wrap = document.getElementById('table-wrap');
    if (!users.length) {
        wrap.innerHTML = '<div class="empty-msg">ไม่พบข้อมูลผู้ใช้</div>';
        return;
    }
    var html = '<table class="data-table"><thead><tr>' +
            '<th>#</th><th>Username</th><th>ชื่อ</th><th>นามสกุล</th><th>Email</th><th>บทบาท</th>' +
            '</tr></thead><tbody>';
    users.forEach(function (u, i) {
        html += '<tr>' +
                '<td>' + (i + 1) + '</td>' +
                '<td>' + esc(u.username) + '</td>' +
                '<td>' + esc(u.firstname || '-') + '</td>' +
                '<td>' + esc(u.lastname || '-') + '</td>' +
                '<td>' + esc(u.email || '-') + '</td>' +
                '<td>' + roleBadge(u.role) + '</td>' +
                '</tr>';
    });
    wrap.innerHTML = html + '</tbody></table>';
}

function filterUsers() {
    var q = document.getElementById('search').value.toLowerCase();
    var role = document.getElementById('role-filter').value;
    renderTable(allUsers.filter(function (u) {
        var mq = !q || (u.username || '').toLowerCase().includes(q) ||
                (u.firstname || '').toLowerCase().includes(q) ||
                (u.lastname || '').toLowerCase().includes(q) ||
                (u.email || '').toLowerCase().includes(q);
        return mq && (!role || u.role === role);
    }));
}

function roleBadge(role) {
    var map = {'Admin': 'badge-admin', 'อาจารย์': 'badge-teacher', 'นักศึกษา': 'badge-student', 'บุคคลทั่วไป': 'badge-public'};
    return '<span class="badge ' + (map[role] || 'badge-public') + '">' + esc(role) + '</span>';
}

function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

init();