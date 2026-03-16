const API_BASE = '/api/v1/subscribers';

const form = document.getElementById('subscriber-form');
const resetBtn = document.getElementById('reset-form');
const refreshBtn = document.getElementById('refresh-list');
const tbody = document.getElementById('subscriber-table-body');
const formErrors = document.getElementById('form-errors');
const formSuccess = document.getElementById('form-success');
const tableMessage = document.getElementById('table-message');

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

async function fetchSubscribers() {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" class="placeholder">Загрузка...</td>
      </tr>
    `;
    hideElement(tableMessage);

    try {
        const res = await fetch(API_BASE);
        if (!res.ok) {
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
        tr.innerHTML = `
          <td>${sub.id ?? ''}</td>
          <td>${sub.msisdn}</td>
          <td>${sub.fullName}</td>
          <td><span class="status-pill ${sub.status}">${sub.status}</span></td>
          <td>${Number(sub.balance).toFixed(2)}</td>
          <td>${sub.createdAt ? sub.createdAt.replace('T', ' ').slice(0, 16) : ''}</td>
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
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (res.status === 400) {
            const body = await res.json().catch(() => ({}));
            if (body && typeof body === 'object') {
                const html = Object.entries(body)
                    .map(([field, msg]) => `<div><strong>${field}</strong>: ${msg}</div>`)
                    .join('');
                showElement(formErrors, html || 'Ошибка валидации', true);
            } else {
                showElement(formErrors, 'Ошибка валидации');
            }
            return;
        }

        if (!res.ok) {
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
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
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
        fullName: formData.get('fullName')?.trim(),
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
    fetchSubscribers();
});

