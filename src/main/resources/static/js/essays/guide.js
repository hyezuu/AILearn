$(document).ready(function() {
    $('.sec1').click(function(e) {
        e.preventDefault()
        // 다른 div들의 act 클래스 제거
        $('section div').removeClass('act');

        $('.sec1 div:nth-child(2)').toggleClass("act");
    });
    $(".sec2").click(function(e) {
        e.preventDefault()
        // 다른 div들의 act 클래스 제거
        $('section div').removeClass('act');

        $('.sec2 div:nth-child(2)').toggleClass('act');
    });
    $('.sec3').click(function(e) {
        e.preventDefault()
        // 다른 div들의 act 클래스 제거
        $('section div').removeClass('act');

        $('.sec3 div:nth-child(2)').toggleClass('act');
    });
});