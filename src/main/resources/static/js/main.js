// $(document).ready(function() {
//     // 초기에 nav 메뉴를 숨김
//     $('nav').hide()
//     // .mainMenu 클릭 시 토글 기능을 구현
//     $('.mainMenu').click(function (e) {
//         e.preventDefault(); // 기본 링크 동작을 방지
//         $('nav').slideToggle(300); // 300ms 동안 슬라이드 효과로 토글
//         // 메뉴 텍스트를 토글 (선택사항)
//         var menuText = $(this).text();
//         $(this).text(menuText === 'MENU v' ? 'MENU ^' : 'MENU v');
//     })
//     // nav 영역 외부 클릭 시 메뉴를 닫기 (선택사항)
//     $(document).click(function (e) {
//         if (!$(e.target).closest('header').length) {
//             $('nav').slideUp(300);
//             $('.mainMenu').text('MENU v');
//         }
//     });
// });
$(document).ready(function() {
    // 초기에 nav 메뉴를 숨김
    $('nav').hide();

    // .mainMenu 클릭 시 토글 기능을 구현
    $('.mainMenu').click(function (e) {
        e.preventDefault(); // 기본 링크 동작을 방지
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

        document.getElementById('logout-button').style.display = 'block';
        document.getElementById('logout-button').style.color = '#777';

    } catch (error) {
        // console.error('Error fetching user data:', error);
        document.getElementById('login-button').style.display = 'block';
        document.getElementById('signup-button').style.display = 'block';
    }
}
