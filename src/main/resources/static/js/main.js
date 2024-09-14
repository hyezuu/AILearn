$(document).ready(function() {
    // 초기에 nav 메뉴를 숨김
    $('nav').hide();

    // .mainMenu 클릭 시 토글 기능을 구현
    $('.mainMenu').click(function (e) {
        e.preventDefault(); // 기본 링크 동작을 방지
        console.log("toggle")
        $('nav').slideToggle(300); // 300ms 동안 슬라이드 효과로 토글

        // 이미지 소스를 토글
        var img = $(this).find('.menu-img');
        var imgSrc = img.attr('src');
        img.attr('src', imgSrc === '/images/menu-v.png' ? '/images/menu-up.png' : '/images/menu-v.png');
    });

    // nav 영역 외부 클릭 시 메뉴를 닫기 (선택사항)
    $(document).click(function (e) {
        if (!$(e.target).closest('header').length) {
            $('nav').slideUp(300);
            $('.mainMenu img').attr('src', '/images/menu-v.png');
        }
    });
});

getUserId();

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

        // 홈화면에서 로그인한 유저는 마이페이지로 보내기
        let isMainPage = document.getElementById("main-btn");
        if(isMainPage) {
            isMainPage.innerHTML = `
            <a href="/my"><button>내 등급 확인하고 공부하러가기!</button></a>
        `;
        }
        // user.grade가 없는 유저는 헤더에 메뉴와 마이페이지 리스트 제거
        if(data.grade === null) {

        }

        document.getElementById("nickname-button").innerHTML = `
            <a href="/my"><strong>${data.nickname}</strong> 님</a>
        `;
        document.getElementById("nickname-button").style.display = 'block';
        document.getElementById('logout-button').style.display = 'block';

    } catch (error) {
        // console.error('Error fetching user data:', error);
        document.getElementById('login-button').style.display = 'block';
        document.getElementById('signup-button').style.display = 'block';
    }
}
