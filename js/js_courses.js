            var SESSION_KEY = 'idx_user';

            /* ─── Init ─── */
            function init() {
                var user = getUser();
                updateNav(user);
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

            /* ─── โหลดวิชาทั้งหมด (AJAX GET → CoursesServlet) ─── */
            function loadCourses() {
                var acc = document.getElementById('accordion');
                fetch('CoursesServlet')
                        .then(function (r) {
                            console.log('[CoursesServlet] HTTP status:', r.status);
                            if (!r.ok) {
                                throw new Error('HTTP ' + r.status);
                            }
                            return r.text();
                        })
                        .then(function (text) {
                            console.log('[CoursesServlet] raw response:', text.substring(0, 300));
                            var courses;
                            try {
                                courses = JSON.parse(text);
                            } catch (e) {
                                throw new Error('JSON parse error: ' + e.message + ' | body: ' + text.substring(0, 200));
                            }
                            renderCourses(courses);
                        })
                        .catch(function (err) {
                            console.error('[CoursesServlet] error:', err);
                            acc.innerHTML = '<div class="error-msg">ไม่สามารถโหลดข้อมูลวิชาได้<br><small style="color:#999">' + err.message + '</small></div>';
                        });
            }

            /* ─── Render accordion จาก JSON ─── */
            function renderCourses(courses) {
                var acc = document.getElementById('accordion');
                console.log('[renderCourses] received', courses ? courses.length : 0, 'courses');

                if (!courses || courses.length === 0) {
                    acc.innerHTML = '<div class="no-data">ยังไม่มีข้อมูลรายวิชา</div>';
                    return;
                }

                acc.innerHTML = courses.map(function (c, i) {
                    /* schedules — CoursesServlet ส่งเป็น Array of string */
                    var schedHtml = (c.schedules || []).map(function (s) {
                        return esc(s);
                    }).join('<br>') || '-';

                    /* links — CoursesServlet ส่งเป็น [{label, url}, ...] */
                    var linkHtml = (c.links || []).map(function (lk) {
                        return '<a class="link-btn" href="' + esc(lk.url) + '" target="_blank">' +
                                '<span>' + esc(lk.label) + '</span>' +
                                '<span class="link-check">&#10003;</span>' +
                                '</a>';
                    }).join('');

                    /* credits อาจเป็น number หรือ string */
                    var credits = (c.credits !== undefined && c.credits !== null) ? String(c.credits) : '-';

                    return '<div class="course-row">' +
                            '<div class="course-header" id="hdr-' + i + '" onclick="toggleCourse(' + i + ')">' +
                            '<span class="course-code-name">' + esc(c.code) + ' - ' + esc(c.name) + '</span>' +
                            '<span class="arrow" id="arrow-' + i + '">&#9658;</span>' +
                            '</div>' +
                            '<div class="course-detail" id="detail-' + i + '">' +
                            '<div class="detail-inner">' +
                            '<table class="info-table">' +
                            '<tr><td>จำนวนหน่วยกิต</td><td>' + esc(credits) + '</td></tr>' +
                            '<tr><td>PR</td><td>' + esc(c.pr || 'None') + '</td></tr>' +
                            '<tr><td>เวลาเรียน</td><td>' + schedHtml + '</td></tr>' +
                            '<tr><td>วันสอบ</td><td>' + esc(c.exam || 'จัดสอบเอง') + '</td></tr>' +
                            '</table>' +
                            '<div class="link-btns">' + linkHtml + '</div>' +
                            '</div>' +
                            '<div class="course-desc-block">' +
                            '<div class="course-desc-label">คำอธิบายรายวิชา</div>' +
                            '<div class="course-desc-text">' + esc(c.desc || '-') + '</div>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                }).join('');

                /* ลบ loading message */
                var loadingMsg = document.getElementById('loading-msg');
                if (loadingMsg)
                    loadingMsg.remove();
            }

            function toggleCourse(idx) {
                var detail = document.getElementById('detail-' + idx);
                var arrow = document.getElementById('arrow-' + idx);
                var header = document.getElementById('hdr-' + idx);
                var isOpen = detail.classList.contains('open');
                if (isOpen) {
                    detail.classList.remove('open');
                    header.classList.remove('open');
                    arrow.innerHTML = '&#9658;';
                } else {
                    detail.classList.add('open');
                    header.classList.add('open');
                    arrow.innerHTML = '&#9660;';
                }
            }

            function esc(s) {
                return String(s || '')
                        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
                        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
            }

            init();