document.addEventListener("DOMContentLoaded", function() {
    const searchForm = document.getElementById("search-container");
    const searchInput = document.getElementById("search-input");
    const essayList = document.getElementById("essay-list");
    const paginationInfo = document.getElementById("page-info");
    const prevPageButton = document.getElementById("prev-page");
    const nextPageButton = document.getElementById("next-page");

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

    // 페이지네이션 버튼 클릭 이벤트 처리
    prevPageButton.addEventListener("click", function() {
        if (currentPage > 0) {
            currentPage--;
            fetchEssays(currentPage, pageSize, searchInput.value);
        }
    });

    nextPageButton.addEventListener("click", function() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchEssays(currentPage, pageSize, searchInput.value);
        }
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

    // 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    function updatePagination(currentPage, totalPages) {
        paginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

        // 첫 페이지일 때 이전 버튼 비활성화
        prevPageButton.disabled = currentPage === 0;

        // 마지막 페이지일 때 다음 버튼 비활성화
        nextPageButton.disabled = currentPage >= totalPages - 1;
    }







    //---------------------------------------------------------------------------
    // // 페이지 번호와 페이지 크기를 정의
    // let currentPage = 0; // 또는 URL 파라미터로부터 값을 가져올 수 있음
    // const pageSize = 10;
    // const searchInput = document.getElementById("search-input");
    // const searchButton = document.getElementById("search-button");
    //
    // // 페이지 로드 시 에세이 로드
    // fetchEssays(currentPage, pageSize);
    //
    // // 검색 버튼 클릭 이벤트
    // searchButton.addEventListener("click", function() {
    //     fetchEssays(currentPage, pageSize, searchInput.value);
    // });
    //
    // // 이전 페이지 버튼 클릭 이벤트
    // document.getElementById("prev-page").addEventListener("click", function() {
    //     if (currentPage > 0) {
    //         currentPage--;
    //         fetchEssays(currentPage, pageSize, searchInput.value);
    //     }
    // });
    //
    // // 다음 페이지 버튼 클릭 이벤트
    // document.getElementById("next-page").addEventListener("click", function() {
    //     currentPage++;
    //     fetchEssays(currentPage, pageSize, searchInput.value);
    // });
    //
    //
    // function fetchEssays(page, pageSize) {
    // fetch(`/api/me/essays?page=${page}&pageSize=${pageSize}`, {
    //     method: 'GET',
    //     headers: {
    //         'Content-Type': 'application/json',
    //     },
    //     credentials: 'include'  // 세션 쿠키를 포함하여 요청
    // })
    //     .then(response => {
    //         if (!response.ok) {
    //             throw new Error(`HTTP error! status: ${response.status}`);
    //         }
    //         return response.json();
    //     })
    //     .then(data => {
    //         // console.log("Received data:", data); // 데이터를 콘솔에 출력해서 확인
    //         displayEssays(data.content);
    //         updatePagination(data.number, data.totalPages); // 페이지네이션 업데이트
    //     })
    //     .catch(error => {
    //         console.error('Error fetching essays:', error);
    //     });
    // }
    //
    // // api 로 받은 값을 목록화
    // function displayEssays(essays) {
    //     const tbody = document.querySelector("table tbody");
    //     tbody.innerHTML = ""; // 기존 내용을 지움
    //
    //     essays.forEach(essay => {
    //         // content 를 100글자까지만 표시
    //         const truncatedContent = essay.content.length > 60 ? essay.content.substring(0, 60) + "..." : essay.content;
    //
    //         // createdAt을 원하는 형식으로 변환
    //         const date = new Date(essay.createdAt);
    //         const formattedDate = date.toISOString().slice(2, 16).replace('T', ' '); // 'yyyy-MM-ddTHH:mm:ss' 에서 'yyyy-MM-dd HH:mm' 으로 변환
    //
    //         // 행을 생성하고 내용 추가
    //         const row = document.createElement("tr");
    //         row.innerHTML = `
    //         <td>${essay.topic}</td>
    //         <td>${truncatedContent}</td>
    //         <td>${formattedDate}</td>
    //     `;
    //
    //         row.addEventListener("click", function() {
    //             window.location.href = `/essays/${essay.id}`; // 클릭 시 상세 페이지로 이동
    //         });
    //
    //         tbody.appendChild(row);
    //     });
    // }
    //
    //
    // // 페이지네이션 번호
    // function updatePagination(currentPage, totalPages) {
    //     const pageInfo = document.getElementById("page-info");
    //     pageInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;
    // }

    // document.getElementById("search-container").addEventListener("submit", function(event) {
    //     event.preventDefault();
    //     console.log("검색api시작")
    //
    //     const searchByTopic = document.getElementById("search-input").value;
    //     console.log("검색어: " + searchByTopic)
    //
    //     fetch(`/api/essays/search?topic=${searchByTopic}&page=${currentPage}&pageSize=${pageSize}`, {
    //         method : 'GET',
    //         headers : {
    //             'Content-Type': 'application/json',
    //         },
    //         credentials: 'include'  // 세션 쿠키를 포함하여 요청
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 throw new Error(`HTTP error! status: ${response.status}`);
    //             }
    //             return response.json();
    //         })
    //         .then(data => {
    //             console.log("검색api전송 성공"); // 데이터를 콘솔에 출력해서 확인
    //             console.log("Received data:", data); // 데이터를 콘솔에 출력해서 확인
    //             displayEssays(data.content);
    //             updatePagination(data.number, data.totalPages); // 페이지네이션 업데이트
    //         })
    //         .catch(error => {
    //             console.error('Error fetching essays:', error);
    //         });
    // });
});
