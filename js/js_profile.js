const SESSION_KEY = 'idx_user';
function getUser() {
    try {
        return JSON.parse(sessionStorage.getItem(SESSION_KEY));
    } catch (e) {
        return null;
    }
}

/* ── Guard: ถ้าไม่ได้ login → ไป login.html ── */
(function () {
    if (!getUser())
        location.replace('login.html');
})();

/* ── Init: โหลดข้อมูลโปรไฟล์จาก Servlet ── */
function init() {
    /*
     * GetProfileServlet คืน JSON:
     * {
     *   "username":"...", "firstname":"...", "lastname":"...",
     *   "email":"...", "role":"...", "avatarUrl":"..."
     * }
     */
    fetch('GetProfileServlet')
            .then(r => r.json())
            .then(data => {
                if (data.error) {
                    location.replace('login.html');
                    return;
                }
                fillProfile(data);
                /* อัปเดต sessionStorage ให้ตรงกับ DB */
                const u = getUser();
                if (u) {
                    u.firstname = data.firstname;
                    sessionStorage.setItem(SESSION_KEY, JSON.stringify(u));
                }
            })
            .catch(() => location.replace('login.html'));
}

function fillProfile(data) {
    document.getElementById('nav-welcome').textContent = 'สวัสดี, ' + data.firstname;
    document.getElementById('btn-auth').textContent = 'Logout';
    document.getElementById('btn-profile').style.display = 'inline-block';
    document.getElementById('btn-mgmt') && (document.getElementById('btn-mgmt').style.display = (data.role === 'Admin') ? 'inline-block' : 'none');
    document.getElementById('display-name').textContent = data.firstname + ' ' + data.lastname;
    document.getElementById('display-username').textContent = '@' + data.username;
    document.getElementById('display-role').textContent = data.role;
    document.getElementById('info-username').value = data.username;
    document.getElementById('info-role').value = data.role;
    document.getElementById('edit-firstname').value = data.firstname;
    document.getElementById('edit-lastname').value = data.lastname;
    document.getElementById('edit-email').value = data.email || '';
    if (data.avatarUrl) {
        document.getElementById('avatar-preview').innerHTML =
                `<img src="${esc(data.avatarUrl)}" alt="avatar"/>`;
    }
}

/* ── Tabs ── */
function switchTab(name) {
    ['info', 'security', 'history'].forEach(t => {
        document.getElementById('tab-' + t).classList.toggle('active', t === name);
        document.getElementById('tab-' + t + '-btn').classList.toggle('active', t === name);
    });
    if (name === 'history')
        loadHistory();
}

/* ── Save Info (AJAX → UpdateProfileServlet) ── */
function saveInfo() {
    const fn = document.getElementById('edit-firstname').value.trim();
    const ln = document.getElementById('edit-lastname').value.trim();
    const em = document.getElementById('edit-email').value.trim();
    if (!fn) {
        setMsg('msg-info', 'error', 'กรุณากรอกชื่อ');
        return;
    }
    if (em && !em.includes('@')) {
        setMsg('msg-info', 'error', 'รูปแบบ Email ไม่ถูกต้อง');
        return;
    }

    fetch('UpdateProfileServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'firstname=' + encodeURIComponent(fn)
                + '&lastname=' + encodeURIComponent(ln)
                + '&email=' + encodeURIComponent(em)
    })
            .then(r => r.json())
            .then(res => {
                if (res.success) {
                    setMsg('msg-info', 'success', 'บันทึกข้อมูลสำเร็จ');
                    document.getElementById('display-name').textContent = fn + ' ' + ln;
                    document.getElementById('nav-welcome').textContent = 'สวัสดี, ' + fn;
                    /* อัปเดต sessionStorage */
                    const u = getUser();
                    if (u) {
                        u.firstname = fn;
                        sessionStorage.setItem(SESSION_KEY, JSON.stringify(u));
                    }
                } else {
                    setMsg('msg-info', 'error', res.message || 'เกิดข้อผิดพลาด');
                }
            });
}

