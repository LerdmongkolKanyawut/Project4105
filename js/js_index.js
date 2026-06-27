/* =====================================================
 SESSION_KEY — ตรงกับที่ login.html เก็บไว้
 LoginServlet รับ POST และ return JSON กลับมา
 login.html บันทึก user ลง sessionStorage
 ===================================================== */
const SESSION_KEY = 'idx_user';   /* sessionStorage key (fallback UI) */

/* ─── อ่าน user จาก sessionStorage (set โดย login.html → LoginServlet) ─── */
function getUser() {
    try {
        return JSON.parse(sessionStorage.getItem(SESSION_KEY));
    } catch {
        return null;
    }
}

/* ─── อัปเดต Nav bar ─── */
function updateNav() {
    const u = getUser();
    document.getElementById('nav-welcome').textContent =
            u ? 'Welcome, ' + u.firstname : 'Welcome, Guest';
    document.getElementById('btn-auth').textContent = u ? 'Logout' : 'Login';
    document.getElementById('btn-profile').style.display =
            u ? 'inline-block' : 'none';
    document.getElementById('btn-mgmt').style.display =
            (u && u.role === 'Admin') ? 'inline-block' : 'none';

    /* ถ้า login อยู่ → enable comment input */
    const textarea = document.getElementById('comment-input');
    if (textarea) {
        textarea.disabled = !u;
        textarea.placeholder = u
                ? 'แสดงความคิดเห็น...'
                : 'กรุณาเข้าสู่ระบบก่อนแสดงความคิดเห็น';
    }
    document.getElementById('comment-guest-note').textContent = u
            ? 'แสดงความคิดเห็นในชื่อ: ' + u.firstname
            : '* กรุณาเข้าสู่ระบบเพื่อแสดงความคิดเห็น';
}

/* ─── Login / Logout ─── */
function handleAuth() {
    if (getUser()) {
        /* Logout: เรียก LogoutServlet (invalidate session ฝั่ง server)
         แล้วล้าง sessionStorage */
        fetch('LogoutServlet', {method: 'POST'})
                .finally(() => {
                    sessionStorage.removeItem(SESSION_KEY);
                    updateNav();
                    loadEntries();   /* โหลด list ใหม่ (comment box จะ disabled) */
                });
    } else {
        location.href = 'login.html';
    }
}

/* =====================================================
 LIST VIEW
 ===================================================== */
function showList() {
    document.getElementById('view-list').style.display = 'block';
    document.getElementById('view-detail').style.display = 'none';
}

/* ─── โหลด entries ทั้งหมด (AJAX → GetEntriesServlet) ─── */
function loadEntries() {
    fetch('GetEntriesServlet')
            .then(r => r.json())
            .then(entries => renderTable(entries))
            .catch(() => {
                document.getElementById('file-tbody').innerHTML =
                        '<tr><td colspan="2" style="text-align:center;color:#c00;padding:20px;">' +
                        'ไม่สามารถโหลดข้อมูลได้ กรุณาลองใหม่</td></tr>';
            });
}

function renderTable(entries) {
    const tbody = document.getElementById('file-tbody');
    if (!entries.length) {
        tbody.innerHTML =
                '<tr><td colspan="2" style="text-align:center;color:#999;padding:20px;">' +
                'ยังไม่มีรายการ</td></tr>';
        return;
    }
    tbody.innerHTML = entries.map((e, i) => `
                    <tr>
                        <td>${i + 1}</td>
                        <td>
                            <span class="entry-title"
                                  onclick="openDetail(${e.entryId})">${esc(e.title)}</span>
                            <div class="entry-meta">
                                ผู้เขียน: ${esc(e.author)}<br>
                                วิชา: ${esc(e.subject)}<br>
                                วันที่ Upload: ${esc(e.uploadDate)}
                            </div>
                        </td>
                    </tr>`).join('');
}

/* =====================================================
 DETAIL VIEW
 ===================================================== */
let currentEntryId = null;

