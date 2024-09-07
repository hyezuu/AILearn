document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('loginForm');
  const messageContainer = document.getElementById('message-container');
  const submitButton = loginForm.querySelector('button[type="submit"]');

  loginForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(loginForm);

    submitButton.disabled = true;
    submitButton.textContent = '로그인 중...';

    try {
      const response = await fetch('/login', {
        method: 'POST',
        body: formData,
        headers: {
          'X-Requested-With': 'XMLHttpRequest'
        }
      });

      if (response.ok || response.redirected) {
        window.location.href = '/';
        return;
      }

      const errorData = await response.json();
      showMessage(errorData.message || '로그인 중 오류가 발생했습니다.');
    } catch (error) {
      showMessage('서버와의 통신 중 오류가 발생했습니다.');
    } finally {
      submitButton.disabled = false;
      submitButton.textContent = '로그인';
    }
  });

  function showMessage(message) {
    messageContainer.textContent = message;
    messageContainer.style.display = 'block';

    setTimeout(() => {
      messageContainer.style.display = 'none';
    }, 5000);
  }
});