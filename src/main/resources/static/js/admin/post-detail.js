// document.addEventListener("DOMContentLoaded", function() {
//     const postId = window.location.pathname.split("/").pop(); // URL 에서 ID를 추출
//     let userId = 0;
//     let userIdByPost = 0;
//     fetchPostDetails(postId);
//     fetchComments();
//
//     // 사용자 정보를 요청하여 userId를 가져옴
//     fetch("/api/me", {
//         method: "GET",
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         credentials: 'include'  // 세션 쿠키를 포함하여 요청
//     })
//         .then(response => response.json())
//         .then(data => {
//             userId = data.id;
//         })
//         .catch(error => {
//             console.error('Error fetching user data:', error);
//         });
//
//     // 게시글 상세 불러오기
//     function  fetchPostDetails(id) {
//         fetch(`/api/admin/posts/${id}`, {
//             method : 'GET',
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         })
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error(`HTTP error! status: ${response.status}`);
//                 }
//                 return response.json();
//             })
//             .then(data => {
//                 console.log("Received data:", data); // 데이터를 콘솔에 출력해서 확인
//                 displayPostDetails(data);
//             })
//             .catch(error => {
//                 console.error('Error fetching post details:', error);
//             });
//     }
//
//     function displayPostDetails(post) {
//         document.getElementById("post-title").textContent = post.title;
//         document.getElementById("post-viewCount").textContent = "조회수: " + post.viewCount;
//         document.getElementById("post-content").textContent = post.content;
//
//         const date = new Date(post.createdAt);
//         document.getElementById("post-createdAt").textContent = date.toISOString().slice(0, 16).replace('T', ' ');
//
//         // 현재 세션에서 가져온 userId와 post에서 가져온 userId가 같으면 수정삭제 버튼 활성화
//         if(post.userId === userId && post.userId !== 0) {
//             document.querySelector(".button-container").style.display = "block";
//         }
//         console.log("postUI:"+ post.userId)
//         console.log("userId: "+ userId);
//     }
//
//
//     // 수정 및 삭제
//     // const edit = document.getElementById("edit");
//     const deletePost = document.getElementById("delete-post")
//
//     // 수정
//     // edit.addEventListener("click", function() {
//     //     window.location.href = `/posts/${postId}/edit`; // 클릭 시 상세 페이지로 이동
//     // });
//
//     // 삭제
//     deletePost.addEventListener("click",function(e) {
//         e.preventDefault()
//         const result = confirm("게시글을 삭제하시겠습니까?");
//
//         if(result) {
//             fetch(`/api/admin/posts/${postId}`, {
//                 method: "DELETE",
//                 headers: {
//                     'Content-Type': 'application/json'
//                 },
//                 credentials: 'include' // 세션 포함
//             })
//                 .then(response => {
//                     if (!response.ok) {
//                         throw new Error(`HTTP error! status: ${response.status}`);
//                     }
//
//                     // 응답 본문이 있을 경우에만 JSON 파싱
//                     return response.text().then(text => {
//                         return text ? JSON.parse(text) : {};
//                     });
//                 })
//                 .then(data => {
//                     console.log("Success:", data);
//                     window.location.href = "api/admin/posts";
//                 })
//                 .catch(error => {
//                     console.error("Error:", error);
//                 });
//         }
//     });
//
//
//     // 댓글 생성 api
//     document.getElementById("input-comment").addEventListener("submit", function(event){
//         event.preventDefault();
//
//         const comment = document.getElementById("input-comment-value").value;
//
//         const commentData = {
//             userId: userId,
//             postId: postId,
//             content: comment
//         }
//
//         fetch(`/api/admin/posts/${postId}/comments`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(commentData)
//         })
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error(`HTTP error! status: ${response.status}`);
//                 }
//
//                 // 응답 본문이 있을 경우에만 JSON 파싱
//                 return response.text().then(text => {
//                     return text ? JSON.parse(text) : {};
//                 });
//             })
//             .then(data => {
//                 console.log("Success:", data);
//                 document.getElementById("input-comment-value").value = "";
//                 fetchComments();
//             })
//             .catch(error => {
//                 console.error("Error:", error);
//             });
//     })
//
//     //댓글 조회 api
//     function fetchComments() {
//         fetch(`/api/admin/posts/${postId}/comments`, {
//             method: "GET",
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         })
//             .then(response => response.json())
//             .then(data => {
//                 displayComments(data);
//             })
//             .catch(error => console.error("Error fetching posts:", error));
//     }
//
//     // displayComment 메서드
//     function  displayComments(comments) {
//         const commentList = document.getElementById("comment-list");
//         commentList.innerHTML="";
//
//         comments.forEach(comment => {
//             const commentContainer = document.createElement("div")
//             commentContainer.classList.add("comment-container")
//
//             const date = new Date(comment.createdAt).toISOString().slice(2, 16).replace("T"," ");
//
//             let display = "";
//             // 현재 세션에서 가져온 userId와 comment 에서 가져온 userId가 같으면 수정삭제 버튼 활성화
//             if(comment.userId === userId) {
//                 display = "block";
//             } else {
//                 display = "none";
//             }
//
//             commentContainer.innerHTML = `
//                 <span class="comment-content">${comment.content}</span>
//                 <div class="comment-info">
//                 <span><strong>${comment.nickname}</strong></span>
//                 <span>${date}</span>
//                 <a class="delete-comment" data-comment-id="${comment.id}" style="display: ${display}">
//                     <img src="/images/x-mark.png" alt="댓글삭제버튼">
//                 </a>
//                 </div>
//             `;
//
//             commentList.appendChild(commentContainer);
//         });
//
//         // 모든 삭제 버튼에 이벤트 리스너 추가
//         const deleteButtons = document.querySelectorAll(".delete-comment");
//         deleteButtons.forEach(button => {
//             button.addEventListener("click", function() {
//                 const commentId = this.getAttribute("data-comment-id");
//                 deleteComment(commentId);
//             });
//         });
//     }
//
//
//
//     // 댓글 삭제
//     function deleteComment(commentId) {
//         const result = confirm("삭제하시겠습니까?");
//
//         if(result) {
//             fetch(`/api/posts/${postId}/comments/${commentId}`, {
//                 method: "DELETE",
//                 headers: {
//                     'Content-Type': 'application/json'
//                 },
//                 credentials: 'include' // 세션 포함
//             })
//                 .then(response => {
//                     if (!response.ok) {
//                         throw new Error(`HTTP error! status: ${response.status}`);
//                     }
//                     // 삭제 후 댓글 목록 갱신
//                     fetchComments(); // 댓글 목록을 다시 불러오는 함수를 호출
//                 })
//                 .catch(error => console.error("Error deleting comment:", error));
//         }
//     }
// });

