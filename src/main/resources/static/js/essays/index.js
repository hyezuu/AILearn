document.addEventListener("DOMContentLoaded", function() {
    // 페이지 번호와 페이지 크기를 정의
    let currentPage = 0; // 또는 URL 파라미터로부터 값을 가져올 수 있음
    const pageSize = 10;
    const searchInput = document.getElementById("search-input");
    const searchButton = document.getElementById("search-button");

    // 페이지 로드 시 에세이 로드
    fetchEssays(currentPage, pageSize);

    // 검색 버튼 클릭 이벤트
    searchButton.addEventListener("click", function() {
        fetchEssays(currentPage, pageSize, searchInput.value);
    });

    // 이전 페이지 버튼 클릭 이벤트
    document.getElementById("prev-page").addEventListener("click", function() {
        if (currentPage > 0) {
            currentPage--;
            fetchEssays(currentPage, pageSize, searchInput.value);
        }
    });

    // 다음 페이지 버튼 클릭 이벤트
    document.getElementById("next-page").addEventListener("click", function() {
        currentPage++;
        fetchEssays(currentPage, pageSize, searchInput.value);
    });


    function fetchEssays(page, pageSize, query = "") {
    fetch(`/api/me/essays?page=${page}&pageSize=${pageSize}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include'  // 세션 쿠키를 포함하여 요청
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Received data:", data); // 데이터를 콘솔에 출력해서 확인
            displayEssays(data.content);
            updatePagination(data.number, data.totalPages); // 페이지네이션 업데이트
        })
        .catch(error => {
            console.error('Error fetching essays:', error);
        });
    }

    function displayEssays(essays) {
        const tbody = document.querySelector("table tbody");
        tbody.innerHTML = ""; // 기존 내용을 지움

        essays.forEach(essay => {
            // content를 100글자까지만 표시
            const truncatedContent = essay.content.length > 60 ? essay.content.substring(0, 60) + "..." : essay.content;

            // createdAt을 원하는 형식으로 변환
            const date = new Date(essay.createdAt);
            const formattedDate = date.toISOString().slice(2, 16).replace('T', ' '); // 'yyyy-MM-ddTHH:mm:ss'에서 'yyyy-MM-dd HH:mm'으로 변환

            // 행을 생성하고 내용 추가
            const row = document.createElement("tr");
            row.innerHTML = `
            <td>${essay.topic}</td>
            <td>${truncatedContent}</td>
            <td>${formattedDate}</td>
        `;

            row.addEventListener("click", function() {
                window.location.href = `/essays/${essay.id}`; // 클릭 시 상세 페이지로 이동
            });

            tbody.appendChild(row);
        });
    }

    function updatePagination(currentPage, totalPages) {
        const pageInfo = document.getElementById("page-info");
        pageInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;
    }
});
