
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
    })
    .catch(error => {
        document.getElementById('login-button').style.display = 'block';
        document.getElementById('signup-button').style.display = 'block';
    });
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
});
