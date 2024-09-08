document.addEventListener("DOMContentLoaded", function () {
    const postList = document.getElementById("post-list");
    const postPaginationInfo = document.getElementById("post-page-info");
    const postPrevPageButton = document.getElementById("post-prev-page");
    const postNextPageButton = document.getElementById("post-next-page");

    let postCurrentPage = 0; // 현재 페이지 번호
    const pageSize = 5; // 한 페이지에 표시할 개수
    let postTotalPages = 0; // 총 페이지 수
    fetchMyPosts(postCurrentPage, pageSize);

    // 내 게시글 조회 api
    function fetchMyPosts(page, pageSize) {

        fetch(`/api/me/posts?page=${page}&size=${pageSize}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include' // 세션 포함
        })
            .then(response => response.json())
            .then(data => {
                displayPosts(data.content);
                postTotalPages = data.totalPages; // 서버에서 받은 총 페이지 수 업데이트
                updatePostPagination(data.number, postTotalPages); // 페이지 네이션 업데이트
            })
            .catch(error => console.error("Error fetching posts:", error));
    }

    // 게시글 목록을 화면에 표시
    function displayPosts(posts) {
        postList.innerHTML = ""; // 기존 목록 초기화

        posts.forEach(posts => {
            const truncatedTitle = posts.title.length > 35
                ? posts.title.substring(0, 35) + "..."
                : posts.title

            const date = new Date(posts.createdAt).toISOString().slice(2, 16).replace("T"," ");

            const viewCount = posts.viewCount;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><span>${truncatedTitle}</span></td>
                <td><span>${date}</span></td>
                <td><span>${viewCount}</span></td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/posts/${posts.id}`; // 클릭 시 상세 페이지로 이동
            });

            postList.appendChild(row);
        });

        const currentRows = posts.length; // 현재 생성된 row의 개수

        for (let i = currentRows; i < 5; i++) {
            const emptyRow = document.createElement("tr");
            postList.appendChild(emptyRow);
        }
    }

    // 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    function updatePostPagination(currentPage, totalPages) {
        postPaginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

        // 첫 페이지일 때 이전 버튼 비활성화
        postPrevPageButton.disabled = currentPage === 0;

        // 마지막 페이지일 때 다음 버튼 비활성화
        postNextPageButton.disabled = currentPage >= totalPages - 1;
    }

    // 페이지네이션 버튼 클릭 이벤트 처리
    postPrevPageButton.addEventListener("click", function() {
        if (postCurrentPage > 0) {
            postCurrentPage--;
            fetchMyPosts(postCurrentPage, pageSize);
        }
    });

    postNextPageButton.addEventListener("click", function() {
        if (postCurrentPage < postTotalPages - 1) {
            postCurrentPage++;
            fetchMyPosts(postCurrentPage, pageSize);
        }
    });



    //--------------------------------------------

    const commentList = document.getElementById("comment-list");
    const commentPaginationInfo = document.getElementById("comment-page-info");
    const commentPrevPageButton = document.getElementById("comment-prev-page");
    const commentNextPageButton = document.getElementById("comment-next-page");

    let commentCurrentPage = 0; // 현재 페이지 번호
    let commentTotalPages = 0; // 총 페이지 수
    fetchMyComments(commentCurrentPage, pageSize);


    // 내 댓글 조회 api
    function fetchMyComments(page, pageSize) {

        fetch(`/api/me/comments?page=${page}&size=${pageSize}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include' // 세션 포함
        })
            .then(response => response.json())
            .then(data => {
                displayComments(data.content);
                commentTotalPages = data.totalPages; // 서버에서 받은 총 페이지 수 업데이트
                updateCommentPagination(data.number, commentTotalPages); // 페이지네이션 업데이트
            })
            .catch(error => console.error("Error fetching posts:", error));
    }

    // 게시글 목록을 화면에 표시
    function displayComments(comments) {
        commentList.innerHTML = ""; // 기존 목록 초기화

        comments.forEach(comment => {
            const truncatedTitle = comment.postTitle.length > 15
                ? comment.postTitle.substring(0, 15) + "..."
                : comment.postTitle

            const commentContent = comment.content;

            const postId = comment.postId

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><span>${truncatedTitle}</span></td>
                <td><span>${commentContent}</span></td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/posts/${postId}`; // 클릭 시 상세 페이지로 이동
            });

            commentList.appendChild(row);
        });

        const currentRows = comments.length; // 현재 생성된 row의 개수

        for (let i = currentRows; i < 5; i++) {
            const emptyRow = document.createElement("tr");
            commentList.appendChild(emptyRow);
        }
    }

    // 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    function updateCommentPagination(currentPage, totalPages) {
        commentPaginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

        // 첫 페이지일 때 이전 버튼 비활성화
        commentPrevPageButton.disabled = currentPage === 0;

        // 마지막 페이지일 때 다음 버튼 비활성화
        commentNextPageButton.disabled = currentPage >= totalPages - 1;
    }

    // 페이지네이션 버튼 클릭 이벤트 처리
    commentPrevPageButton.addEventListener("click", function() {
        if (commentCurrentPage > 0) {
            commentCurrentPage--;
            fetchMyComments(commentCurrentPage, pageSize);
        }
    });

    commentNextPageButton.addEventListener("click", function() {
        if (commentCurrentPage < commentTotalPages - 1) {
            commentCurrentPage++;
            fetchMyComments(commentCurrentPage, pageSize);
        }
    });



    //---------------------------------------------------------------

    const likeList = document.getElementById("like-list");
    const likePaginationInfo = document.getElementById("like-page-info");
    const likePrevPageButton = document.getElementById("like-prev-page");
    const likeNextPageButton = document.getElementById("like-next-page");

    let likeCurrentPage = 0; // 현재 페이지 번호
    let likeTotalPages = 0; // 총 페이지 수
    fetchMyLikes(likeCurrentPage, pageSize);


