$(document).ready(function () {
    let currentPage = 0;  // 서버 페이지네이션은 0부터 시작합니다
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
                <tr>
                <td>${user.userId}</td>
                <td>${user.email}</td>
                <td>${user.nickname}</td>
                <td>${user.level}</td>
                <td>${user.readyForUpgrade !== undefined ? (user.readyForUpgrade ? 'Yes' : 'No') : ''}</td>
                <td>${user.active !== undefined ? (user.active ? 'Active' : 'Inactive') : ''}</td>
                <td>${formatDate(user.createdAt)}</td>
                <td>${formatDate(user.lastLoginedAt)}</td>
                <td>${user.deletedAt ? formatDate(user.deletedAt) : ''}</td>
            </tr>
        `;
            userList.append(tr);
        });
    }

    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        });
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

        // 이전 페이지 버튼
        paginationEl.append(`<button id="prev-page" class="pagination-button" ${pageData.first ? 'disabled' : ''}> << </button>`);

        // 페이지 번호
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            paginationEl.append(`<button class="pagination-button ${i === currentPage ? 'active' : ''}">${i + 1}</button>`);
        }

        // 다음 페이지 버튼
        paginationEl.append(`<button id="next-page" class="pagination-button" ${pageData.last ? 'disabled' : ''}> >> </button>`);

        // 페이지 정보
        paginationEl.append(`<span id="page-info">Page ${currentPage + 1} of ${totalPages}</span>`);
    }

    // 페이지네이션 버튼 클릭 이벤트
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
});