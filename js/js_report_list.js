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
var allBugs = [];

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
    loadBugs();
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

function loadBugs() {
    (function () {
        console.log('[fetch] URL:', CTX + '/GetBugReportsServlet');
        return fetch(CTX + '/GetBugReportsServlet');
    })()
            .then(function (r) {
                return r.json();
            })
            .then(function (data) {
                allBugs = data;
                renderStats(allBugs);
                renderTable(allBugs);
            })
            .catch(function () {
                document.getElementById('table-wrap').innerHTML =
                        '<div class="error-msg">ไม่สามารถโหลดรายงานปัญหาได้</div>';
            });
}

function renderStats(bugs) {
    var p = bugs.filter(function (b) {
        return (b.status || 'pending') === 'pending';
    }).length;
    var i = bugs.filter(function (b) {
        return (b.status || 'pending') === 'in_progress';
    }).length;
    var r = bugs.filter(function (b) {
        return (b.status || 'pending') === 'resolved';
    }).length;
    document.getElementById('stat-bar').innerHTML =
            '<div class="stat-item">ทั้งหมด <b>' + bugs.length + '</b> รายการ</div>' +
            '<div class="stat-item">รอดำเนินการ <b>' + p + '</b></div>' +
            '<div class="stat-item">กำลังดำเนินการ <b>' + i + '</b></div>' +
            '<div class="stat-item">แก้ไขแล้ว <b>' + r + '</b></div>';
}

function renderTable(bugs) {
    var wrap = document.getElementById('table-wrap');
    if (!bugs.length) {
        wrap.innerHTML = '<div class="empty-msg">ไม่พบรายงานปัญหา</div>';
        return;
    }
    var html = '<table class="data-table"><thead><tr>' +
            '<th>#</th><th>หัวข้อ</th><th>รายละเอียด</th><th>ผู้แจ้ง</th><th>Email</th><th>สถานะ</th><th style="text-align:center">จัดการ</th>' +
            '</tr></thead><tbody>';
    bugs.forEach(function (b, i) {
        var st = b.status || 'pending';
        html += '<tr>' +
                '<td>' + (i + 1) + '</td>' +
                '<td>' + esc(b.subject) + '</td>' +
                '<td class="detail-cell">' + esc(b.detail) + '</td>' +
                '<td>' + esc(b.reporterName || 'ไม่ระบุ') + '</td>' +
                '<td>' + esc(b.reporterEmail || '-') + '</td>' +
                '<td>' + statusBadge(st) + '</td>' +
                '<td style="text-align:center">' +
                '<button class="btn-edit" onclick="openEditModal(' + b.reportId + ',\'' + st + '\',\'' + esc(b.subject) + '\')">&#9998; แก้ไข</button>' +
                '</td>' +
                '</tr>';
    });
    wrap.innerHTML = html + '</tbody></table>';
}

function filterBugs() {
    var q = document.getElementById('search').value.toLowerCase();
    var st = document.getElementById('status-filter').value;
    renderTable(allBugs.filter(function (b) {
        var mq = !q || (b.subject || '').toLowerCase().includes(q) ||
                (b.detail || '').toLowerCase().includes(q) ||
                (b.reporterName || '').toLowerCase().includes(q);
        // null/undefined status ถือเป็น 'pending'
        var actualStatus = b.status || 'pending';
        return mq && (!st || actualStatus === st);
    }));
}

function statusBadge(status) {
    var map = {
        'pending': ['badge-pending', 'Pending'],
        'in_progress': ['badge-progress', 'In Progress'],
        'resolved': ['badge-resolved', 'Resolved']
    };
    // null หรือ undefined → แสดงเป็น Pending
    var key = status || 'pending';
    var v = map[key];
    if (v)
        return '<span class="badge ' + v[0] + '">' + v[1] + '</span>';
    return '<span class="badge">' + esc(status) + '</span>';
}

/* ── Edit Status Modal ── */
var editingReportId = null;

function openEditModal(reportId, currentStatus, subject) {
    editingReportId = reportId;
    document.getElementById('modal-info').innerHTML =
            'หัวข้อ: <b>' + subject + '</b>';
    document.getElementById('modal-status').value = currentStatus || 'pending';
    document.getElementById('btn-save-status').disabled = false;
    document.getElementById('modal').classList.add('open');
}

function closeModal() {
    document.getElementById('modal').classList.remove('open');
    editingReportId = null;
}

function closeOutside(e) {
    if (e.target === document.getElementById('modal'))
        closeModal();
}

function saveStatus() {
    if (!editingReportId)
        return;
    var status = document.getElementById('modal-status').value;
    var btn = document.getElementById('btn-save-status');
    btn.disabled = true;

    fetch(CTX + '/UpdateBugStatusServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'reportId=' + encodeURIComponent(editingReportId) +
                '&status=' + encodeURIComponent(status)
    })
            .then(function (r) {
                return r.json();
            })
            .then(function (res) {
                btn.disabled = false;
                if (res.success) {
                    // อัปเดต allBugs ใน memory
                    allBugs.forEach(function (b) {
                        if (String(b.reportId) === String(editingReportId))
                            b.status = status;
                    });
                    closeModal();
                    renderStats(allBugs);
                    filterBugs(); // re-render table พร้อม filter ปัจจุบัน
                    showToast('success', 'อัปเดตสถานะสำเร็จ');
                } else {
                    showToast('error', res.message || 'อัปเดตไม่สำเร็จ');
                    btn.disabled = false;
                }
            })
            .catch(function () {
                btn.disabled = false;
                showToast('error', 'ไม่สามารถเชื่อมต่อ server ได้');
            });
}

/* ── Toast ── */
function showToast(type, msg) {
    var t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        t.style.cssText = 'position:fixed;bottom:28px;left:50%;transform:translateX(-50%);padding:10px 28px;border-radius:3px;font-size:0.95rem;font-family:Sarabun,sans-serif;color:#fff;z-index:9999;display:none';
        document.body.appendChild(t);
    }
    t.textContent = msg;
    t.style.background = type === 'success' ? '#2a7a2a' : '#c00';
    t.style.display = 'block';
    setTimeout(function () {
        t.style.display = 'none';
    }, 2500);
}

function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

init();