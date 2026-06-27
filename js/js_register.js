/* ─── ถ้า login อยู่แล้ว ไปหน้าหลักทันที ─── */
if (sessionStorage.getItem('idx_user')) {
    location.href = 'index.html';
}

var selectedRole = '';

function selectRole(r, cardId) {
    document.querySelectorAll('.role-card')
            .forEach(function (c) {
                c.classList.remove('selected');
            });
    document.getElementById(cardId).classList.add('selected');
    selectedRole = r;
}

function goToForm() {
    if (!selectedRole) {
        alert('กรุณาเลือกตำแหน่งก่อน');
        return;
    }
    document.getElementById('role-display').value = selectedRole;
    ['reg-firstname', 'reg-lastname', 'reg-email',
        'reg-username', 'reg-pass', 'reg-confirm']
            .forEach(function (id) {
                document.getElementById(id).value = '';
            });
    setMsg('', '');
    document.getElementById('step-select').style.display = 'none';
    document.getElementById('step-form').style.display = 'block';
}

function goBack() {
    document.getElementById('step-form').style.display = 'none';
    document.getElementById('step-select').style.display = 'block';
}

function togglePwd(id) {
    var el = document.getElementById(id);
    el.type = el.type === 'password' ? 'text' : 'password';
}

/* ─── Register: AJAX POST ไป RegisterServlet ─── */
function doRegister() {
    var firstname = document.getElementById('reg-firstname').value.trim();
    var lastname = document.getElementById('reg-lastname').value.trim();
    var email = document.getElementById('reg-email').value.trim();
    var username = document.getElementById('reg-username').value.trim();
    var password = document.getElementById('reg-pass').value;
    var confirm = document.getElementById('reg-confirm').value;

    if (!firstname || !lastname || !email || !username || !password || !confirm) {
        setMsg('error', 'กรุณากรอกข้อมูลให้ครบทุกช่อง');
        return;
    }
    if (password !== confirm) {
        setMsg('error', 'Password ไม่ตรงกัน');
        return;
    }
    if (password.length < 4) {
        setMsg('error', 'Password ต้องมีอย่างน้อย 4 ตัวอักษร');
        return;
    }

    var btn = document.getElementById('btn-submit');
    btn.disabled = true;
    btn.textContent = 'กำลังสมัคร...';

    fetch('RegisterServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'firstname=' + encodeURIComponent(firstname)
                + '&lastname=' + encodeURIComponent(lastname)
                + '&email=' + encodeURIComponent(email)
                + '&username=' + encodeURIComponent(username)
                + '&password=' + encodeURIComponent(password)
                + '&role=' + encodeURIComponent(selectedRole)
    })
            .then(function (r) {
                return r.json();
            })
            .then(function (res) {
                if (res.success) {
                    /* ไปหน้า login พร้อม flag registered=1 */
                    location.href = 'login.html?registered=1';
                } else {
                    setMsg('error', res.message || 'เกิดข้อผิดพลาด กรุณาลองใหม่');
                    btn.disabled = false;
                    btn.textContent = 'สมัครสมาชิก';
                }
            })
            .catch(function () {
                setMsg('error', 'ไม่สามารถเชื่อมต่อ server ได้ กรุณาลองใหม่');
                btn.disabled = false;
                btn.textContent = 'สมัครสมาชิก';
            });
}

/* ─── ดึง context path จาก URL ปัจจุบัน ───────────────────────────
 ตัวอย่าง:
 URL: http://localhost:8080/myapp/register.html → ctx = '/myapp'
 URL: http://localhost:8080/register.html       → ctx = ''
 ------------------------------------------------------------------ */
function getContextPath() {
    var path = location.pathname;
    var parts = path.split('/');
    if (parts.length >= 3 && parts[2] !== '') {
        return '/' + parts[1];
    }
    return '';
}

function setMsg(type, text) {
    var el = document.getElementById('msg');
    el.className = type ? 'msg ' + type : 'msg';
    el.textContent = text;
}