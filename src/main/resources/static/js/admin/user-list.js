$(document).ready(function () {
    let currentPage = 0;
    const itemsPerPage = 10;
    let totalPages = 0;

    if (window.location.pathname === "/admin/users") {
        fetchUsers(currentPage);
    }

    function fetchUsers(page) {
        $.ajax({
            url: `/api/admin/users?page=${page}&size=${itemsPerPage}`,
            method: 'GET',
            success: function (response) {
                update(response);
            },
            error: function (error) {
                console.error('Error fetching users:', error);
            }
        });
    }

    function renderUsers(users) {
        const userList = $('#user-list');
        userList.empty();
        users.forEach((user) => {
            const tr = `
                <tr data-user-id="${user.userId}">
                <td>${user.userId}</td>
                <td>${user.email}</td>
                <td>${user.nickname}</td>
                <td>${user.role}</td>
                <td>${user.level}</td>
                <td>${user.readyForUpgrade ? 'Yes' : 'No'}</td>
                <td>${user.active ? '활성' : '정지'}</td>
                <td>${formatDate(user.createdAt)}</td>
                <td>${formatDate(user.lastLoginedAt)}</td>
                <td>${user.deletedAt ? formatDate(user.deletedAt) : ''}</td>
                <td>
                    ${user.deletedAt ? "" : `<button class="stop-btn">${user.active ? "정지하기" : "정지해제"}</button>`}
                </td>
                <td>
                    ${user.deletedAt ? "" : '<button class="delete-btn">탈퇴하기</button>'}
                </td>
            </tr>
        `;
            userList.append(tr);
        });
    }

    function toggleUserStatus(userId, isActive) {
        const action = isActive ? "정지" : "정지해제";
        if (confirm(`${action} 하시겠습니까?`)) {
            $.ajax({
                url: `/api/admin/users/${userId}`,
                type: 'PUT',
                contentType: 'application/x-www-form-urlencoded',
                success: function(response) {
                    alert(`${action}되었습니다.`);
                    // 버튼 텍스트 업데이트
                    const buttonElement = $('.stop-btn').filter(function() {
                        return $(this).closest('tr').data('user-id') === userId;
                    });

                    buttonElement.text(isActive ? "정지해제" : "정지하기");

                    // 상태 셀 업데이트
                    const statusCell = buttonElement.closest('tr').find('td').eq(6);
                    statusCell.text(isActive ? "정지" : "활성");
                },
                error: function(xhr) {
                    alert('오류가 발생했습니다.');
                }
            });
        }
    }

    function deleteUser(userId) {
        if (confirm(`탈퇴 처리 하시겠습니까?`)) {
            $.ajax({
                url: `/api/admin/users/${userId}`,
                type: 'DELETE',
                contentType: 'application/x-www-form-urlencoded',
                success: function(response) {
                    alert(`탈퇴 처리 되었습니다.`);

                    // 행 요소 찾기
                    const closestTr = $('.delete-btn').filter(function() {
                        return $(this).closest('tr').data('user-id') === userId;
                    }).closest('tr');

                    // 버튼 요소 찾기 및 제거
                    closestTr.find('.delete-btn').remove();
                    closestTr.find('.stop-btn').remove(); // "정지하기" 버튼 제거

                    // 상태 셀 업데이트
                    const deletedCell = closestTr.find('td').eq(9);
                    deletedCell.text(formatDate(response));

                },
                error: function(xhr) {
                    alert('오류가 발생했습니다.');
                }
            });
        }
    }

    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        // 연도, 월, 일, 시간, 분, 초, 밀리초를 포함한 문자열 생성
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        const milliseconds = String(date.getMilliseconds()).padStart(3, '0');

        return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
    }

    function update(pageData) {
        renderUsers(pageData.content);
        totalPages = pageData.totalPages;
        currentPage = pageData.number;
        renderPagination(pageData);
    }

    function renderPagination(pageData) {
        const paginationEl = $('.pagination');
        paginationEl.empty();

        paginationEl.append(`<button id="prev-page" class="pagination-button" ${pageData.first ? 'disabled' : ''}> << </button>`);

        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            paginationEl.append(`<button class="pagination-button ${i === currentPage ? 'active' : ''}">${i + 1}</button>`);
        }

        paginationEl.append(`<button id="next-page" class="pagination-button" ${pageData.last ? 'disabled' : ''}> >> </button>`);
        paginationEl.append(`<span id="page-info">Page ${currentPage + 1} of ${totalPages}</span>`);
    }

    $(document).on('click', '.pagination-button', function() {
        if ($(this).attr('id') === 'prev-page') {
            if (currentPage > 0) currentPage--;
        } else if ($(this).attr('id') === 'next-page') {
            if (currentPage < totalPages - 1) currentPage++;
        } else {
            currentPage = parseInt($(this).text()) - 1;
        }
        fetchUsers(currentPage);
    });

    $(document).on('click', '.stop-btn', function() {
        const closestTr = $(this).closest('tr');
        const userId = closestTr.data('user-id');
        const isActive = $(this).text() === "정지하기";

        toggleUserStatus(userId, isActive);
    });

    $(document).on('click', '.delete-btn', function() {
        const closestTr = $(this).closest('tr');
        const userId = closestTr.data('user-id');

        deleteUser(userId);
    });
});
