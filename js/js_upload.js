var SESSION_KEY = 'idx_user';
var selectedFiles = [];
var dt = new DataTransfer();

/* ─── Init ─── */
function init() {
    var user = getUser();
    updateNav(user);

    /* แสดง success/error จาก UploadServlet (ผ่าน query string) */
    var params = new URLSearchParams(location.search);
    if (params.get('success') === '1') {
        document.getElementById('server-msg').className = 'msg success';
        document.getElementById('server-msg').textContent = 'Upload ไฟล์สำเร็จ';
    } else if (params.get('error')) {
        document.getElementById('server-msg').className = 'msg error';
        document.getElementById('server-msg').textContent = decodeURIComponent(params.get('error'));
    }

    if (user) {
        /* ใส่ชื่อผู้เขียนและวันที่ */
        document.getElementById('f-author').value = user.firstname || '';
        var now = new Date();
        document.getElementById('f-date').value =
                now.getDate() + '/' + (now.getMonth() + 1) + '/' + now.getFullYear();
        document.getElementById('area-guest').style.display = 'none';
        document.getElementById('area-upload').style.display = 'block';
    } else {
        document.getElementById('area-guest').style.display = 'block';
        document.getElementById('area-upload').style.display = 'none';
    }
}

function getUser() {
    try {
        return JSON.parse(sessionStorage.getItem(SESSION_KEY));
    } catch (e) {
        return null;
    }
}

function updateNav(user) {
    document.getElementById('nav-welcome').textContent =
            user ? 'Welcome, ' + user.firstname : 'Welcome, Guest';
    document.getElementById('btn-auth').textContent = user ? 'Logout' : 'Login';
    document.getElementById('btn-profile').style.display = user ? 'inline-block' : 'none';
    document.getElementById('btn-mgmt').style.display = (user && user.role === 'Admin') ? 'inline-block' : 'none';
}

function handleAuth() {
    if (getUser()) {
        fetch('LogoutServlet', {method: 'POST'})
                .finally(function () {
                    sessionStorage.removeItem(SESSION_KEY);
                    location.href = 'login.html';
                });
    } else {
        location.href = 'login.html';
    }
}

/* ─── File handling ─── */
function addFiles(input) {
    Array.from(input.files).forEach(function (f) {
        if (!selectedFiles.find(function (x) {
            return x.name === f.name && x.size === f.size;
        })) {
            selectedFiles.push(f);
            dt.items.add(f);
        }
    });
    document.getElementById('file-input').files = dt.files;
    renderFileCards();
}

function removeFile(idx) {
    selectedFiles.splice(idx, 1);
    dt = new DataTransfer();
    selectedFiles.forEach(function (f) {
        dt.items.add(f);
    });
    document.getElementById('file-input').files = dt.files;
    renderFileCards();
}

function renderFileCards() {
    var panel = document.getElementById('file-cards-panel');
    var emptyMsg = document.getElementById('file-empty-msg');
    var fcCount = document.getElementById('fc-count');
    var badge = document.getElementById('file-count-badge');
    var n = selectedFiles.length;
    if (fcCount)
        fcCount.textContent = n;
    if (badge) {
        badge.textContent = n > 0 ? 'เลือกแล้ว ' + n + ' ไฟล์' : 'ยังไม่ได้เลือกไฟล์';
        badge.className = n > 0 ? 'file-count-badge has-files' : 'file-count-badge';
    }
    panel.querySelectorAll('.file-card,.add-more-row').forEach(function (el) {
        el.remove();
    });
    if (n === 0) {
        if (emptyMsg)
            emptyMsg.style.display = 'block';
        return;
    }
    if (emptyMsg)
        emptyMsg.style.display = 'none';
    selectedFiles.forEach(function (f, i) {
        var ext = f.name.split('.').pop().toLowerCase();
        var card = document.createElement('div');
        card.className = 'file-card';
        card.innerHTML =
                '<div class="fc-icon">' + fileIconSvg(ext) + '</div>' +
                '<div class="fc-info">' +
                '<div class="fc-name" title="' + esc(f.name) + '">' + esc(f.name) + '</div>' +
                '<div class="fc-size">' + formatSize(f.size) + '</div>' +
                '</div>' +
                '<button type="button" class="fc-del" onclick="removeFile(' + i + ')">&#10005;</button>';
        panel.appendChild(card);
    });
    var addRow = document.createElement('div');
    addRow.className = 'add-more-row';
    addRow.innerHTML = '<label class="add-more-btn" for="file-input">+ เพิ่มไฟล์</label>';
    panel.appendChild(addRow);
}

function doUpload() {
    var subj = document.getElementById('f-subject').value.trim();
    var title = document.getElementById('f-title').value.trim();
    if (!subj) {
        setMsg('error', 'กรุณากรอกรหัสวิชา');
        return;
    }
    if (!title) {
        setMsg('error', 'กรุณากรอกหัวข้อ');
        return;
    }
    if (selectedFiles.length === 0) {
        setMsg('error', 'กรุณาเลือกไฟล์อย่างน้อย 1 ไฟล์');
        return;
    }
    document.getElementById('uploadForm').submit();
}

function setMsg(type, text) {
    var el = document.getElementById('upload-msg');
    if (el) {
        el.className = 'msg ' + type;
        el.textContent = text;
    }
}

function fileIconSvg(ext) {
    var colors = {pdf: '#e53935', docx: '#1565c0', doc: '#1565c0', pptx: '#e65100', ppt: '#e65100', xlsx: '#2e7d32', xls: '#2e7d32', zip: '#546e7a', rar: '#546e7a', txt: '#888'};
    var color = colors[ext] || '#555';
    var label = ext.toUpperCase().slice(0, 4);
    return '<svg viewBox="0 0 28 34" xmlns="http://www.w3.org/2000/svg">' +
            '<rect x="1" y="1" width="26" height="32" rx="2" fill="#fff" stroke="#ccc" stroke-width="1.2"/>' +
            '<polygon points="18,1 27,10 18,10" fill="#e0e0e0"/>' +
            '<rect x="0" y="17" width="28" height="11" rx="1.5" fill="' + color + '"/>' +
            '<text x="14" y="25.5" text-anchor="middle" font-size="5.5" font-weight="700" font-family="Arial,sans-serif" fill="#fff">' + label + '</text>' +
            '</svg>';
}

function formatSize(bytes) {
    if (bytes < 1024)
        return bytes + ' B';
    if (bytes < 1048576)
        return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
}

function esc(s) {
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

init();