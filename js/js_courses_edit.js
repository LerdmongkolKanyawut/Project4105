var SESSION_KEY = 'idx_user';
var allCourses = [];
var editingId = null; // null = add, number = edit

/* ── Init ── */
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
    loadCourses();
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

function getServletBase() {
    var pathname = window.location.pathname;
    var base = pathname.substring(0, pathname.lastIndexOf('/admin/'));
    return base;
}
var CTX = getServletBase();

/* ── Load ── */
function loadCourses() {
    fetch(CTX + '/CoursesServlet')
            .then(function (r) {
                return r.json();
            })
            .then(function (data) {
                allCourses = data;
                renderTable(data);
            })
            .catch(function () {
                document.getElementById('table-wrap').innerHTML =
                        '<div class="error-msg">ไม่สามารถโหลดข้อมูลวิชาได้</div>';
            });
}

/* ── Render Table ── */
function renderTable(courses) {
    var wrap = document.getElementById('table-wrap');
    if (!courses.length) {
        wrap.innerHTML = '<div class="empty-msg">ยังไม่มีรายวิชา</div>';
        return;
    }
    var html = '<table class="data-table"><thead><tr>' +
            '<th>#</th><th>รหัสวิชา</th><th>ชื่อวิชา</th><th>หน่วยกิต</th><th>PR</th><th>วันสอบ</th><th style="text-align:center">จัดการ</th>' +
            '</tr></thead><tbody>';
    courses.forEach(function (c, i) {
        html += '<tr>' +
                '<td>' + (i + 1) + '</td>' +
                '<td><b>' + esc(c.code) + '</b></td>' +
                '<td>' + esc(c.name) + '</td>' +
                '<td style="text-align:center">' + esc(String(c.credits)) + '</td>' +
                '<td>' + esc(c.pr || 'None') + '</td>' +
                '<td>' + esc(c.exam || '-') + '</td>' +
                '<td style="text-align:center;white-space:nowrap">' +
                '<button class="btn" style="padding:3px 12px;font-size:0.83rem;margin-right:4px" onclick="openEditModal(' + i + ')">&#9998; แก้ไข</button>' +
                '<button class="btn danger" style="padding:3px 12px;font-size:0.83rem" onclick="deleteCourse(' + esc(String(c.courseId || '')) + ',\'' + esc(c.code) + '\')">&#10006; ลบ</button>' +
                '</td>' +
                '</tr>';
    });
    wrap.innerHTML = html + '</tbody></table>';
}

/* ── Modal: Add ── */
function openAddModal() {
    editingId = null;
    document.getElementById('modal-title').textContent = '+ เพิ่มรายวิชา';
    clearForm();
    addSchedRow();
    addLinkRow();
    document.getElementById('modal').classList.add('open');
}

/* ── Modal: Edit ── */
function openEditModal(idx) {
    var c = allCourses[idx];
    editingId = c.courseId || null;
    document.getElementById('modal-title').textContent = '✎ แก้ไขวิชา ' + c.code;
    clearForm();

    document.getElementById('f-code').value = c.code || '';
    document.getElementById('f-name').value = c.name || '';
    document.getElementById('f-credit').value = c.credits || '';
    document.getElementById('f-pr').value = c.pr || '';
    document.getElementById('f-exam').value = c.exam || '';
    document.getElementById('f-desc').value = c.desc || '';

    // schedules
    (c.schedules || []).forEach(function (s) {
        addSchedRow(s);
    });
    if (!(c.schedules && c.schedules.length))
        addSchedRow();

    // links
    (c.links || []).forEach(function (lk) {
        addLinkRow(lk.label, lk.url);
    });
    if (!(c.links && c.links.length))
        addLinkRow();

    document.getElementById('modal').classList.add('open');
}

function clearForm() {
    ['f-code', 'f-name', 'f-credit', 'f-pr', 'f-exam', 'f-desc'].forEach(function (id) {
        document.getElementById(id).value = '';
    });
    document.getElementById('sched-list').innerHTML =
            '<div class="sched-header">' +
            '<span>ประเภทคาบ</span><span>วัน</span><span>เวลาเริ่ม</span><span>เวลาสิ้นสุด</span><span>ห้องเรียน</span><span></span>' +
            '</div>';
    document.getElementById('link-list').innerHTML = '';
    document.getElementById('modal-msg').textContent = '';
}

function closeModal() {
    document.getElementById('modal').classList.remove('open');
}
function closeOutside(e) {
    if (e.target === document.getElementById('modal'))
        closeModal();
}

