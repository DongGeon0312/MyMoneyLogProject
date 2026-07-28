// api.js — 모든 화면에서 <script src="api.js"></script>로 먼저 로드
// 같은 오리진(스프링이 정적 파일을 8080에서 함께 서빙)이라 CORS 설정이 필요 없다.
const API_BASE = "";

// 저장된 토큰을 자동으로 Authorization 헤더에 첨부하는 fetch 래퍼.
// 백엔드는 성공/에러 모두 공통 응답 형식으로 응답한다:
//   성공: { success:true, message, data, (meta) }
//   에러: { success:false, code, message, data:null }
async function apiFetch(path, options = {}) {
  const token = localStorage.getItem("accessToken");

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 204) {
    return null;
  }

  const body = await res.json().catch(() => ({}));

  if (!res.ok || body.success === false) {
    if (res.status === 401) {
      localStorage.removeItem("accessToken");
      location.href = "login.html";
      return;
    }
    throw new Error(body.message || `요청 실패 (${body.code || res.status})`);
  }

  return body;
}

function requireLogin() {
  if (!localStorage.getItem("accessToken")) {
    location.href = "login.html";
  }
}

function logout() {
  localStorage.removeItem("accessToken");
  location.href = "login.html";
}

function currentYearMonth() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}
