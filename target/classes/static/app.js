const API_BASE = '/api/subscribers';
const AUTH_BASE = '/api/auth';

const form = document.getElementById('subscriber-form');
const resetBtn = document.getElementById('reset-form');
const refreshBtn = document.getElementById('refresh-list');
const tbody = document.getElementById('subscriber-table-body');
const formErrors = document.getElementById('form-errors');
const formSuccess = document.getElementById('form-success');
const tableMessage = document.getElementById('table-message');

const tabLogin = document.getElementById('tab-login');
const tabRegister = document.getElementById('tab-register');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const authErrors = document.getElementById('auth-errors');
const authSuccess = document.getElementById('auth-success');
const tokenBox = document.getElementById('token-box');
const tokenValue = document.getElementById('token-value');
const logoutBtn = document.getElementById('logout-btn');

function showElement(el, text, isHtml = false) {
    if (!el) return;
    if (text !== undefined) {
        if (isHtml) {
            el.innerHTML = text;
        } else {
            el.textContent = text;
        }
    }
    el.classList.remove('hidden');
}

function hideElement(el) {
    if (!el) return;
    el.classList.add('hidden');
}

function setToken(token) {
    if (token) {
        localStorage.setItem('jwt', token);
    } else {
        localStorage.removeItem('jwt');
    }
    renderAuthState();
}

function getToken() {
    return localStorage.getItem('jwt');
}

function authHeaders() {
    const token = getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

function renderAuthState() {
    const token = getToken();
    if (token) {
        if (tokenBox && tokenValue) {
            tokenValue.textContent = token;
            showElement(tokenBox);
        }
        if (logoutBtn) logoutBtn.classList.remove('hidden');
    } else {
        hideElement(tokenBox);
        if (logoutBtn) logoutBtn.classList.add('hidden');
    }
}

function setAuthTab(tab) {
    hideElement(authErrors);
    hideElement(authSuccess);
    if (tab === 'register') {
        hideElement(loginForm);
        showElement(registerForm);
    } else {
        hideElement(registerForm);
        showElement(loginForm);
    }
}

async function authRequest(path, payload) {
    const res = await fetch(`${AUTH_BASE}${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    const body = await res.json().catch(() => null);
    if (!res.ok) {
        const message = body?.message || body?.error || `Ошибка: ${res.status}`;
        const fields = body?.fieldErrors;
        if (fields && typeof fields === 'object') {
            const html = Object.entries(fields)
                .map(([field, msg]) => `<div><strong>${field}</strong>: ${msg}</div>`)
                .join('');
            showElement(authErrors, html || message, true);
        } else {
            showElement(authErrors, message);
        }
        throw new Error(message);
    }

    return body;
}

async function fetchSubscribers() {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" class="placeholder">Загрузка...</td>
      </tr>
    `;
    hideElement(tableMessage);

    try {
        const res = await fetch(API_BASE, { headers: { ...authHeaders() } });
        if (!res.ok) {
            if (res.status === 401) throw new Error('Нужно войти (JWT).');
            throw new Error('Ошибка при загрузке списка абонентов');
        }
        const data = await res.json();
        renderSubscribers(data);
        tableMessage.textContent = `Загружено абонентов: ${data.length}`;
        showElement(tableMessage);
    } catch (e) {
        tbody.innerHTML = `
          <tr>
            <td colspan="7" class="placeholder">Не удалось загрузить абонентов: ${e.message}</td>
          </tr>
        `;
    }
}

function renderSubscribers(list) {
    if (!list || list.length === 0) {
        tbody.innerHTML = `
          <tr>
            <td colspan="7" class="placeholder">Пока нет созданных абонентов</td>
          </tr>
        `;
        return;
    }

    tbody.innerHTML = '';
    list.forEach(sub => {
        const tr = document.createElement('tr');
        const fullName = `${sub.lastName ?? ''} ${sub.firstName ?? ''}`.trim();
        tr.innerHTML = `
          <td>${sub.id ?? ''}</td>
          <td>${sub.msisdn}</td>
          <td>${fullName}</td>
          <td><span class="status-pill ${sub.status}">${sub.status}</span></td>
          <td>${Number(sub.balance).toFixed(2)}</td>
          <td>${sub.tariffName ?? ''}</td>
          <td>
            <button class="danger" data-id="${sub.id}">Удалить</button>
          </td>
        `;
        tbody.appendChild(tr);
    });
}

async function createSubscriber(payload) {
    hideElement(formErrors);
    hideElement(formSuccess);

    try {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...authHeaders()
            },
            body: JSON.stringify(payload)
        });

        if (res.status === 400) {
            const body = await res.json().catch(() => ({}));
            const fields = body?.fieldErrors;
            if (fields && typeof fields === 'object') {
                const html = Object.entries(fields)
                    .map(([field, msg]) => `<div><strong>${field}</strong>: ${msg}</div>`)
                    .join('');
                showElement(formErrors, html || 'Ошибка валидации', true);
            } else {
                showElement(formErrors, body?.message || 'Ошибка валидации');
            }
            return;
        }

        if (!res.ok) {
            if (res.status === 401) throw new Error('Нужно войти (JWT).');
            throw new Error(`Ошибка сервера: ${res.status}`);
        }

        const created = await res.json();
        showElement(formSuccess, `Абонент ${created.msisdn} успешно создан`);
        form.reset();
        await fetchSubscribers();
    } catch (e) {
        showElement(formErrors, e.message || 'Неизвестная ошибка');
    }
}