/* ── Dynamic rows ── */
function addSchedRow(val) {
    var section = '', day = '', timeStart = '', timeEnd = '', room = '';
    if (val && typeof val === 'object') {
        section = val.section || '';
        day = val.day || '';
        timeStart = val.timeStart || '';
        timeEnd = val.timeEnd || '';
        room = val.room || '';
    } else if (val && typeof val === 'string' && val.trim()) {
        // parse string เดิม เช่น "Sec.1 วันอังคาร 11:30-13:20 SCL209"
        var parts = val.trim().split(/\s+/);
        parts.forEach(function (p) {
            if (/วัน/.test(p)) {
                day = p;
            } else if (/^\d{1,2}:\d{2}-\d{1,2}:\d{2}$/.test(p)) {
                var times = p.split('-');
                timeStart = times[0];
                timeEnd = times[1];
            } else if (/^(Lec|Lab|Sec|lec|lab|sec)/i.test(p)) {
                section = p;
            } else if (p && !day.includes(p) && !timeStart.includes(p)) {
                room = p;
            }
        });
    }

    var days = ['วันจันทร์', 'วันอังคาร', 'วันพุธ', 'วันพฤหัสบดี', 'วันศุกร์', 'วันเสาร์', 'วันอาทิตย์'];
    var dayOpts = days.map(function (d) {
        return '<option value="' + d + '"' + (d === day ? ' selected' : '') + '>' + d + '</option>';
    }).join('');

    var secTypes = ['Lec.', 'Lab.', 'Sec.1', 'Sec.2', 'Sec.3', 'Sec.4', 'Sec.5'];
    var secOpts = secTypes.map(function (s) {
        return '<option value="' + s + '"' + (s === section ? ' selected' : '') + '>' + s + '</option>';
    }).join('');

    var div = document.createElement('div');
    div.className = 'sched-item';
    div.innerHTML =
            '<select class="s-section">' +
            '<option value="">-- ประเภท --</option>' + secOpts +
            '</select>' +
            '<select class="s-day"><option value="">-- วัน --</option>' + dayOpts + '</select>' +
            '<input type="time" class="s-start" value="' + esc(timeStart) + '"/>' +
            '<input type="time" class="s-end"   value="' + esc(timeEnd) + '"/>' +
            '<input type="text" class="s-room"  value="' + esc(room) + '" placeholder="SCL209"/>' +
            '<button class="dyn-remove" onclick="this.parentNode.remove()">ลบ</button>';
    document.getElementById('sched-list').appendChild(div);
}
function addLinkRow(label, url) {
    var div = document.createElement('div');
    div.className = 'dyn-item';
    div.innerHTML = '<input type="text" placeholder="ชื่อลิงก์ เช่น Google Classroom" style="max-width:180px" value="' + esc(label || '') + '"/>' +
            '<input type="text" placeholder="URL เช่น https://..." value="' + esc(url || '') + '"/>' +
            '<button class="dyn-remove" onclick="this.parentNode.remove()">ลบ</button>';
    document.getElementById('link-list').appendChild(div);
}

/* ── Save ── */
function saveCourse() {
    var code = document.getElementById('f-code').value.trim();
    var name = document.getElementById('f-name').value.trim();
    var credit = document.getElementById('f-credit').value.trim();
    var pr = document.getElementById('f-pr').value.trim();
    var exam = document.getElementById('f-exam').value.trim();
    var desc = document.getElementById('f-desc').value.trim();
    var msgEl = document.getElementById('modal-msg');

    if (!code || !name || !credit) {
        msgEl.textContent = 'กรุณากรอกรหัสวิชา ชื่อวิชา และหน่วยกิต';
        return;
    }

    // schedules — อ่านจาก 5 fields แล้วรวมเป็น string
    var scheds = [];
    document.querySelectorAll('#sched-list .sched-item').forEach(function (row) {
        var section = row.querySelector('.s-section').value.trim();
        var day = row.querySelector('.s-day').value.trim();
        var start = row.querySelector('.s-start').value.trim();
        var end = row.querySelector('.s-end').value.trim();
        var room = row.querySelector('.s-room').value.trim();
        if (day || start || end || room) {
            var s = '';
            if (section)
                s += section + ' ';
            if (day)
                s += day + ' ';
            if (start)
                s += start;
            if (end)
                s += '-' + end;
            if (room)
                s += ' ' + room;
            scheds.push(s.trim());
        }
    });

    // links
    var links = [];
    document.querySelectorAll('#link-list .dyn-item').forEach(function (row) {
        var inputs = row.querySelectorAll('input');
        var lbl = inputs[0].value.trim();
        var url = inputs[1].value.trim();
        if (lbl && url)
            links.push({label: lbl, url: url});
    });

    var payload = {
        courseId: editingId,
        code: code,
        name: name,
        credit: parseInt(credit),
        pr: pr,
        exam: exam,
        desc: desc,
        schedules: scheds,
        links: links
    };

    var btn = document.getElementById('btn-save');
    btn.disabled = true;
    msgEl.textContent = '';

    fetch(CTX + '/SaveCourseServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json; charset=UTF-8'},
        body: JSON.stringify(payload)
    })
            .then(function (r) {
                return r.json();
            })
            .then(function (res) {
                btn.disabled = false;
                if (res.success) {
                    closeModal();
                    showToast('success', editingId ? 'แก้ไขวิชาสำเร็จ' : 'เพิ่มวิชาสำเร็จ');
                    loadCourses();
                } else {
                    msgEl.textContent = res.message || 'เกิดข้อผิดพลาด';
                }
            })
            .catch(function () {
                btn.disabled = false;
                msgEl.textContent = 'ไม่สามารถเชื่อมต่อ server ได้';
            });
}

/* ── Delete ── */
function deleteCourse(courseId, code) {
    if (!confirm('ยืนยันลบวิชา "' + code + '" ?\nข้อมูลตารางเรียนและลิงก์จะถูกลบทั้งหมด'))
        return;
    fetch(CTX + '/DeleteCourseServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'courseId=' + encodeURIComponent(courseId)
    })
            .then(function (r) {
                return r.json();
            })
            .then(function (res) {
                if (res.success) {
                    showToast('success', 'ลบวิชา "' + code + '" สำเร็จ');
                    loadCourses();
                } else {
                    showToast('error', res.message || 'ลบไม่สำเร็จ');
                }
            })
            .catch(function () {
                showToast('error', 'ไม่สามารถเชื่อมต่อ server ได้');
            });
}

/* ── Toast ── */
function showToast(type, msg) {
    var t = document.getElementById('toast');
    t.textContent = msg;
    t.className = 'toast ' + type + ' show';
    setTimeout(function () {
        t.className = 'toast';
    }, 2800);
}

function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

init();