document.addEventListener("DOMContentLoaded", function() {


    let userId = null;
    // const topic = document.getElementById("topic");
    // const content = document.getElementById("content");
    const essayId = window.location.pathname.split("/").slice(-2, -1)[0];

    fetchEssayData(essayId);


    document.getElementById("back-to-list").addEventListener("click", function () {
        window.location.href = `/essays/${essayId}`;
    });

    // 해당 에세이에 대한 상세 정보 GET
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
            })
            .catch(error => {
                console.error('Error fetching essay details:', error);
            });
    }

    // input value에 기존 에세이 값을 불러오기
    function displayEssayData(essay) {
        document.getElementById("topic").value = essay.topic;
        document.getElementById("content").value = essay.content;
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

    // 에세이 수정
    document.getElementById("essay-edit-form").addEventListener("submit", function(event) {
        event.preventDefault();

        const editTopic = document.getElementById("topic").value;
        const editContent = document.getElementById("content").value;

        const essayData = {
            userId: userId,  // 가져온 사용자 ID를 사용
            topic: editTopic,
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
                window.location.href = `/essays/${essayId}`;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });

    // 에세이 삭제
    document.getElementById("essay-delete").addEventListener("click", function (event){
        event.preventDefault();

        fetch(`/api/essays/${essayId}/delete`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            credentials : "include"
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
                window.location.href = `/essays`;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    })

});