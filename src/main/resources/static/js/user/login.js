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

      const contentType = response.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const data = await response.json();
        if (response.ok) {
          // 로그인 성공
          window.location.href = data.redirectUrl;
        } else {
          // 로그인 실패
          showMessage(data.message || '로그인 중 오류가 발생했습니다.');
        }
      } else {
        // JSON이 아닌 응답 처리
        showMessage('예기치 않은 응답 형식입니다.');
      }
    } catch (error) {
      console.error('Error:', error);
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