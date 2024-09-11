document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('upgrade-quiz-form') || document.getElementById('quiz-form');
    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            if (form.id === 'upgrade-quiz-form') {
                submitUpgradeTests(event);
            } else {
                submitLevelTests(event);
            }
        });
    }
    submitForm();
});

async function submitForm() {
    $('#gradeForm').on('submit', async function (event) {
        event.preventDefault();

        const formData = new FormData(this);
        const selectedGrade = formData.get('grade');

        if (selectedGrade) {
            if (selectedGrade === 'A1') {
                const result = Swal.fire({
                    title: 'A1 등급이 부여되었습니다.',
                    icon: 'info',
                }).then(() => {
                    $.ajax({
                        url: '/grade/A1',
                        method: 'POST',
                        success: function () {
                            window.location.href = '/test-result';
                        },
                        error: function (response) {
                            swal('warning', '오류 발생',  response.message);
                        }
                    });
                })
            } else {
                const result = await Swal.fire({
                    title: `${selectedGrade} 레벨 테스트를 진행하시겠습니까?`,
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: '네',
                    cancelButtonText: '아니오'
                });

                if (result.isConfirmed) {
                    $.ajax({
                        url: `/level-tests?grade=${selectedGrade}`,
                        method: 'POST',
                        success: function (response) {
                            if (response) {
                                window.location.href = `/tests/level-tests?grade=${selectedGrade}`;
                            } else {
                                swal('warning', '오류 발생',  response.message);
                            }
                        },
                        error: function (xhr) {
                            swal('warning', '오류 발생',  '서버 오류가 발생했습니다.');

                        }
                    });
                }
            }
        } else {
            swal('warning', '레벨을 선택해 주세요!', '');
        }
    });
}



async function submitLevelTests(event) {
    event.preventDefault();
    const form = event.target;
    const testedGrade = form.querySelector('button[type="submit"]').getAttribute('data-tested-grade');
    const formData = new FormData(form);

    const submitRequestDto = [];
    const testIds = formData.getAll('testId');
    const answers = formData.getAll('answer');

    for (let i = 0; i < testIds.length; i++) {
        submitRequestDto.push({ testId: parseInt(testIds[i]), answer: answers[i] });
    }

    const submitRequestVo = { dtoList: submitRequestDto };

    const result = await Swal.fire({
        title: `제출하시겠습니까?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '네',
        cancelButtonText: '아니오'
    });

    if (result.isConfirmed) {
        $.ajax({
            url: `/grade?grade=${encodeURIComponent(testedGrade)}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(submitRequestVo),
            success: function (data) {
                if (data.status === 'success') {
                    window.location.href = '/test-result';
                } else if (data.status === 'fail') {
                    if (data.grade === 'A1') {
                        Swal.fire({
                            icon: 'warning',
                            title: '테스트 실패',
                            text: '테스트를 통과하지 못했습니다.'
                        }).then(() => {
                            $.ajax({
                                url: '/grade/A1',
                                method: 'POST',
                                success: function (response) {
                                    window.location.href = '/test-result';
                                }
                            });
                        });
                    } else {
                        Swal.fire({
                            icon: 'warning',
                            title: '레벨 테스트 실패',
                            text: `${data.grade} 등급 테스트를 시작합니다.`
                        }).then(() => {
                            $.ajax({
                                url: `/level-tests?grade=${encodeURIComponent(data.grade)}`,
                                method: 'POST',
                                success: function (response) {
                                    window.location.href = `/tests/level-tests?grade=${data.grade}`;
                                },
                                error: function (xhr) {
                                    swal('error', '오류 발생', ' 레벨 테스트 중 오류가 발생했습니다.');
                                }
                            });
                        });
                    }
                } else {
                    swal('error', '오류 발생', '알 수 없는 오류가 발생했습니다.');

                }
            },
            error: function (xhr) {
                swal('error', '오류 발생', '테스트 처리 중 오류가 발생했습니다.');
            }
        });
    }
    return false;
}

async function submitUpgradeTests(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    const submitRequestDto = [];
    const testIds = formData.getAll('testId');
    const answers = formData.getAll('answer');

    for (let i = 0; i < testIds.length; i++) {
        submitRequestDto.push({ testId: parseInt(testIds[i]), answer: answers[i] });
    }

    const submitRequestVo = { dtoList: submitRequestDto };

    const result = await Swal.fire({
        title: '제출하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '네',
        cancelButtonText: '아니오'
    });

    if (result.isConfirmed) {
        $.ajax({
            url: '/api/upgrade',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(submitRequestVo),
            success: function (data) {
                if (data.status === 'success') {
                    Swal.fire({
                        icon: 'success',
                        title: '승급',
                        text: '다음 등급으로 올라갑니다.'
                    }).then(() => {
                        window.location.href = '/test-result';
                    });
                } else if (data.status === 'keep') {
                    Swal.fire({
                        icon: 'info',
                        title: '등급 유지',
                        text: '현재 등급이 유지됩니다.'
                    }).then(() => {
                        window.location.href = '/test-result';
                    });
                } else {
                    Swal.fire({
                        icon: 'warning',
                        title: '강등',
                        text: '등급이 하향되었습니다.'
                    }).then(() => {
                        window.location.href = '/test-result';
                    });
                }
            },
            error: function (xhr) {
                swal('error', '오류 발생', '테스트 처리 중 오류가 발생했습니다.');
            }
        });
    }
    return false;
}

function swal(icon, title, text) {
    Swal.fire({
        icon: icon,
        title: title,
        text: text
    })
}