
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