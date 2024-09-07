document.addEventListener('DOMContentLoaded', () => {
  const passwordResetForm = document.getElementById('passwordResetForm');
  const emailInput = document.getElementById('email');
  const emailResult = document.getElementById('emailResult');
  const resetPasswordButton = document.getElementById('reset-password-button');

  passwordResetForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = emailInput.value;

    // 요청 시작 시 메시지 초기화 및 버튼 비활성화
    emailResult.textContent = '';
    emailResult.classList.remove('error-message', 'success-message');
    resetPasswordButton.disabled = true;
    resetPasswordButton.textContent = '처리 중...';

    try {
      const response = await fetch(`/api/auth/password?email=${encodeURIComponent(email)}`, {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error('서버 응답이 실패했습니다.');
      }

      emailResult.textContent = '임시 비밀번호가 이메일로 전송되었습니다. 이메일을 확인해주세요.';
      emailResult.classList.add('success-message');
    } catch (error) {
      console.error('Error:', error);
      emailResult.textContent = '임시 비밀번호 발급에 실패했습니다. 이메일을 확인해주세요.';
      emailResult.classList.add('error-message');
    } finally {
      // 요청 완료 후 버튼 상태 복원
      resetPasswordButton.disabled = false;
      resetPasswordButton.textContent = '임시 비밀번호 발급';
    }
  });
});