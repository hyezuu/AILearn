document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("search-container");
    const searchInput = document.getElementById("search-input");
    const postList = document.getElementById("post-list");
    // const paginationInfo = document.getElementById("page-info");
    // const prevPageButton = document.getElementById("prev-page");
    // const nextPageButton = document.getElementById("next-page");

    let currentPage = 0; // 현재 페이지 번호
    const pageSize = 12; // 한 페이지에 표시할 개수
    let totalPages = 0; // 총 페이지 수

    // 페이지 로드 시 초기 에세이 목록 로드
    fetchPosts(currentPage, pageSize);

    searchForm.addEventListener("submit", function(event) {
        event.preventDefault(); // 페이지 새로고침 방지
        const searchQuery = searchInput.value;
        currentPage = 0; // 검색 시 페이지를 1페이지로 초기화
        fetchPosts(currentPage, pageSize, searchQuery);
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

            const nickname = posts.userName;
            let userGrade = posts.userGrade;
            switch (posts.userGrade) {
                case "A1":
                    userGrade = "/images/bronze.png";
                    break;
                case "A2":
                    userGrade = "/images/silver.png";
                    break;
                case "B1":
                    userGrade = "/images/gold.png";
                    break;
                case "B2":
                    userGrade = "/images/platinum.png";
                    break;
                case "C1":
                    userGrade = "/images/diamond.png";
                    break;
                case "C2":
                    userGrade = "/images/diamond 2.png";
                    break;
                default:
                    userGrade = "";
                    break;
            }

            const date = new Date(posts.createdAt).toISOString().slice(2, 16).replace("T"," ");

            const viewCount = posts.viewCount;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${truncatedTitle}</td>
                <td><img src="${userGrade}" alt="userGrade" class="grade-img">${nickname}</td>
                <td>${date}</td>
                <td>${viewCount}</td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/posts/${posts.id}`; // 클릭 시 상세 페이지로 이동
            });

            postList.appendChild(row);
        });
    }

    function updatePagination(currentPage, totalPages) {
        const pageContainer = document.getElementById('paginationContainer');
        pageContainer.innerHTML = '';  // 기존 버튼 제거

        const maxVisiblePages = 5;
        let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages);

        if (endPage - startPage < maxVisiblePages) {
            startPage = Math.max(0, endPage - maxVisiblePages);
        }


        // "<<" 첫 페이지로 이동 버튼
        const firstButton = document.createElement('button');
        firstButton.className = "move-btn"
        firstButton.innerText = '<<';
        firstButton.disabled = currentPage === 0;  // 첫 페이지에서는 비활성화
        firstButton.addEventListener('click', function() {
            if (currentPage > 0) {
                currentPage = 0;
                fetchPosts(currentPage, pageSize, searchInput.value);
            }
        });
        pageContainer.appendChild(firstButton);

        // 페이지 번호 버튼들 (최대 5개)
        for (let i = startPage; i < endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.innerText = i + 1;
            pageButton.disabled = i === currentPage;  // 현재 페이지는 비활성화
            pageButton.addEventListener('click', function() {
                currentPage = i;
                fetchPosts(currentPage, pageSize, searchInput.value);
            });
            pageContainer.appendChild(pageButton);
        }

        // ">>" 마지막 페이지로 이동 버튼
        const lastButton = document.createElement('button');
        lastButton.className = "move-btn"
        lastButton.innerText = '>>';
        lastButton.disabled = currentPage >= totalPages - 1;  // 마지막 페이지에서는 비활성화
        lastButton.addEventListener('click', function() {
            if (currentPage < totalPages - 1) {
                currentPage = totalPages - 1;
                fetchPosts(currentPage, pageSize, searchInput.value);
            }
        });
        pageContainer.appendChild(lastButton);
    }


})