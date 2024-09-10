document.addEventListener("DOMContentLoaded", function () {

    const essayId = window.location.pathname.split("/").slice(-2, -1)[0];
    let topic = null;
    let userId = null;

    fetchEssayData(essayId);

    function fetchEssayData(essayId) {
        fetch(`/api/essays/${essayId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                displayEssayData(data);
                topic = data.topic;
            })
            .catch(error => {
                console.error('Error fetching essay details:', error);
            });
    }

    function displayEssayData(essay) {
        document.getElementById("content").value = essay.content;
    }

    const reviewDiv = document.getElementById("review");

    // 로딩 도넛을 동적으로 추가하는 함수
    function addLoadingDonut() {
        // 로딩 도넛이 이미 존재하는지 확인
        if (!document.querySelector(".loading-donut")) {
            const loadingDonut = document.createElement("div");
            loadingDonut.className = "loading-donut";
            reviewDiv.insertBefore(loadingDonut, reviewDiv.querySelector("p"));
        }
    }

    // 로딩 도넛 제거 함수
    function removeLoadingDonut() {
        const loadingDonut = document.querySelector(".loading-donut");
        if (loadingDonut) {
            loadingDonut.remove();
        }
    }

    // 로딩 도넛을 추가 (여기서만 추가)
    addLoadingDonut();

    // 첨삭된 에세이 내용 가져오기
    fetch(`/api/essays/${essayId}/review`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            displayEssayReview(data);
            removeLoadingDonut(); // 성공 시 로딩 도넛 제거
        })
        .catch(error => {
            console.error('Error fetching essay details:', error);
            displayEssayError(error);
            removeLoadingDonut(); // 실패 시에도 로딩 도넛 제거
        });

    function displayEssayReview(review) {
        document.getElementById("review").innerHTML = review.reviewedContent;
    }

    function displayEssayError(error) {
        document.getElementById("review").innerText = "에세이를 첨삭할 수 없습니다.\n" + error;
    }

    // 사용자 정보를 요청하여 userId를 가져옴
    fetch("/api/me", {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include' // 세션 쿠키를 포함하여 요청
    })
        .then(response => response.json())
        .then(data => {
            userId = data.id;
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });

    // 첨삭페이지에서 수정한 값으로 에세이 수정시키기
    document.getElementById("essay-edit").addEventListener("submit", function (event) {
        event.preventDefault();

        const editContent = document.getElementById("content").value;

        const essayData = {
            userId: userId, // 가져온 사용자 ID를 사용
            topic: topic,
            content: editContent
        };

        fetch(`/api/essays/${essayId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(essayData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                return response.text().then(text => {
                    return text ? JSON.parse(text) : {};
                });
            })
            .then(data => {
                console.log("Success:", data);
                window.location.href = `/essays/${essayId}`;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });

    document.getElementById("back-to-list").addEventListener("click", function (event) {
        history.back();
    });
});
