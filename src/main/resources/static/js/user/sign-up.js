document.addEventListener('DOMContentLoaded', function () {
  let emailVerified = false;
  let nicknameVerified = false;
  const requestVerificationButton = document.getElementById('request-verification');
  const email = document.getElementById('email');
  const nickname = document.getElementById('nickname');
  const password = document.getElementById('password');
  const confirmPassword = document.getElementById('confirmPassword');
  const nicknameResultElement = document.getElementById('nicknameResult');
  const passwordResultElement = document.getElementById('passwordResult');
  const confirmPasswordResultElement = document.getElementById('confirmPasswordResult');
  const emailResultElement = document.getElementById('emailResult');

  // 이메일 유효성 검사
  function validateEmail() {
    const emailValue = email.value.trim();
    if (!emailValue) {
      // 값이 없을 때 에러메시지 지우기
      emailResultElement.textContent = '';
      emailResultElement.classList.remove('error-message', 'success-message');
      emailVerified = false;
      requestVerificationButton.disabled = true;
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailValue)) {
      emailResultElement.textContent = '올바른 이메일 형식으로 입력해주세요!';
      emailResultElement.classList.remove('success-message');
      emailResultElement.classList.add('error-message');
      emailVerified = false;
      requestVerificationButton.disabled = true;
    } else {
      emailResultElement.textContent = '';
      emailResultElement.classList.remove('error-message');
      checkDuplication(email, '/api/email-duplication', 'emailResult');
    }
  }

  // 닉네임 유효성 검사
  function validateNickname() {
    const nicknameValue = nickname.value.trim();
    if (!nicknameValue) {
      // 값이 없을 때 에러메시지 지우기
      nicknameResultElement.textContent = '';
      nicknameResultElement.classList.remove('error-message', 'success-message');
      nicknameVerified = false;
      return;
    }

    if (nicknameValue.length < 2 || nicknameValue.length > 12) {
      nicknameResultElement.textContent = '닉네임은 2~12자 사이여야 합니다.';
      nicknameResultElement.classList.remove('success-message');
      nicknameResultElement.classList.add('error-message');
      nicknameVerified = false;
    } else {
      nicknameResultElement.textContent = '';
      nicknameResultElement.classList.remove('error-message');
      checkDuplication(nickname, '/api/nickname-duplication', 'nicknameResult');
    }
  }

  // 비밀번호 유효성 검사
  function validatePassword() {
    const passwordValue = password.value.trim();
    if (!passwordValue) {
      // 값이 없을 때 에러메시지 지우기
      passwordResultElement.textContent = '';
      passwordResultElement.classList.remove('error-message', 'success-message');
      return false;
    }

    if (passwordValue.length < 8 || passwordValue.length > 16) {
      passwordResultElement.textContent = '비밀번호는 8~16자 사이여야 합니다.';
      passwordResultElement.classList.remove('success-message');
      passwordResultElement.classList.add('error-message');
      return false;
    } else {
      passwordResultElement.textContent = '';
      passwordResultElement.classList.remove('error-message');
      return true;
    }
  }

  // 비밀번호 확인 검사
  function checkPasswordMatch() {
    if (!confirmPassword.value) {
      // 값이 없을 때 에러메시지 지우기
      confirmPasswordResultElement.textContent = '';
      confirmPasswordResultElement.classList.remove('error-message', 'success-message');
      return;
    }

    if (password.value !== confirmPassword.value) {
      confirmPasswordResultElement.textContent = '비밀번호가 일치하지 않습니다.';
      confirmPasswordResultElement.classList.remove('success-message');
      confirmPasswordResultElement.classList.add('error-message');
    } else {
      confirmPasswordResultElement.textContent = '비밀번호가 일치합니다.';
      confirmPasswordResultElement.classList.remove('error-message');
      confirmPasswordResultElement.classList.add('success-message');
    }
  }

  // 중복 검사 함수
  async function checkDuplication(field, url, resultSpan) {
    const value = field.value.trim();
    const resultElement = document.getElementById(resultSpan);

    if (!value) {
      // 값이 없을 때 에러메시지 지우기
      resultElement.textContent = '';
      resultElement.classList.remove('error-message', 'success-message');
      return;
    }

    try {
      const response = await fetch(`${url}?${field.name}=${encodeURIComponent(value)}`);
      const result = await response.json();

      if (result) {
        resultElement.textContent = '이미 사용중입니다.';
        resultElement.classList.remove('success-message');
        resultElement.classList.add('error-message');

        if (field.name === 'email') {
          emailVerified = false;
          requestVerificationButton.disabled = true;
        }
        if (field.name === 'nickname') {
          nicknameVerified = false;
        }
      } else {
        resultElement.textContent = '사용 가능합니다.';
        resultElement.classList.remove('error-message');
        resultElement.classList.add('success-message');

        if (field.name === 'email') {
          emailVerified = true;
          requestVerificationButton.disabled = false;
        }
        if (field.name === 'nickname') {
          nicknameVerified = true;
        }
      }
    } catch (error) {
      resultElement.textContent = '확인 중 오류가 발생했습니다.';
      resultElement.classList.remove('success-message');
      resultElement.classList.add('error-message');
    }
  }

  // 이벤트 리스너 추가
  email.addEventListener('input', validateEmail);
  nickname.addEventListener('input', validateNickname);
  password.addEventListener('input', validatePassword);
  confirmPassword.addEventListener('input', checkPasswordMatch);

  // 블러 이벤트에서도 유효성 검사 메시지가 남아있게 처리
  email.addEventListener('blur', validateEmail);
  nickname.addEventListener('blur', validateNickname);
  password.addEventListener('blur', validatePassword);
  confirmPassword.addEventListener('blur', checkPasswordMatch);

  // 제출 시 모든 유효성 및 중복 검사 확인
  document.getElementById('signupForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    if (!emailVerified) {
      alert('이메일 인증이 필요합니다.');
      return;
    }
    if (!nicknameVerified) {
      alert('닉네임 중복 확인이 필요합니다.');
      return;
    }
    if (password.value !== confirmPassword.value) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const response = await fetch('/api/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: email.value,
          nickname: nickname.value,
          password: password.value,
        })
      });

      if (response.ok) {
        alert('회원가입이 완료되었습니다.');
        window.location.href = '/login';
      } else {
        const errorText = await response.text();
        alert('회원가입에 실패했습니다: ' + errorText);
      }
    } catch (error) {
      alert('회원가입 처리 중 오류가 발생했습니다.');
    }
  });
});
