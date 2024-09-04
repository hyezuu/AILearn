$(document).ready(function() {
    $('.sec1 img').click(function(e) {
        e.preventDefault(); // 링크의 기본 동작 방지

        var index = $(this).index();
        var targetDiv = $('.sec1 div:nth-child(2)').eq(index);

        targetDiv.toggleClass("act");

        // 다른 div들의 act 클래스 제거
        $('.sec1 div:nth-child(2)').not(targetDiv).removeClass('act');
    });
    $(".sec2 img").click(function(e) {
        e.preventDefault(); // 링크의 기본 동작 방지

        var index = $(this).index();
        var targetDiv = $('.sec2 div:nth-child(2)').eq(index);

        targetDiv.toggleClass("act");

        // 다른 div들의 act 클래스 제거
        $('.sec2 div:nth-child(2)').not(targetDiv).removeClass('act');
    });
    $('.sec3 img').click(function(e) {
        e.preventDefault(); // 링크의 기본 동작 방지

        var index = $(this).index();
        var targetDiv = $('.sec3 div:nth-child(2)').eq(index);

        targetDiv.toggleClass("act");

        // 다른 div들의 act 클래스 제거
        $('.sec3 div:nth-child(2)').not(targetDiv).removeClass('act');
    });

});