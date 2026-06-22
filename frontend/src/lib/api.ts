import type { PagedResponse, SimulatedUser, WebhookAttempt } from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

function authHeaders(user: SimulatedUser): HeadersInit {
  return {
    'X-User-Id': user.id,
    'X-Role': user.role,
    'Content-Type': 'application/json',
  };
}

export async function fetchWebhookAttempts(
  user: SimulatedUser,
  status: string,
  page: number,
  size: number
): Promise<PagedResponse<WebhookAttempt>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (status && status !== 'ALL') {
    params.set('status', status);
  }

  const res = await fetch(`${API_BASE_URL}/api/admin/webhook-attempts?${params.toString()}`, {
    headers: authHeaders(user),
    cache: 'no-store',
  });

  if (!res.ok) {
    if (res.status === 403) {
      throw new ApiError(403, `${user.role} is not authorized to view webhook attempts.`);
    }
    throw new ApiError(res.status, `Failed to load webhook attempts (HTTP ${res.status}).`);
  }

  return res.json();
}

export async function retryWebhookAttempt(user: SimulatedUser, attemptId: string): Promise<WebhookAttempt> {
  const res = await fetch(`${API_BASE_URL}/api/admin/webhook-attempts/${attemptId}/retry`, {
    method: 'POST',
    headers: authHeaders(user),
  });

  if (!res.ok) {
    let message = `Retry failed (HTTP ${res.status}).`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
    } catch {
      // Response had no JSON body — fall back to the generic message above.
    }
    throw new ApiError(res.status, message);
  }

  return res.json();
}