/* ── Change Password (AJAX → ChangePasswordServlet) ── */
function changePassword() {
    const oldPw = document.getElementById('sec-old').value;
    const newPw = document.getElementById('sec-new').value;
    const cfPw = document.getElementById('sec-confirm').value;
    if (!oldPw || !newPw || !cfPw) {
        setMsg('msg-security', 'error', 'กรุณากรอกข้อมูลให้ครบ');
        return;
    }
    if (newPw.length < 4) {
        setMsg('msg-security', 'error', 'รหัสผ่านใหม่ต้องมีอย่างน้อย 4 ตัวอักษร');
        return;
    }
    if (newPw !== cfPw) {
        setMsg('msg-security', 'error', 'รหัสผ่านใหม่ไม่ตรงกัน');
        return;
    }

    fetch('ChangePasswordServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'oldPassword=' + encodeURIComponent(oldPw)
                + '&newPassword=' + encodeURIComponent(newPw)
    })
            .then(r => r.json())
            .then(res => {
                if (res.success) {
                    setMsg('msg-security', 'success', 'เปลี่ยนรหัสผ่านสำเร็จ');
                    ['sec-old', 'sec-new', 'sec-confirm'].forEach(id => document.getElementById(id).value = '');
                } else {
                    setMsg('msg-security', 'error', res.message || 'รหัสผ่านปัจจุบันไม่ถูกต้อง');
                }
            });
}

/* ── Load History (AJAX → GetUserHistoryServlet) ── */
function loadHistory() {
    fetch('GetUserHistoryServlet')
            .then(r => r.json())
            .then(items => {
                const area = document.getElementById('history-area');
                if (!items.length) {
                    area.innerHTML = `<div class="no-data">คุณยังไม่มีประวัติการ Upload<br>
                                <small style="color:#aaa">ไปที่หน้า Upload เพื่อเพิ่มไฟล์</small></div>`;
                    return;
                }
                area.innerHTML =
                        `<table class="history-table">
                               <thead><tr><th>#</th><th>หัวข้อ</th><th>วิชา</th><th>วันที่ Upload</th><th>ไฟล์</th></tr></thead>
                               <tbody>${items.map((r, i) =>
                                `<tr>
                                      <td>${i + 1}</td>
                                      <td><span class="entry-link" onclick="location.href='index.html'">${esc(r.title)}</span></td>
                                      <td>${esc(r.subject)}</td>
                                      <td>${esc(r.uploadDate)}</td>
                                      <td style="text-align:center">${r.fileCount} ไฟล์</td>
                                    </tr>`).join('')}
                               </tbody>
                             </table>
                             <p style="font-size:0.83rem;color:#888;margin-top:8px;text-align:right">รวม ${items.length} รายการ</p>`;
            });
}

/* ── Upload Avatar (AJAX → UploadAvatarServlet) ── */
function uploadAvatar(input) {
    if (!input.files || !input.files[0])
        return;
    const formData = new FormData();
    formData.append('avatar', input.files[0]);
    fetch('UploadAvatarServlet', {method: 'POST', body: formData})
            .then(r => r.json())
            .then(res => {
                if (res.success) {
                    document.getElementById('avatar-preview').innerHTML =
                            `<img src="${esc(res.avatarUrl)}" alt="avatar"/>`;
                }
            });
}

/* ── Logout / handleAuth ── */
function handleAuth() {
    doLogout();
}
function doLogout() {
    fetch('LogoutServlet', {method: 'POST'})
            .finally(() => {
                sessionStorage.removeItem(SESSION_KEY);
                location.href = 'login.html';
            });
}

/* ── Helpers ── */
function toggleEye(id) {
    const el = document.getElementById(id);
    if (el)
        el.type = el.type === 'password' ? 'text' : 'password';
}
function setMsg(id, type, text) {
    const el = document.getElementById(id);
    if (!el)
        return;
    el.className = 'msg ' + type;
    el.textContent = text;
    if (type === 'success')
        setTimeout(() => {
            if (el)
                el.textContent = '';
        }, 3000);
}
function esc(s) {
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

init();