$(document).ready(function(){
    // 초기에 nav 메뉴를 숨김
    $('nav').hide()
    // .mainMenu 클릭 시 토글 기능을 구현
    $('.mainMenu').click(function (e) {
        e.preventDefault(); // 기본 링크 동작을 방지
        $('nav').slideToggle(300); // 300ms 동안 슬라이드 효과로 토글
        // 메뉴 텍스트를 토글 (선택사항)
        var menuText = $(this).text();
        $(this).text(menuText === 'MENU v' ? 'MENU ^' : 'MENU v');
        })
        // nav 영역 외부 클릭 시 메뉴를 닫기 (선택사항)
    $(document).click(function (e) {
            if (!$(e.target).closest('header').length) {
                $('nav').slideUp(300);
                $('.mainMenu').text('MENU v');
            }});

    //휠(스크롤) 위치에 따라 왼쪽메뉴 활성화.
    $(document).scroll(function(){
        //브라우저높이
        const h2 = $(window).height();

        const t1 = $(document).scrollTop();

        const ht = Math.floor(t1/h1);

        //모든클래스 삭제
        $("a").removeClass();
        //해당 메뉴만 클래스 추가
        $("li").eq(ht).children().addClass("menuOver "+"m"+(ht+1));
    });


});/////////end