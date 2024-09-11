document.addEventListener("DOMContentLoaded", function() {
    let userId = null;
    const postId = window.location.pathname.split("/").slice(-2, -1)[0];
    fetchPostDetails(postId);

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

    // 게시글 상세 불러오기
    function  fetchPostDetails(id) {
        fetch(`/api/posts/${id}`, {
            method : 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                displayPostDetails(data);
            })
            .catch(error => {
                console.error('Error fetching post details:', error);
            });
    }

    function displayPostDetails(post) {
        document.getElementById("title").value = post.title;
        document.getElementById("content").value = post.content;
    }


    // 게시글 수정 api 요청
    document.getElementById("edit-form").addEventListener("submit", function (event) {
        event.preventDefault();

        const title = document.getElementById("title").value;
        const content = document.getElementById("content").value;

        const postData = {
            userId: userId,  // 가져온 사용자 ID를 사용
            title: title,
            content: content
        };

        fetch(`/api/posts/${postId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(postData),
            credentials: 'include'  // 세션 쿠키를 포함하여 요청
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
                window.location.href = `/posts/${postId}`;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    })


})