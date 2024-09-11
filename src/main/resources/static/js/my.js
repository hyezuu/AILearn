
// 네비게이션 메뉴 토글
$(document).ready(function() {
    // 초기에 nav 메뉴를 숨깁니다.
    $('nav').hide();

    // .mainMenu 클릭 시 토글 기능을 구현합니다.
    $('.mainMenu').click(function(e) {
        e.preventDefault(); // 기본 링크 동작을 방지합니다.
        $('nav').slideToggle(300); // 300ms 동안 슬라이드 효과로 토글합니다.

        // 메뉴 텍스트를 토글합니다 (선택사항)
        var menuText = $(this).text();
        $(this).text(menuText === 'MENU v' ? 'MENU ^' : 'MENU v');
    });

    // nav 영역 외부 클릭 시 메뉴를 닫습니다 (선택사항)
    $(document).click(function(e) {
        if (!$(e.target).closest('header').length) {
            $('nav').slideUp(300);
            $('.mainMenu').text('MENU v');
        }
    });
});

// 페이지 로드시 로그인 확인해서 LOGOUT 출력
window.onload = function () {
fetch('/api/me')
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Network response was not ok');
        }
    })
    .then(user => {
        document.getElementById('logout-button').style.display = 'block';
        displayProfile(user);
    })
    .catch(error => {
        document.getElementById('login-button').style.display = 'block';
        document.getElementById('signup-button').style.display = 'block';
    });

    function displayProfile(user) {
        const imageElement = document.getElementById('profile-img');

        console.log(user.id);
        if(user.id % 3 === 2) {
            imageElement.src = '/images/profile1.png';
        } else if (user.id % 3 === 0) {
            imageElement.src = '/images/profile2.png';
        } else {
            imageElement.src = '/images/profile.png';
        }

    }
};

// 프로필 경험치 바
document.addEventListener("DOMContentLoaded", function () {
const pointValue = document.getElementById("point-text").textContent;
const point = parseInt(pointValue, 10); // point 값을 정수로 변환
const progressBar = document.getElementById("progress-bar");
const pointDisplay = document.getElementById("point-display");

// 포인트에 따른 진행 바의 너비를 계산 (포인트가 0에서 10 사이이므로 10%씩 증가)
const progressPercentage = (point / 10) * 100;

// 진행 바 업데이트
progressBar.style.width = progressPercentage + "%";

// point 값을 progress-bar의 오른쪽 끝 부분에 표시
pointDisplay.style.left = progressPercentage + "%";
pointDisplay.textContent = `${point} p`;


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
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const userGrade = parseInt(document.getElementById("user-grade").textContent);

    const userGradeSpan = document.getElementById("user-grade");
    const gradeBox = document.getElementById("user-grade-box")
    const gradeImg = document.getElementById("user-grade-image");

    // 이미지 추가 함수
    function addImage(imgSrc) {
        const imgElement = document.createElement("img");
        imgElement.src = imgSrc;
        imgElement.alt = "User Grade Image";
        imgElement.style.width = "70px";  // 이미지 크기를 원하는 대로 설정
        gradeImg.appendChild(imgElement);
    }

    const box2 = document.getElementById("user-grade-box4-1");
    const diaBox = document.createElement("div")
    diaBox.className = "user-grade-box4-box";

    // userGrade에 따라 텍스트와 이미지를 설정
    switch (userGrade) {
        case 0:
            userGradeSpan.innerText = "A1";
            addImage("/images/bronze.png"); // 이미지 경로 설정
            gradeBox.classList.replace("user-grade-box", "user-grade-box0");
            break;
        case 1:
            userGradeSpan.innerText = "A2";
            addImage("/images/silver.png");
            gradeBox.classList.replace("user-grade-box", "user-grade-box1");
            break;
        case 2:
            userGradeSpan.innerText = "B1";
            addImage("/images/gold.png");
            gradeBox.classList.replace("user-grade-box", "user-grade-box2");
            break;
        case 3:
            userGradeSpan.innerText = "B2";
            addImage("/images/platinum.png");
            gradeBox.classList.replace("user-grade-box", "user-grade-box3");
            break;
        case 4:
            userGradeSpan.innerText = "C1";
            addImage("/images/diamond.png");
            gradeBox.classList.replace("user-grade-box", "user-grade-box4");
            box2.appendChild(diaBox);
            break;
        case 5:
            userGradeSpan.innerText = "C2";
            addImage("/images/diamond 2.png");
            gradeBox.classList.replace("user-grade-box", "user-grade-box5");
            break;
        default:
            userGradeSpan.innerText = "Unknown";
            break;
    }


    const ready = document.getElementById("ready-upgrade").innerText;

    // 5단위 레벨에서만 테스트 가능
    document.getElementById("test-button").addEventListener("click", function(event) {

        event.preventDefault();

        if(ready === "true"){
            window.location.href = "/upgrade-tests";
        } else {
            // window.alert("5단위 레벨에서만 테스트를 진행할 수 있습니다.\n(예시: Lv.5, Lv.10, Lv.15 에서만 가능)")
            Swal.fire('다음 5단위 레벨에서 테스트 자격이 주어집니다!', `예시: Lv.5, Lv.10, Lv.15 ... 에서 테스트 자격 획득`, 'warning');
        }
    });

    const check = document.createElement("span");
    check.innerHTML = `
        <div class="image-container">
            <img src="/images/check.png" alt="check Image" class="check-img">
            <div class="tooltip" id="tooltip">등급 테스트에 도전할 수 있습니다!</div>
        </div>
    `;

    if(ready === "true") {
        document.getElementById("user-info-title").appendChild(check);
    }

    //단어장 생성
    const createVocabularyLink = document.getElementById('createVocabularyLink');

    if (createVocabularyLink) {
        createVocabularyLink.addEventListener('click', function(event) {
            event.preventDefault();
            createVocabularyList();
        });
    }

    function createVocabularyList() {
        createVocabularyLink.textContent = '단어장 생성 중...';
        createVocabularyLink.style.pointerEvents = 'none'; // 중복 클릭 방지

        fetch('/api/vocabulary-list', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(() => {
            alert('단어장이 성공적으로 생성되었습니다.');
            window.location.href = '/vocabulary-list'; // 단어장 페이지로 리다이렉트
        })
        .catch(error => {
            console.error('Error creating vocabulary list:', error);
            createVocabularyLink.textContent = '단어장 만들기 + (재시도)';
            createVocabularyLink.style.pointerEvents = 'auto'; // 재시도 가능하도록 활성화
        });
    }
})