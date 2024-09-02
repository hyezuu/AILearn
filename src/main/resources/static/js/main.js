$(document).ready(function(){
    $("a.mainMenu").mouseenter(function(){
        $("header nav").stop().slideDown();
    });
    $("header nav a").mouseenter(function(){
        $("a.mainMenu").removeClass('act');
        $(this).parent().prev().addClass('act');
    });
    $("header ul").mouseleave(function(){
        $("a.mainMenu").removeClass('act');
        $("header nav").stop().slideUp();
    });

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