document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("search-container");
    const searchInput = document.getElementById("search-input");
    const postList = document.getElementById("post-list");
    const paginationInfo = document.getElementById("page-info");
    const prevPageButton = document.getElementById("prev-page");
    const nextPageButton = document.getElementById("next-page");

    let currentPage = 0; // 현재 페이지 번호
    const pageSize = 12; // 한 페이지에 표시할 개수
    let totalPages = 0; // 총 페이지 수

    // 페이지 로드 시 초기 에세이 목록 로드
    fetchPosts(currentPage, pageSize);

    searchForm.addEventListener("submit", function(event) {
        event.preventDefault(); // 페이지 새로고침 방지
        console.log("search-submit")
        const searchQuery = searchInput.value;
        currentPage = 0; // 검색 시 페이지를 1페이지로 초기화
        fetchPosts(currentPage, pageSize, searchQuery);
    });

    // 페이지네이션 버튼 클릭 이벤트 처리
    prevPageButton.addEventListener("click", function() {
        if (currentPage > 0) {
            currentPage--;
            fetchPosts(currentPage, pageSize, searchInput.value);
        }
    });

    nextPageButton.addEventListener("click", function() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchPosts(currentPage, pageSize, searchInput.value);
        }
    });

    // 게시글 목록 및 검색 기능 구현
    function fetchPosts(page, pageSize, searchQuery = "") {

        fetch(`/api/posts?page=${page}&size=${pageSize}&keyword=${searchQuery}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include' // 세션 포함
        })
            .then(response => response.json())
            .then(data => {
                displayPosts(data.content);
                totalPages = data.totalPages; // 서버에서 받은 총 페이지 수 업데이트
                updatePagination(data.number, totalPages);
            })
            .catch(error => console.error("Error fetching posts:", error));
    }

    // 게시글 목록을 화면에 표시
    function displayPosts(posts) {
        postList.innerHTML = ""; // 기존 목록 초기화

        posts.forEach(posts => {
            const truncatedTitle = posts.title.length > 35
                ? posts.title.substring(0, 35) + "..."
                : posts.title

            const nickname = posts.userNickname;

            const date = new Date(posts.createdAt).toISOString().slice(2, 16).replace("T"," ");

            const viewCount = posts.viewCount;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${truncatedTitle}</td>
                <td>${nickname}</td>
                <td>${date}</td>
                <td>${viewCount}</td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/posts/${posts.id}`; // 클릭 시 상세 페이지로 이동
            });

            postList.appendChild(row);
        });
    }

    // 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    function updatePagination(currentPage, totalPages) {
        paginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

        // 첫 페이지일 때 이전 버튼 비활성화
        prevPageButton.disabled = currentPage === 0;

        // 마지막 페이지일 때 다음 버튼 비활성화
        nextPageButton.disabled = currentPage >= totalPages - 1;
    }


})