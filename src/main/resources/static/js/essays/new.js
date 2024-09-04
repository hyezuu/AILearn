document.addEventListener("DOMContentLoaded", function() {
    let userId = null;

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

    document.getElementById("essay-form").addEventListener("submit", function(event) {
        event.preventDefault();

        const topic = document.getElementById("topic").value;
        const content = document.getElementById("content").value;

        const essayData = {
            userId: userId,  // 가져온 사용자 ID를 사용
            topic: topic,
            content: content
        };

        fetch("/api/essays", {
            method: "POST",
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
                window.location.href = "/essays";
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });
});