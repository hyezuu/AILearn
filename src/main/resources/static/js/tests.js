function submitForm() {
    document.querySelector('#gradeForm').addEventListener('submit', function (event) {
            event.preventDefault();

            // Fetch를 이용해 폼 데이터 전송
            const formData = new FormData(this);

            // grade 값 추출 (필요 시)
            const selectedGrade = formData.get('grade');
            if (selectedGrade) {
                if (selectedGrade === 'A1') {
                    fetch('/grade/A1', {method: "POST"}).then(response => {
                        window.location.href = '/test-result';
                    })
                } else {
                    fetch(`/level-tests?grade=${selectedGrade}`, {method: "POST"})
                        .then(response => {
                            if (response.redirected) {
                                // 요청이 성공적으로 처리되면 페이지 이동
                                return fetch(response.url).then(response => {
                                    window.location.href = response.url;
                                });
                            } else {
                                alert("요청 처리 중 오류가 발생했습니다.");
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert("요청을 처리할 수 없습니다.");
                        });
                }
            } else {
                alert("레벨을 선택해 주세요!");
            }
        }
    )
    ;
}

document.addEventListener('DOMContentLoaded', function () {
    submitLevelTests();
    submitUpgradeTests()
    submitForm();
});

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
});

function submitLevelTests(event) {
    const form = event.target;
    const testedGrade = form.querySelector('button[type="submit"]').getAttribute('data-tested-grade');

    // Form 데이터를 가져오기
    const formData = new FormData(form);

    const submitRequestDto = [];
    const testIds = formData.getAll('testId'); // 모든 testId 값을 가져옴
    const answers = formData.getAll('answer'); // 모든 answer 값을 가져옴

    // testId와 answer를 묶어서 DTO 배열을 생성
    for (let i = 0; i < testIds.length; i++) {
        const testId = parseInt(testIds[i]);
        const answer = answers[i];
        submitRequestDto.push({testId, answer});
    }

    const submitRequestVo = {
        dtoList: submitRequestDto
    };

    fetch('/grade?grade=' + encodeURIComponent(testedGrade), {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'  // JSON 형식으로 설정
        },
        body: JSON.stringify(submitRequestVo) // JSON 문자열로 변환
    })
        .then(response => response.json())  // 서버에서 JSON 형태로 응답이 올 것으로 가정
        .then(data => {
            const result = data.status;
            const resultGrade = data.grade;

            if (result === 'success') {
                window.location.href = '/test-result';

            } else if (result === "fail") {
                if (resultGrade === 'A1') {
                    alert("테스트를 통과하지 못했습니다.");
                    fetch('/grade/A1', {method: "POST"})
                        .then(response => {
                            window.location.href = '/test-result';
                        })
                } else {
                    // 실패 시 메시지
                    alert("레벨 테스트에 실패했습니다. " + resultGrade + " 등급 테스트를 시작합니다.");
                    // 필요시 다른 페이지로 리다이렉트
                    fetch('/level-tests?grade=' + encodeURIComponent(resultGrade), {method: "POST"})
                        .then(response => {
                            if (response.redirected) {
                                // 리다이렉트된 경우 응답의 최종 URL을 얻어옴
                                return fetch(response.url).then(response => {
                                    window.location.href = response.url;
                                });
                            } else {
                                return response.text();
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert("오류가 발생했습니다. 다시 시도해 주세요.");
                        })
                }
            } else {
                // 예상치 못한 경우 처리
                alert("알 수 없는 오류가 발생했습니다.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("오류가 발생했습니다. 다시 시도해 주세요.");
        });

    // 폼이 기본적으로 제출되지 않도록 방지
    return false;
}

function submitUpgradeTests(event) {
    event.preventDefault();
    const form = event.target;
    const testedGrade = form.querySelector('button[type="submit"]').getAttribute('data-tested-grade');

    // Form 데이터를 가져오기
    const formData = new FormData(form);

    const submitRequestDto = [];
    const testIds = formData.getAll('testId'); // 모든 testId 값을 가져옴
    const answers = formData.getAll('answer'); // 모든 answer 값을 가져옴

    // testId와 answer를 묶어서 DTO 배열을 생성
    for (let i = 0; i < testIds.length; i++) {
        const testId = parseInt(testIds[i]);
        const answer = answers[i];
        submitRequestDto.push({testId, answer});
    }

    const submitRequestVo = {
        dtoList: submitRequestDto
    };

    fetch('/api/upgrade', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'  // JSON 형식으로 설정
        },
        body: JSON.stringify(submitRequestVo) // JSON 문자열로 변환
    })
        .then(response => response.json())  // 서버에서 JSON 형태로 응답이 올 것으로 가정
        .then(data => {

            if (data.status === 'success') alert("승급에 성공하셨습니다!");
            else if (data.status === 'keep') alert("현재 등급이 유지됩니다.");
            else alert("등급이 하향 되었습니다.");

            window.location.href = '/test-result';
        })
        .catch(error => {
            console.error('Error:', error);
            alert("오류가 발생했습니다. 다시 시도해 주세요.");
        });

    // 폼이 기본적으로 제출되지 않도록 방지
    return false;

}