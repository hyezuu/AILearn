$(document).ready(function () {
    console.log('HERERERERERERE');
    const $searchForm = $("#search-container");
    const $searchInput = $("#search-input");
    const $postList = $("#post-list");
    const $paginationInfo = $("#page-info");
    const $prevPageButton = $("#prev-page");
    const $nextPageButton = $("#next-page");

    let currentPage = 0;
    const pageSize = 12;
    let totalPages = 0;

    // 페이지 로드 시 초기 에세이 목록 로드
    fetchPosts(currentPage, pageSize);

    $searchForm.on("submit", function(event) {
        event.preventDefault();
        console.log("search-submit");
        const searchQuery = $searchInput.val();
        currentPage = 0;
        fetchPosts(currentPage, pageSize, searchQuery);
    });

    $prevPageButton.on("click", function() {
        if (currentPage > 0) {
            currentPage--;
            fetchPosts(currentPage, pageSize, $searchInput.val());
        }
    });

    $nextPageButton.on("click", function() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchPosts(currentPage, pageSize, $searchInput.val());
        }
    });

    function fetchPosts(page, pageSize, searchQuery = "") {
        console.log('Fetching.....');
        $.ajax({
            url: `/api/admin/posts?keyword=${searchQuery}`,
            method: 'GET',
            dataType: 'json',
            xhrFields: {
                withCredentials: true
            },
            success: function(data) {
                console.log('Success fetch');
                displayPosts(data.content);
                totalPages = data.totalPages;
                updatePagination(data.number, totalPages);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching posts:", error);
            }
        });
    }

    function displayPosts(posts) {
        $postList.empty();

        posts.forEach(post => {
            const truncatedTitle = post.title.length > 35
                ? post.title.substring(0, 35) + "..."
                : post.title;

            const nickname = post.nickname;
            const date = new Date(post.createdAt).toISOString().slice(2, 16).replace("T"," ");
            const viewCount = post.viewCount;

            const $row = $("<tr>").html(`
                <td>${post.id}</td>
                <td>${truncatedTitle}</td>
                <td>${viewCount}</td>
                <td>${nickname}</td>
                <td>${date}</td>
                <td>${post.deletedAt ? new Date(post.deletedAt).toISOString().slice(2, 16).replace("T"," ") : ''}</td>
            `);

            $row.on("click", function() {
                window.location.href = `/posts/${post.id}`;
            });

            $postList.append($row);
        });
    }

    function updatePagination(currentPage, totalPages) {
        $paginationInfo.text(`Page ${currentPage + 1} of ${totalPages}`);
        $prevPageButton.prop("disabled", currentPage === 0);
        $nextPageButton.prop("disabled", currentPage >= totalPages - 1);
    }
});