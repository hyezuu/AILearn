document.addEventListener("DOMContentLoaded", async function() {
    const searchForm = document.getElementById("search-form");
    const searchInput = document.getElementById("search-input");
    let userGrammarExamples = 0;
    let userID = 0;
    let currentPage = 0;
    const pageSize = 5; // 페이지당 아이템 수
    getUserId();


    // SweetAlert2 라이브러리 추가
    loadScript('https://cdn.jsdelivr.net/npm/sweetalert2@11', initializeApp);

    function loadScript(url, callback) {
        let script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = url;
        script.onload = callback;
        document.head.appendChild(script);
    }

    function initializeApp() {
        fetchGrammarExamples(currentPage);
    }

    // 사용자 정보를 요청하여 userId를 가져옴 (비동기 처리)
    async function getUserId() {
        try {
            const response = await fetch("/api/me", {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'  // 세션 쿠키를 포함하여 요청
            });

            const data = await response.json();
            userGrammarExamples = data.grammarExampleCount;
            userID = data.id;

        } catch (error) {
            console.error('Error fetching user data:', error);
        }
    }

    // 문법 예문 조회 (비동기 처리)
    async function fetchGrammarExamples(page, searchQuery) {
        const url = searchQuery
            ? `/api/me/grammar-examples?page=${page}&pageSize=${pageSize}&keyword=${searchQuery}`
            : `/api/me/grammar-examples?page=${page}&pageSize=${pageSize}`;

        try {
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include"
            });

            const data = await response.json();
            await renderGrammarExamples(data.data);
            await renderPagination(data);
        } catch (error) {
            console.error('Error fetching grammar examples:', error);
        }
    }

    // 문법 예문 조회 화면 출력
    function renderGrammarExamples(GrammerExamples) {
        const container = document.getElementById('GrammarContainer');
        container.innerHTML = '';
        GrammerExamples.forEach(grammar => {
            let src = "";
            if(grammar.grade === "A1") {
                src = "/images/bronze.png";
            } else if(grammar.grade === "A2") {
                src = "/images/silver.png";
            } else if(grammar.grade === "B1") {
                src = "/images/gold.png";
            } else if(grammar.grade === "B2") {
                src = "/images/platinum.png";
            } else if(grammar.grade === "C1") {
                src = "/images/diamond.png";
            } else if(grammar.grade === "C2") {
                src = "/images/diamond 2.png";
            }

            const grammarElement = document.createElement('div');
            grammarElement.className = 'vocabulary';
            grammarElement.innerHTML = `
            <div class="vocabulary-header">
                <img src="${src}" alt="" class="grammar-grade">
            </div>
            <div class="example">${grammar.question}</div>
            <form class="answer-form" data-id="${grammar.id}">
                <input id="answer-${grammar.id}" type="text">
                <button class="grading-grammar" type="submit"> 채점하기 </button>
            </form>
        `;

            container.appendChild(grammarElement);
        });
        gradingGrammarExample();
    }

    // 채점 api
    function gradingGrammarExample() {
        const gradingForms = document.querySelectorAll('.answer-form');
        gradingForms.forEach(form => {
            form.addEventListener('submit', async function (event) {
                event.preventDefault(); // 기본 폼 제출 동작을 막음

                const grammarId = form.getAttribute('data-id'); // grammar.id 가져오기
                const answerInput = document.getElementById(`answer-${grammarId}`).value; // 해당 input의 값 가져오기

                try {
                    const response = await fetch(`/api/grammar-examples/${grammarId}/grading`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ answer: answerInput }),
                    });

                    const data = await response.json();
                    console.log('Grading result:', data);

                    if (data.correct) {
                        Swal.fire('정답', `${data.data.commentary}`, 'success');
                    } else {
                        Swal.fire('오답', `${data.data.commentary}`, 'error');
                    }

                } catch (error) {
                    console.error('Error grading grammar example:', error);
                }
            });
        });
    }

    // 문법예문 추가하기 (비동기 처리)
    // function addSubmitEventListeners() {
        document.getElementById("addGrammarExampleButton").addEventListener("click", async function (event) {
            event.preventDefault();

            try {
                const response = await fetch("/api/grammar-examples/more", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include"
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                Swal.fire('문법 예문', `문법 예문을 5개 추가하였습니다.`, 'success');
                window.location.reload();

            } catch (error) {
                console.error('Error adding grammar example:', error);
            }
        });
    // }

    // 페이지네이션
    function renderPagination() {

        const container = document.getElementById('paginationContainer');
        container.innerHTML = '';

        const totalPages = Math.ceil(userGrammarExamples / pageSize);
        const maxVisiblePages = 5;

        let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages);

        // If not enough pages at the end, adjust the start
        if (endPage - startPage < maxVisiblePages) {
            startPage = Math.max(0, endPage - maxVisiblePages);
        }

        // "<<" 버튼 (첫 페이지로 이동)
        if (currentPage > 0) {
            const firstButton = document.createElement('button');
            firstButton.innerText = '<<';
            firstButton.addEventListener('click', () => {
                currentPage = 0;
                fetchGrammarExamples(currentPage);
                renderPagination();
            });
            container.appendChild(firstButton);
        }

        // 페이지 번호 버튼들
        for (let i = startPage; i < endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.innerText = i + 1;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                fetchGrammarExamples(currentPage);
                renderPagination();
            });
            if (i === currentPage) {
                pageButton.disabled = true;
            }
            container.appendChild(pageButton);
        }

        // ">>" 버튼 (마지막 페이지로 이동)
        if (currentPage < totalPages - 1) {
            const lastButton = document.createElement('button');
            lastButton.innerText = '>>';
            lastButton.addEventListener('click', () => {
                currentPage = totalPages - 1;
                fetchGrammarExamples(currentPage);
                renderPagination();
            });
            container.appendChild(lastButton);
        }
    }

});