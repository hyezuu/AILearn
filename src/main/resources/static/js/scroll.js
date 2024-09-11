document.addEventListener('DOMContentLoaded', function() {
    const main = document.querySelector('main');
    let isScrolling = false;

    function smoothScroll(target) {
        if (isScrolling) return;
        isScrolling = true;
        target.scrollIntoView({
            behavior: 'smooth'
        });
        setTimeout(() => {
            isScrolling = false;
        }, 1000); // 스크롤 애니메이션 시간과 일치시킵니다.
    }

    main.addEventListener('wheel', function(e) {
        e.preventDefault();
        const delta = e.deltaY;
        const currentSection = document.elementFromPoint(window.innerWidth / 2, window.innerHeight / 2).closest('section');

        if (delta > 0 && currentSection.nextElementSibling) {
            smoothScroll(currentSection.nextElementSibling);
        } else if (delta < 0 && currentSection.previousElementSibling) {
            smoothScroll(currentSection.previousElementSibling);
        }
    }, { passive: false });
});