document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('loginForm');
  const messageContainer = document.getElementById('message-container');

  loginForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = new FormData(loginForm);

    fetch('/login', {
      method: 'POST',
      body: formData,
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      }
    })
    .then(response => {
      if (!response.ok) {
        return response.json().then(errorData => {
          throw errorData;
        });
      }
      window.location.href = '/'; // 로그인 성공 시 리다이렉트
    })
    .catch(errorData => {
      showMessage(errorData.message, true);
    });
  });

  function showMessage(message, isError = true) {
    messageContainer.textContent = message;
    messageContainer.className = isError ? 'error-message' : 'success-message';
    messageContainer.style.display = 'block';

    // 5초 후 메시지 숨기기
    setTimeout(() => {
      messageContainer.style.display = 'none';
    }, 5000);
  }
});