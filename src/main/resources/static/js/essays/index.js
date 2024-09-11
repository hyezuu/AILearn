document.addEventListener("DOMContentLoaded", function() {
    const searchForm = document.getElementById("search-container");
    const searchInput = document.getElementById("search-input");
    const essayList = document.getElementById("essay-list");
    // const paginationInfo = document.getElementById("page-info");
    // const prevPageButton = document.getElementById("prev-page");
    // const nextPageButton = document.getElementById("next-page");

    let currentPage = 0; // 현재 페이지 번호
    const pageSize = 10; // 한 페이지에 표시할 개수
    let totalPages = 0; // 총 페이지 수

    // 페이지 로드 시 초기 에세이 목록 로드
    fetchEssays(currentPage, pageSize);

    // 검색 폼 제출 이벤트 처리
    searchForm.addEventListener("submit", function(event) {
        event.preventDefault(); // 페이지 새로고침 방지
        const searchQuery = searchInput.value;
        currentPage = 0; // 검색 시 페이지를 1페이지로 초기화
        fetchEssays(currentPage, pageSize, searchQuery);
    });

    // 에세이 목록 및 검색 기능 구현
    function fetchEssays(page, pageSize, searchQuery = "") {
        const url = searchQuery
            ? `/api/essays/search?topic=${encodeURIComponent(searchQuery)}&page=${page}&pageSize=${pageSize}`
            : `/api/me/essays?page=${page}&pageSize=${pageSize}`;


        fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include' // 세션 포함
        })
            .then(response => response.json())
            .then(data => {
                displayEssays(data.content);
                totalPages = data.totalPages; // 서버에서 받은 총 페이지 수 업데이트
                updatePagination(data.number, totalPages);
            })
            .catch(error => console.error("Error fetching essays:", error));
    }

    // 에세이 목록을 화면에 표시
    function displayEssays(essays) {
        essayList.innerHTML = ""; // 기존 목록 초기화

        essays.forEach(essay => {
            const truncatedTopic = essay.topic.length > 15
                ? essay.topic.substring(0, 15) + "..."
                : essay.topic

            const truncatedContent = essay.content.length > 60
                ? essay.content.substring(0, 60) + "..."
                : essay.content;

            const date = new Date(essay.createdAt).toISOString().slice(2, 16).replace("T"," ");

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${truncatedTopic}</td>
                <td>${truncatedContent}</td>
                <td>${date}</td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/essays/${essay.id}`; // 클릭 시 상세 페이지로 이동
            });

            essayList.appendChild(row);
        });
    }

    // // 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    // function updatePagination(currentPage, totalPages) {
    //     paginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;
    //
    //     // 첫 페이지일 때 이전 버튼 비활성화
    //     prevPageButton.disabled = currentPage === 0;
    //
    //     // 마지막 페이지일 때 다음 버튼 비활성화
    //     nextPageButton.disabled = currentPage >= totalPages - 1;
    // }
    // // 페이지네이션 버튼 클릭 이벤트 처리
    // prevPageButton.addEventListener("click", function() {
    //     if (currentPage > 0) {
    //         currentPage--;
    //         fetchEssays(currentPage, pageSize, searchInput.value);
    //     }
    // });
    //
    // nextPageButton.addEventListener("click", function() {
    //     if (currentPage < totalPages - 1) {
    //         currentPage++;
    //         fetchEssays(currentPage, pageSize, searchInput.value);
    //     }
    // });

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
                fetchEssays(currentPage, pageSize, searchInput.value);
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
                fetchEssays(currentPage, pageSize, searchInput.value);
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
                fetchEssays(currentPage, pageSize, searchInput.value);
            }
        });
        pageContainer.appendChild(lastButton);
    }
});