/* ─── เปิด detail (AJAX → GetEntryServlet) ─── */
function openDetail(entryId) {
    currentEntryId = entryId;
    fetch('GetEntryServlet?entryId=' + entryId)
            .then(r => r.json())
            .then(data => {
                document.getElementById('det-title').textContent = data.title;
                document.getElementById('det-author').textContent = data.author;
                document.getElementById('det-date').textContent = data.uploadDate;
                document.getElementById('det-subject').textContent = data.subject;
                document.getElementById('det-desc').textContent = data.desc;

                /* Files */
                const filesEl = document.getElementById('det-files');
                filesEl.innerHTML = '';
                (data.files || []).forEach(f => {
                    const item = document.createElement('div');
                    item.className = 'file-item';
                    item.innerHTML =
                            fileIconSvg(f.fileType) +
                            `<div class="file-info">
                                     <span class="file-name"
                                           title="${esc(f.fileName)}">${esc(f.fileName)}</span>
                                     <a class="file-dl"
                                        href="DownloadServlet?fileId=${f.fileId}"
                                        download="${esc(f.fileName)}">Download</a>
                                 </div>`;
                    filesEl.appendChild(item);
                });

                loadComments(entryId);
                document.getElementById('view-list').style.display = 'none';
                document.getElementById('view-detail').style.display = 'block';
            })
            .catch(() => alert('เกิดข้อผิดพลาดในการโหลดข้อมูล'));
}

/* =====================================================
 COMMENTS
 ===================================================== */
/* ─── โหลด comments (AJAX → GetCommentsServlet) ─── */
function loadComments(entryId) {
    fetch('GetCommentsServlet?entryId=' + entryId)
            .then(r => r.json())
            .then(items => {
                const list = document.getElementById('comments-list');
                if (!items.length) {
                    list.innerHTML =
                            '<p style="color:#888;font-size:0.9rem;margin-bottom:10px;">' +
                            'ยังไม่มีความคิดเห็น</p>';
                    return;
                }
                list.innerHTML = items.map(c =>
                        `<div class="comment-item">
                                 <span class="comment-author">${esc(c.author)}</span>
                                 <span class="comment-date">${esc(c.createdAt)}</span>
                                 <div class="comment-text">${esc(c.comment)}</div>
                             </div>`
                ).join('');
            });
}

/* ─── ส่ง comment (AJAX → AddCommentServlet) ─── */
function submitComment() {
    if (!getUser()) {
        alert('กรุณาเข้าสู่ระบบก่อนแสดงความคิดเห็น');
        return;
    }
    const input = document.getElementById('comment-input');
    const text = input.value.trim();
    if (!text) {
        alert('กรุณากรอกความคิดเห็น');
        return;
    }

    fetch('AddCommentServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'entryId=' + currentEntryId
                + '&comment=' + encodeURIComponent(text)
    })
            .then(r => r.json())
            .then(res => {
                if (res.success) {
                    input.value = '';
                    loadComments(currentEntryId);
                } else {
                    alert(res.message || 'เกิดข้อผิดพลาด');
                }
            });
}

/* =====================================================
 HELPERS
 ===================================================== */
function fileIconSvg(type) {
    const colors = {
        pdf: '#e53935', docx: '#1565c0', doc: '#1565c0',
        pptx: '#e65100', ppt: '#e65100', xlsx: '#2e7d32', xls: '#2e7d32'
    };
    const color = colors[(type || '').toLowerCase()] || '#555';
    const label = (type || 'FILE').toUpperCase().slice(0, 4);
    return `<div class="file-icon">
                  <svg viewBox="0 0 46 56" xmlns="http://www.w3.org/2000/svg">
                    <rect x="1" y="1" width="44" height="54" rx="3" fill="#fff"
                          stroke="#ccc" stroke-width="1.5"/>
                    <polygon points="29,1 45,17 29,17" fill="#e0e0e0"/>
                    <line x1="29" y1="1" x2="29" y2="17" stroke="#ccc" stroke-width="1.5"/>
                    <line x1="29" y1="17" x2="45" y2="17" stroke="#ccc" stroke-width="1.5"/>
                    <rect x="0" y="28" width="46" height="18" rx="2" fill="${color}"/>
                    <text x="23" y="41" text-anchor="middle" font-size="9" font-weight="700"
                          font-family="Arial,sans-serif" fill="#fff"
                          letter-spacing="0.5">${label}</text>
                  </svg></div>`;
}

function esc(s) {
    return String(s)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

/* =====================================================
 INIT
 ===================================================== */
updateNav();
loadEntries();