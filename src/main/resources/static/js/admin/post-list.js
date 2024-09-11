$(document).ready(function () {
    const $searchForm = $("#search-form");
    const $searchInput = $(".search-input");
    const $postList = $("#post-list");
    const $pagination = $('.pagination');

    let currentPage = 0;
    const itemsPerPage = 10;
    let totalPages = 0;

    fetchPosts(currentPage);

    $searchForm.on("submit", function(event) {
        event.preventDefault();
        console.log("submit event occured");
        currentPage = 0;
        fetchPosts(currentPage);
    });

    function fetchPosts(page) {
        const searchQuery = $searchInput.val();
        console.log(searchQuery);
        console.log($searchInput.value);
        $.ajax({
            url: `/api/admin/posts?page=${page}&size=${itemsPerPage}&keyword=${searchQuery}`,
            method: 'GET',
            dataType: 'json',
            xhrFields: {
                withCredentials: true
            },
            success: function (response) {
                console.log("Success to search")
                console.log(response)
                update(response);
            },
            error: function (xhr, status, error) {
                console.error("Error fetching posts:", error);
            }
        });
    }

    function displayPosts(posts) {
        $postList.empty();

        if (posts.length === 0) {
            $postList.append('<tr><td colspan="7">No posts found</td></tr>');
            return;
        }

        posts.forEach(post => {
            const truncatedTitle = post.title.length > 35
                ? post.title.substring(0, 35) + "..."
                : post.title;

            const $row = $("<tr>").html(`
                <td>${post.id}</td>
                <td class="post-title" data-post-id="${post.id}">${truncatedTitle}</td>
                <td>${post.viewCount}</td>
                <td>${post.nickname}</td>
                <td>${formatDate(post.createdAt)}</td>
                <td>${post.deletedAt ? formatDate(post.deletedAt) : ''}</td>
                <td>
                    ${post.deletedAt ? "" : '<button class="delete-btn">삭제하기</button>'}
                </td>
            `);

            $postList.append($row);
        });
    }

    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const year = date.getFullYear().toString().slice(-2);
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        const milliseconds = String(date.getMilliseconds()).padStart(3, '0');

        return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
    }

    function update(pageData) {
        displayPosts(pageData.content);
        totalPages = pageData.totalPages;
        currentPage = pageData.number;
        renderPagination(pageData);
    }

    function renderPagination(pageData) {
        $pagination.empty();

        if (totalPages <= 1) return;  // 페이지가 1개 이하면 페이지네이션 표시하지 않음

        $pagination.append(`<button id="prev-page" class="pagination-button" ${pageData.first ? 'disabled' : ''}> << </button>`);

        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            $pagination.append(`<button class="pagination-button ${i === currentPage ? 'active' : ''}">${i + 1}</button>`);
        }

        $pagination.append(`<button id="next-page" class="pagination-button" ${pageData.last ? 'disabled' : ''}> >> </button>`);
        $pagination.append(`<span id="page-info">Page ${currentPage + 1} of ${totalPages}</span>`);
    }

    $(document).on('click', '.pagination-button', function() {
        if ($(this).attr('id') === 'prev-page') {
            if (currentPage > 0) currentPage--;
        } else if ($(this).attr('id') === 'next-page') {
            if (currentPage < totalPages - 1) currentPage++;
        } else {
            currentPage = parseInt($(this).text()) - 1;
        }
        fetchPosts(currentPage);
    });

    $(document).on('click', '.post-title', function() {
        const postId = $(this).data('post-id');
        window.location.href = `/admin/posts/${postId}`;
    });

    $(document).on('click', '.delete-btn', async function(event) {
        event.stopPropagation();
        const postId = $(this).closest('tr').find('td:first').text();
        await deletePost(postId);
    });

    async function deletePost(postId) {
        const result = await Swal.fire({
            title: '게시글을 삭제하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소'
        });

        if (result.isConfirmed) {
            $.ajax({
                url: `/api/admin/posts/${postId}`,
                type: 'DELETE',
                contentType: 'application/x-www-form-urlencoded',
                success: function(response) {
                    Swal.fire('삭제되었습니다.', '', 'success');
                    fetchPosts(currentPage);
                },
                error: function(xhr) {
                    Swal.fire('오류가 발생했습니다.', '', 'error');
                }
            });
        }
    }
});