document.addEventListener("DOMContentLoaded", function() {
    const postId = window.location.pathname.split("/").pop(); // URL 에서 ID를 추출
    let userId = 0;

    fetchPostDetails(postId);
    fetchComments();

    // 사용자 정보를 요청하여 userId를 가져옴
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/api/me", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.withCredentials = true;
    xhr.onreadystatechange = function() {
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
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                displayPostDetails(data);
            } else if (xhr.readyState === 4) {
                console.error('Error fetching post details:', xhr.status);
            }
        };
        xhr.send();
    }

    function displayPostDetails(post) {
        document.getElementById("post-title").textContent = post.title;
        document.getElementById("post-viewCount").textContent = "조회수: " + post.viewCount;
        document.getElementById("post-content").textContent = post.content;

        const date = new Date(post.createdAt);
        document.getElementById("post-createdAt").textContent = date.toISOString().slice(0, 16).replace('T', ' ');

        if (post.userId === userId && post.userId !== 0) {
            document.querySelector(".button-container").style.display = "block";
        }
    }

    // 게시글 삭제
    document.getElementById("delete-post").addEventListener("click", function(e) {
        e.preventDefault();
        const result = confirm("게시글을 삭제하시겠습니까?");
        if (result) {
            var xhr = new XMLHttpRequest();
            xhr.open("DELETE", `/api/admin/posts/${postId}`, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.withCredentials = true;
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    window.location.href = "/api/admin/posts";
                } else if (xhr.readyState === 4) {
                    console.error('Error deleting post:', xhr.status);
                }
            };
            xhr.send();
        }
    });

    // 댓글 생성
    document.getElementById("input-comment").addEventListener("submit", function(event) {
        event.preventDefault();

        const comment = document.getElementById("input-comment-value").value;
        const commentData = {
            userId: userId,
            postId: postId,
            content: comment
        };

        var xhr = new XMLHttpRequest();
        xhr.open("POST", `/api/admin/posts/${postId}/comments`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                document.getElementById("input-comment-value").value = "";
                fetchComments();
            } else if (xhr.readyState === 4) {
                console.error('Error creating comment:', xhr.status);
            }
        };
        xhr.send(JSON.stringify(commentData));
    });

    // 댓글 조회
    function fetchComments() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", `/api/admin/posts/${postId}/comments`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                displayComments(data);
            } else if (xhr.readyState === 4) {
                console.error('Error fetching comments:', xhr.status);
            }
        };
        xhr.send();
    }

    // function displayComments(comments) {
    //     const commentList = document.getElementById("comment-list");
    //     commentList.innerHTML = "";
    //
    //     comments.forEach(comment => {
    //         const commentContainer = document.createElement("div");
    //         commentContainer.classList.add("comment-container");
    //
    //         const date = new Date(comment.createdAt).toISOString().slice(2, 16).replace("T", " ");
    //
    //         let display = comment.userId === userId ? "block" : "none";
    //
    //         commentContainer.innerHTML = `
    //             <span class="comment-content">${comment.content}</span>
    //             <div class="comment-info">
    //             <span><strong>${comment.nickname}</strong></span>
    //             <span>${date}</span>
    //             <a class="delete-comment" data-comment-id="${comment.id}" style="display: ${display}">
    //                 <img src="/images/x-mark.png" alt="댓글삭제버튼">
    //             </a>
    //             </div>
    //         `;
    //
    //         commentList.appendChild(commentContainer);
    //     });
    //
    //     // 모든 삭제 버튼에 이벤트 리스너 추가
    //     const deleteButtons = document.querySelectorAll(".delete-comment");
    //     deleteButtons.forEach(button => {
    //         button.addEventListener("click", function() {
    //             const commentId = this.getAttribute("data-comment-id");
    //             deleteComment(commentId);
    //         });
    //     });
    // }

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
            button.addEventListener("click", function() {
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
            xhr.open("DELETE", `/api/posts/${postId}/comments/${commentId}`, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.withCredentials = true;
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    fetchComments();
                } else if (xhr.readyState === 4) {
                    console.error('Error deleting comment:', xhr.status);
                }
            };
            xhr.send();
        }
    }
});
