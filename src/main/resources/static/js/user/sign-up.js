
  document.addEventListener('DOMContentLoaded', function () {
  let emailVerified = false;
  let nicknameVerified = false;
  const requestVerificationButton = document.getElementById('request-verification');

  async function checkDuplication(field, url, resultSpan) {
  const value = field.value;
  const resultElement = document.getElementById(resultSpan);
  if (value) {
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
  if (field.name === 'email') {
  requestVerificationButton.disabled = true;
}
}
} else {
  resultElement.textContent = '';
  resultElement.classList.remove('success-message', 'error-message');
  if (field.name === 'email') {
  requestVerificationButton.disabled = true;
}
}
}

  document.getElementById('email').addEventListener('blur', function () {
  checkDuplication(this, '/api/email-duplication', 'emailResult');
});

  document.getElementById('nickname').addEventListener('blur', function () {
  checkDuplication(this, '/api/nickname-duplication', 'nicknameResult');
});

  requestVerificationButton.addEventListener('click', async function () {
  const email = document.getElementById('email').value;
  const emailResultElement = document.getElementById('emailResult');
  try {
  const response = await fetch('/api/request-verification', {
  method: 'POST',
  headers: {
  'Content-Type': 'application/x-www-form-urlencoded',
},
  body: `email=${encodeURIComponent(email)}`
});
  if (response.ok) {
  document.getElementById('toggle-content').style.display = 'block';
  emailResultElement.textContent = '인증 코드가 이메일로 전송되었습니다.';
  emailResultElement.classList.remove('error-message');
  emailResultElement.classList.add('success-message');
} else {
  emailResultElement.textContent = '인증 코드 전송에 실패했습니다.';
  emailResultElement.classList.remove('success-message');
  emailResultElement.classList.add('error-message');
}
} catch (error) {
  emailResultElement.textContent = '인증 코드 전송 중 오류가 발생했습니다.';
  emailResultElement.classList.remove('success-message');
  emailResultElement.classList.add('error-message');
}
});

  document.getElementById('verify-email').addEventListener('click', async function () {
  const email = document.getElementById('email').value;
  const code = document.getElementById('authenticationCode').value;
  try {
  const response = await fetch('/api/verify-email', {
  method: 'POST',
  headers: {
  'Content-Type': 'application/x-www-form-urlencoded',
},
  body: `email=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`
});
  const emailVerificationElement = document.getElementById('emailVerification');
  if (response.ok) {
  emailVerificationElement.textContent = '이메일이 인증되었습니다.';
  emailVerificationElement.classList.remove('error-message');
  emailVerificationElement.classList.add('success-message');
  emailVerified = true;
} else {
  emailVerificationElement.textContent = '인증에 실패했습니다.';
  emailVerificationElement.classList.remove('success-message');
  emailVerificationElement.classList.add('error-message');
  emailVerified = false;
}
} catch (error) {
  alert('이메일 인증 중 오류가 발생했습니다.');
}
});

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
  if (document.getElementById('password').value !== document.getElementById(
  'confirmPassword').value) {
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
  email: document.getElementById('email').value,
  nickname: document.getElementById('nickname').value,
  password: document.getElementById('password').value
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
