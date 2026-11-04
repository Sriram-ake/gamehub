/**
 * GameHub API Client
 * Centralized fetch wrapper with automatic JWT token attachment and error parsing.
 */

const BASE_URL = '/api/v1';

export class ApiError extends Error {
  constructor(status, message, fieldErrors = null) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint}`;
  const token = localStorage.getItem('gamehub_token');

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  const config = {
    ...options,
    headers
  };

  try {
    const response = await fetch(url, config);

    // Handle 204 No Content
    if (response.status === 204) {
      return null;
    }

    let data = null;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    }

    if (!response.ok) {
      if (response.status === 401) {
        // Expired or invalid token
        if (token) {
          localStorage.removeItem('gamehub_token');
          localStorage.removeItem('gamehub_user');
          const currentPath = window.location.pathname;
          if (!currentPath.includes('login.html') && !currentPath.includes('register.html')) {
            window.location.href = `/login.html?expired=true&returnUrl=${encodeURIComponent(currentPath)}`;
          }
        }
      }

      const errorMessage = data?.message || `HTTP ${response.status}: ${response.statusText}`;
      const fieldErrors = data?.fieldErrors || null;
      throw new ApiError(response.status, errorMessage, fieldErrors);
    }

    return data;
  } catch (err) {
    if (err instanceof ApiError) {
      throw err;
    }
    throw new ApiError(0, err.message || 'Network error occurred. Please check your connection.');
  }
}

export const api = {
  get: (endpoint, headers) => request(endpoint, { method: 'GET', headers }),
  post: (endpoint, body, headers) => request(endpoint, { method: 'POST', body: JSON.stringify(body), headers }),
  put: (endpoint, body, headers) => request(endpoint, { method: 'PUT', body: JSON.stringify(body), headers }),
  delete: (endpoint, headers) => request(endpoint, { method: 'DELETE', headers })
};
