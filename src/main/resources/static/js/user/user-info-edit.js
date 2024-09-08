document.addEventListener('DOMContentLoaded', function() {
  const form = document.getElementById('updateUserForm');
  const nicknameInput = document.getElementById('nickname');
  const passwordInput = document.getElementById('password');
  const passwordConfirmInput = document.getElementById('confirmPassword');
  const nicknameResult = document.getElementById('nicknameResult');
  const passwordResult = document.getElementById('passwordResult');
  const confirmPasswordResult = document.getElementById('confirmPasswordResult');
  const withdrawLink = document.getElementById('withdrawLink');
  const modal = document.getElementById('withdrawalModal');
  const confirmWithdrawal = document.getElementById('confirmWithdrawal');
  const cancelWithdrawal = document.getElementById('cancelWithdrawal');

  let nicknameVerified = false;
  const originalNickname = form.getAttribute('data-current-nickname');

  // 초기 닉네임 상태 설정
  function setInitialNicknameState() {
    if (nicknameInput.value.trim() === originalNickname) {
      nicknameInput.classList.add('current-nickname');
      nicknameVerified = true;
    }
  }

  // 닉네임 유효성 검사
  function validateNickname() {
    const nicknameValue = nicknameInput.value.trim();

    if (!nicknameValue) {
      nicknameResult.textContent = '';
      nicknameResult.className = 'validation-message';
      nicknameInput.classList.remove('current-nickname');
      nicknameVerified = false;
      return;
    }

    if (nicknameValue === originalNickname) {
      nicknameResult.textContent = ''; // 메시지 제거
      nicknameResult.className = 'validation-message';
      nicknameInput.classList.add('current-nickname');
      nicknameVerified = true;
      return;
    }

    nicknameInput.classList.remove('current-nickname');

    if (nicknameValue.length < 2 || nicknameValue.length > 12) {
      nicknameResult.textContent = '닉네임은 2~12자 사이여야 합니다.';
      nicknameResult.className = 'validation-message error-message';
      nicknameVerified = false;
    } else {
      checkDuplication(nicknameInput, '/api/nickname-duplication', nicknameResult);
    }
  }

  // 비밀번호 유효성 검사
  function validatePassword() {
    const passwordValue = passwordInput.value.trim();
    if (!passwordValue) {
      passwordResult.textContent = '';
      passwordResult.className = 'validation-message';
      return false;
    }

    if (passwordValue.length < 8 || passwordValue.length > 16) {
      passwordResult.textContent = '비밀번호는 8~16자 사이여야 합니다.';
      passwordResult.className = 'validation-message error-message';
      return false;
    } else {
      passwordResult.textContent = '';
      passwordResult.className = 'validation-message';
      return true;
    }
  }

  // 비밀번호 확인 검사
  function checkPasswordMatch() {
    if (!passwordConfirmInput.value) {
      confirmPasswordResult.textContent = '';
      confirmPasswordResult.className = 'validation-message';
      return;
    }

    if (passwordInput.value !== passwordConfirmInput.value) {
      confirmPasswordResult.textContent = '비밀번호가 일치하지 않습니다.';
      confirmPasswordResult.className = 'validation-message error-message';
    } else {
      confirmPasswordResult.textContent = '비밀번호가 일치합니다.';
      confirmPasswordResult.className = 'validation-message success-message';
    }
  }

  // 중복 검사 함수
  async function checkDuplication(field, url, resultElement) {
    const value = field.value.trim();

    if (!value || value === originalNickname) {
      resultElement.textContent = '';
      resultElement.className = 'validation-message';
      nicknameVerified = (value === originalNickname);
      return;
    }

    try {
      const response = await fetch(`${url}?${field.name}=${encodeURIComponent(value)}`);
      const result = await response.json();

      if (result) {
        resultElement.textContent = '이미 사용중인 닉네임입니다.';
        resultElement.className = 'validation-message error-message';
        nicknameVerified = false;
      } else {
        resultElement.textContent = '사용 가능한 닉네임입니다.';
        resultElement.className = 'validation-message success-message';
        nicknameVerified = true;
      }
    } catch (error) {
      resultElement.textContent = '확인 중 오류가 발생했습니다.';
      resultElement.className = 'validation-message error-message';
      nicknameVerified = false;
    }
  }

  // 페이지 로드 시 초기 닉네임 상태 설정
  setInitialNicknameState();

  // 이벤트 리스너 추가
  nicknameInput.addEventListener('input', validateNickname);
  passwordInput.addEventListener('input', validatePassword);
  passwordConfirmInput.addEventListener('input', checkPasswordMatch);

  // 블러 이벤트에서도 유효성 검사 메시지가 남아있게 처리
  nicknameInput.addEventListener('blur', validateNickname);
  passwordInput.addEventListener('blur', validatePassword);
  passwordConfirmInput.addEventListener('blur', checkPasswordMatch);

  // 회원정보 수정 폼 제출
  form.addEventListener('submit', async function(e) {
    e.preventDefault();

    if (!nicknameVerified) {
      alert('유효한 닉네임을 입력해주세요.');
      return;
    }
    if (passwordInput.value && passwordInput.value !== passwordConfirmInput.value) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const updateData = {
      nickname: nicknameInput.value
    };

    if (passwordInput.value) {
      updateData.password = passwordInput.value;
    }

    try {
      const response = await fetch('/api/me', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
      });

      if (response.ok) {
        alert("회원정보가 성공적으로 업데이트되었습니다.");
        window.location.href = '/my';
      } else {
        const errorData = await response.json();
        alert("회원정보 업데이트 실패: " + errorData.message);
      }
    } catch (error) {
      console.error("Error updating user info:", error);
      alert("회원정보 업데이트 중 오류가 발생했습니다.");
    }
  });

  // 탈퇴 링크 이벤트 리스너 (모달 열기)
  if (withdrawLink) {
    withdrawLink.addEventListener('click', function(e) {
      e.preventDefault();
      modal.style.display = 'block';
    });
  }

  // 모달 닫기 버튼
  if (cancelWithdrawal) {
    cancelWithdrawal.addEventListener('click', function() {
      modal.style.display = 'none';
    });
  }

  // 모달 외부 클릭 시 닫기
  window.addEventListener('click', function(event) {
    if (event.target == modal) {
      modal.style.display = 'none';
    }
  });

  // 탈퇴 확인 버튼 클릭 이벤트
  if (confirmWithdrawal) {
    confirmWithdrawal.addEventListener('click', withdrawUser);
  }

  async function withdrawUser() {
    try {
      const response = await fetch('/api/withdrawal', {
        method: 'DELETE'
      });

      if (response.ok) {
        alert('회원 탈퇴가 완료되었습니다. 이용해 주셔서 감사합니다.');
        window.location.href = '/'; // 메인 페이지로 리다이렉트
      } else {
        const errorData = await response.json();
        alert('탈퇴 처리 중 오류가 발생했습니다: ' + errorData.message);
      }
    } catch (error) {
      console.error('Error during withdrawal:', error);
      alert('탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    } finally {
      modal.style.display = 'none';
    }
  }
});