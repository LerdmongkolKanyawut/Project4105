/* ─── ถ้า login อยู่แล้ว ไปหน้าหลักทันที ─── */
if (sessionStorage.getItem('idx_user')) {
    location.href = 'index.html';
}

/* ─── แสดง success message จากหน้า register ─── */
const urlParams = new URLSearchParams(location.search);
if (urlParams.get('registered') === '1') {
    setMsg('success', 'สมัครสมาชิกสำเร็จ! กรุณาเข้าสู่ระบบ');
}

function togglePwd() {
    const p = document.getElementById('password');
    p.type = p.type === 'password' ? 'text' : 'password';
}

/* ─── Login: AJAX POST ไป LoginServlet ─── */
function doLogin() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    if (!username || !password) {
        setMsg('error', 'กรุณากรอก Username และ Password');
        return;
    }

    const btn = document.getElementById('btn-login');
    btn.disabled = true;
    btn.textContent = 'กำลังเข้าสู่ระบบ...';

    /* ใช้ getContextPath() ไม่ได้ใน HTML ธรรมดา
     → ใช้ /[ชื่อ context root]/LoginServlet แทน
     หรือถ้า deploy ที่ root ("/") ใช้ '/LoginServlet' ได้เลย
     ถ้า deploy เป็น /myapp ให้เปลี่ยนเป็น '/myapp/LoginServlet' */
    fetch('LoginServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'username=' + encodeURIComponent(username)
                + '&password=' + encodeURIComponent(password)
    })
            .then(function (r) {
                return r.json();
            })
            .then(function (res) {
                if (res.success) {
                    /* บันทึก user ลง sessionStorage (ตรงกับที่ index.html อ่าน) */
                    sessionStorage.setItem('idx_user', JSON.stringify({
                        username: res.username,
                        firstname: res.firstname,
                        role: res.role
                    }));
                    setMsg('success', 'เข้าสู่ระบบสำเร็จ กำลังโหลด...');
                    setTimeout(function () {
                        location.href = 'index.html';
                    }, 800);
                } else {
                    setMsg('error', res.message || 'Username หรือ Password ไม่ถูกต้อง');
                    btn.disabled = false;
                    btn.textContent = 'Login';
                }
            })
            .catch(function () {
                setMsg('error', 'ไม่สามารถเชื่อมต่อ server ได้ กรุณาลองใหม่');
                btn.disabled = false;
                btn.textContent = 'Login';
            });
}

/* ─── ดึง context path จาก URL ปัจจุบัน ───────────────────────────
 ตัวอย่าง:
 URL: http://localhost:8080/myapp/login.html → ctx = '/myapp'
 URL: http://localhost:8080/login.html       → ctx = ''
 ------------------------------------------------------------------ */
function getContextPath() {
    var path = location.pathname;          /* เช่น /myapp/login.html */
    var parts = path.split('/');
    /* parts[0]='' parts[1]='myapp' parts[2]='login.html' */
    if (parts.length >= 3 && parts[2] !== '') {
        /* มี context path → คืน /myapp */
        return '/' + parts[1];
    }
    /* deploy ที่ root → คืน '' */
    return '';
}

function setMsg(type, text) {
    const el = document.getElementById('msg');
    el.className = 'msg ' + type;
    el.textContent = text;
}

document.addEventListener('keydown', function (e) {
    if (e.key === 'Enter')
        doLogin();
});