async function deleteSubscriber(id) {
    if (!id) return;
    const confirmDelete = window.confirm('Удалить абонента #' + id + '?');
    if (!confirmDelete) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE', headers: { ...authHeaders() } });
        if (!res.ok && res.status !== 204) {
            throw new Error('Не удалось удалить абонента');
        }
        await fetchSubscribers();
    } catch (e) {
        alert(e.message || 'Ошибка при удалении');
    }
}

form.addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(form);
    const payload = {
        msisdn: formData.get('msisdn')?.trim(),
        firstName: formData.get('firstName')?.trim(),
        lastName: formData.get('lastName')?.trim(),
        email: (formData.get('email') || '').trim() || null,
        status: formData.get('status'),
        balance: parseFloat(formData.get('balance') || '0')
    };
    createSubscriber(payload);
});

resetBtn.addEventListener('click', () => {
    form.reset();
    hideElement(formErrors);
    hideElement(formSuccess);
});

refreshBtn.addEventListener('click', () => {
    fetchSubscribers();
});

tbody.addEventListener('click', (e) => {
    const target = e.target;
    if (target.matches('button.danger')) {
        const id = target.getAttribute('data-id');
        deleteSubscriber(id);
    }
});

document.addEventListener('DOMContentLoaded', () => {
    renderAuthState();
    setAuthTab('login');
    fetchSubscribers();
});

tabLogin?.addEventListener('click', () => setAuthTab('login'));
tabRegister?.addEventListener('click', () => setAuthTab('register'));

logoutBtn?.addEventListener('click', () => {
    setToken(null);
    showElement(authSuccess, 'Вы вышли из аккаунта');
});

loginForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideElement(authErrors);
    hideElement(authSuccess);
    const fd = new FormData(loginForm);
    const payload = {
        username: (fd.get('username') || '').trim(),
        password: (fd.get('password') || '').trim()
    };
    try {
        const body = await authRequest('/login', payload);
        setToken(body.token);
        showElement(authSuccess, `Вход успешен: ${body.username}`);
        await fetchSubscribers();
    } catch (err) {
        // handled by authRequest
    }
});

registerForm?.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideElement(authErrors);
    hideElement(authSuccess);
    const fd = new FormData(registerForm);
    const payload = {
        username: (fd.get('username') || '').trim(),
        email: (fd.get('email') || '').trim(),
        password: (fd.get('password') || '').trim()
    };
    try {
        const body = await authRequest('/register', payload);
        setToken(body.token);
        showElement(authSuccess, `Регистрация успешна: ${body.username}`);
        await fetchSubscribers();
    } catch (err) {
        // handled by authRequest
    }
});

