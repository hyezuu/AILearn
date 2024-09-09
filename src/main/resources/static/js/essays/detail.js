document.addEventListener("DOMContentLoaded", function() {
    const essayId = window.location.pathname.split("/").pop(); // URL에서 ID를 추출
    console.log(essayId);
    fetchEssayDetails(essayId);

    function fetchEssayDetails(id) {
        fetch(`/api/essays/${id}`, {
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
                displayEssayDetails(data);
            })
            .catch(error => {
                console.error('Error fetching essay details:', error);
                displayEssayError(error.message);
            });
    }

    function displayEssayDetails(essay) {
        document.getElementById("essay-topic").textContent = essay.topic;
        document.getElementById("essay-content").textContent = essay.content;

        // 작성일을 원하는 형식으로 변환
        const date = new Date(essay.createdAt);
        const formattedDate = date.toISOString().slice(0, 16).replace('T', ' ');
        document.getElementById("essay-date").textContent = formattedDate;
    }

    function displayEssayError(error) {
        const errorCode = error.match(/\d{3}/)?.[0]; // 3자리 숫자를 매칭

        if(errorCode === "404") {
            document.getElementById("layout").innerHTML = `
            <header th:replace="~{layout/header::header}"></header>
            <h3 class="error-404">삭제되었거나 존재하지 않는 에세이 입니다.</h3>
        `;
        }

        if(errorCode === "403") {
            document.getElementById("layout").innerHTML = `
            <header th:replace="~{layout/header::header}"></header>
            <h3 class="error-404">접근 권한이 없는 에세이 입니다.</h3>
        `;
        }
    }


    const edit = document.getElementById("edit");
    const review = document.getElementById("review")
    edit.addEventListener("click", function() {
        window.location.href = `/essays/${essayId}/edit`; // 클릭 시 상세 페이지로 이동
    });

    review.addEventListener("click",function() {
       window.location.href = `/essays/${essayId}/review`
    });
});