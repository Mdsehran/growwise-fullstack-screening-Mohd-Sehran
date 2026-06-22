export type AttemptStatus = 'SUCCESS' | 'FAILED' | 'PENDING';

export interface WebhookAttempt {
  id: string;
  subscriptionId: string;
  eventType: string;
  status: AttemptStatus;
  responseCode: number | null;
  createdAt: string;
  lastError: string | null;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type Role = 'ADMIN' | 'INSTRUCTOR' | 'STUDENT';

export interface SimulatedUser {
  id: string;
  role: Role;
  label: string;
}
