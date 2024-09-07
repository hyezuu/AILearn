document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('loginForm');
  const messageContainer = document.getElementById('message-container');
  const urlParams = new URLSearchParams(window.location.search);
  const errorType = urlParams.get('error');
  const errorMessage = urlParams.get('message');

  function showMessage(message, isError = true) {
    messageContainer.textContent = message;
    messageContainer.className = isError ? 'validation-message error-message' : 'validation-message success-message';
    messageContainer.style.display = 'block';
  }

  if (errorType || errorMessage) {
    let message;
    switch(errorType) {
      case 'DEACTIVATED':
        message = '탈퇴한 계정입니다.';
        break;
      case 'SUSPENDED':
        message = '정지된 계정입니다.';
        break;
      case 'INVALID_CREDENTIALS':
        message = '이메일 혹은 비밀번호가 일치하지 않습니다.';
        break;
      case 'AUTHENTICATION_FAILED':
        message = '인증 중 오류가 발생했습니다.';
        break;
      default:
        message = errorMessage ? decodeURIComponent(errorMessage) : '로그인 중 오류가 발생했습니다.';
    }
    showMessage(message);
  }

  loginForm.addEventListener('submit', function(e) {
    // 필요한 경우 여기에 추가 로직을 구현할 수 있습니다.
  });
});