// 내 댓글 조회 api
    function fetchMyLikes(page, pageSize) {

        fetch(`/api/me/likes?page=${page}&size=${pageSize}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include' // 세션 포함
        })
            .then(response => response.json())
            .then(data => {
                displayLikes(data.content);
                likeTotalPages = data.totalPages; // 서버에서 받은 총 페이지 수 업데이트
                updateLikePagination(data.number, likeTotalPages); // 페이지네이션 업데이트
            })
            .catch(error => console.error("Error fetching posts:", error));
    }

// 게시글 목록을 화면에 표시
    function displayLikes(likes) {
        likeList.innerHTML = ""; // 기존 목록 초기화

        likes.forEach(like => {
            const truncatedTitle = like.title.length > 15
                ? like.title.substring(0, 15) + "..."
                : like.title

            const userName = like.userName

            const postId = like.id

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><span>${truncatedTitle}</span></td>
                <td><span>${userName}</span></td>
            `;

            row.addEventListener("click", function() {
                window.location.href = `/posts/${postId}`; // 클릭 시 상세 페이지로 이동
            });

            likeList.appendChild(row);
        });

        const currentRows = likes.length; // 현재 생성된 row의 개수

        for (let i = currentRows; i < 5; i++) {
            const emptyRow = document.createElement("tr");
            likeList.appendChild(emptyRow);
        }
    }

// 페이지네이션 정보 업데이트 및 버튼 활성화/비활성화 처리
    function updateLikePagination(currentPage, totalPages) {
        likePaginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

        // 첫 페이지일 때 이전 버튼 비활성화
        likePrevPageButton.disabled = currentPage === 0;

        // 마지막 페이지일 때 다음 버튼 비활성화
        likeNextPageButton.disabled = currentPage >= totalPages - 1;
    }

// 페이지네이션 버튼 클릭 이벤트 처리
    likePrevPageButton.addEventListener("click", function() {
        if (likeCurrentPage > 0) {
            likeCurrentPage--;
            fetchMyLikes(likeCurrentPage, pageSize);
        }
    });

    likeNextPageButton.addEventListener("click", function() {
        if (likeCurrentPage < likeTotalPages - 1) {
            likeCurrentPage++;
            fetchMyLikes(likeCurrentPage, pageSize);
        }
    });
});