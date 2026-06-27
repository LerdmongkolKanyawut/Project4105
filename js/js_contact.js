const SESSION_KEY = 'idx_user';
function getUser() {
    try {
        return JSON.parse(sessionStorage.getItem(SESSION_KEY));
    } catch (e) {
        return null;
    }
}

function updateNav() {
    const u = getUser();
    document.getElementById('nav-welcome').textContent = u ? 'Welcome, ' + u.firstname : 'Welcome, Guest';
    document.getElementById('btn-auth').textContent = u ? 'Logout' : 'Login';
    document.getElementById('btn-profile').style.display = u ? 'inline-block' : 'none';
    document.getElementById('btn-mgmt').style.display = (u && u.role === 'Admin') ? 'inline-block' : 'none';
}

function handleAuth() {
    if (getUser()) {
        fetch('LogoutServlet', {method: 'POST'})
                .finally(() => {
                    sessionStorage.removeItem(SESSION_KEY);
                    updateNav();
                });
    } else {
        location.href = 'login.html';
    }
}

function openModal() {
    ['rpt-subject', 'rpt-detail', 'rpt-name', 'rpt-email']
            .forEach(id => document.getElementById(id).value = '');
    document.getElementById('rpt-msg').textContent = '';
    document.getElementById('rpt-msg').className = 'msg';
    document.getElementById('modal').classList.add('open');
}
function closeModal() {
    document.getElementById('modal').classList.remove('open');
}
function closeOutside(e) {
    if (e.target === document.getElementById('modal'))
        closeModal();
}

/* ── AJAX POST → BugReportServlet ── */
function submitReport() {
    const subj = document.getElementById('rpt-subject').value.trim();
    const detail = document.getElementById('rpt-detail').value.trim();
    const name = document.getElementById('rpt-name').value.trim();
    const email = document.getElementById('rpt-email').value.trim();
    const msgEl = document.getElementById('rpt-msg');

    if (!subj || !detail) {
        msgEl.className = 'msg error';
        msgEl.textContent = 'กรุณากรอกหัวข้อและรายละเอียด';
        return;
    }

    const btn = document.getElementById('btn-send');
    btn.disabled = true;

    /*
     * BugReportServlet รับ POST application/x-www-form-urlencoded
     * คืน JSON:
     *   { "success": true }
     *   { "success": false, "message": "..." }
     */
    fetch('BugReportServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'subject=' + encodeURIComponent(subj)
                + '&detail=' + encodeURIComponent(detail)
                + '&name=' + encodeURIComponent(name)
                + '&email=' + encodeURIComponent(email)
    })
            .then(r => r.json())
            .then(res => {
                if (res.success) {
                    msgEl.className = 'msg success';
                    msgEl.textContent = 'ส่งรายงานสำเร็จ ขอบคุณที่แจ้งปัญหา';
                    setTimeout(() => closeModal(), 1800);
                } else {
                    msgEl.className = 'msg error';
                    msgEl.textContent = res.message || 'เกิดข้อผิดพลาด กรุณาลองใหม่';
                }
                btn.disabled = false;
            })
            .catch(() => {
                msgEl.className = 'msg error';
                msgEl.textContent = 'ไม่สามารถส่งรายงานได้ กรุณาลองใหม่';
                btn.disabled = false;
            });
}

updateNav();