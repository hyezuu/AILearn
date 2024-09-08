document.addEventListener("DOMContentLoaded", function() {

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

    // textarea value에 기존 에세이 값을 불러오기
    function displayEssayData(essay) {
        document.getElementById("content").value = essay.content;
    }


    // 첨삭된 에세이 내용 가져오기
    fetch(`/api/essays/${essayId}/review`, {
        method: 'PUT',
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
            displayEssayReview(data)
        })
        .catch(error => {
            console.error('Error fetching essay details:', error);
        });

    function displayEssayReview(review) {
        document.getElementById("review").innerHTML = review.reviewedContent;
    }

    // 사용자 정보를 요청하여 userId를 가져옴
    fetch("/api/me", {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'  // 세션 쿠키를 포함하여 요청
    })
        .then(response => response.json())
        .then(data => {
            userId = data.id;
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });

    // 첨삭페이지에서 수정한 값으로 에세이 수정시키기
    document.getElementById("essay-edit").addEventListener("submit", function(event) {
      event.preventDefault();

      const editContent = document.getElementById("content").value;

        const essayData = {
            userId: userId,  // 가져온 사용자 ID를 사용
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

                // 응답 본문이 있을 경우에만 JSON 파싱
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
    })

});