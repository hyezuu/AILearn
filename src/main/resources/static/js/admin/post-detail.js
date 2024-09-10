document.addEventListener("DOMContentLoaded", function () {
    const postId = window.location.pathname.split("/").pop(); // URL 에서 ID를 추출
    let userId = 0;

    fetchPostDetails(postId);

    // 사용자 정보를 요청하여 userId를 가져옴
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/api/me", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.withCredentials = true;
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var data = JSON.parse(xhr.responseText);
            userId = data.id;
        } else if (xhr.readyState === 4) {
            console.error('Error fetching user data:', xhr.status);
        }
    };
    xhr.send();

    // 게시글 상세 불러오기
    function fetchPostDetails(id) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", `/api/admin/posts/${id}`, true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                displayPostDetails(data);
            } else if (xhr.readyState === 4) {
                alert('오류가 발생했습니다.');
                console.error('Error fetching post details:', xhr.status);
            }
        };
        xhr.send();
    }

    function displayPostDetails(post) {
        document.getElementById("post-title").textContent = post.title;
        document.getElementById("post-viewCount").textContent = "조회수: " + post.viewCount;
        document.getElementById("post-content").textContent = post.contents;

        const date = new Date(post.createdAt);
        document.getElementById("post-createdAt").textContent = "작성일: " + date.toISOString().slice(0, 16).replace('T', ' ');

        if (post.deletedAt === null) {
            document.querySelector("#post-deletedAt").style.display = 'none';
        } else {
            let deleted = new Date(post.deletedAt);
            document.getElementById("post-deletedAt").textContent = "삭제일: " + deleted.toISOString().slice(0, 16).replace('T', ' ');
        }
        displayComments(post.comments);
    }

    // 게시글 삭제
    document.getElementById("delete-post").addEventListener("click", function (e) {
        e.preventDefault();
        const result = confirm("게시글을 삭제하시겠습니까?");
        if (result) {
            var xhr = new XMLHttpRequest();
            xhr.open("DELETE", `/api/admin/posts/${postId}`, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.withCredentials = true;
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    window.location.href = "/admin/posts";
                } else if (xhr.readyState === 4) {
                    alert('오류가 발생했습니다.');
                    console.error('Error deleting post:', xhr.status);
                }
            };
            xhr.send();
        }
    });

    // 댓글 생성
    document.getElementById("input-comment").addEventListener("submit", function (event) {
        event.preventDefault();

        const comment = document.getElementById("input-comment-value").value;
        const commentData = {
            userId: userId,
            postId: postId,
            content: comment
        };

        var xhr = new XMLHttpRequest();
        xhr.open("POST", `/api/posts/${postId}/comments`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && (xhr.status === 200 || xhr.status === 201)) {
                document.getElementById("input-comment-value").value = "";
                window.location.reload();
            } else if (xhr.readyState === 4) {
                alert('오류가 발생했습니다.');
                console.error('Error creating comment:', xhr.status);
            }
        };
        xhr.send(JSON.stringify(commentData));
    });

    function displayComments(comments) {
        const commentList = document.getElementById("comment-list");
        commentList.innerHTML = "";

        comments.forEach(comment => {
            const commentContainer = document.createElement("div");
            commentContainer.classList.add("comment-container");

            const date = new Date(comment.createdAt).toISOString().slice(2, 16).replace("T", " ");

            // 댓글 삭제 버튼을 모든 댓글에 대해 표시
            commentContainer.innerHTML = `
            <span class="comment-content">${comment.content}</span>
            <div class="comment-info">
                <span><strong>${comment.nickname}</strong></span>
                <span>${date}</span>
                <a class="delete-comment" data-comment-id="${comment.id}">
                    <img src="/images/x-mark.png" alt="댓글삭제버튼">
                </a>
            </div>            
        `;

            commentList.appendChild(commentContainer);
        });

        // 모든 삭제 버튼에 이벤트 리스너 추가
        const deleteButtons = document.querySelectorAll(".delete-comment");
        deleteButtons.forEach(button => {
            button.addEventListener("click", function () {
                const commentId = this.getAttribute("data-comment-id");
                deleteComment(commentId);
            });
        });
    }


    // 댓글 삭제
    function deleteComment(commentId) {
        const result = confirm("댓글을 삭제하시겠습니까?");
        if (result) {
            var xhr = new XMLHttpRequest();
            xhr.open("DELETE", `/api/admin/posts/${postId}/comments/${commentId}`, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.withCredentials = true;
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    window.location.reload();
                } else if (xhr.readyState === 4) {
                    alert('오류가 발생했습니다.');
                    console.error('Error deleting comment:', xhr.status);
                }
            };
            xhr.send();
        }
    }
});
