'use client';

import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '@/lib/authContext';
import { useToast } from '@/lib/toastContext';
import { fetchWebhookAttempts, ApiError } from '@/lib/api';
import type { WebhookAttempt } from '@/lib/types';
import { AuthSwitcher } from '@/components/AuthSwitcher';
import { StatusFilter, type FilterValue } from '@/components/StatusFilter';
import { WebhookTable } from '@/components/WebhookTable';
import { Pagination } from '@/components/Pagination';
import { EmptyState } from '@/components/EmptyState';
import { LoadingSkeleton } from '@/components/LoadingSkeleton';

const PAGE_SIZE = 5;

export default function AdminDashboardPage() {
  const { currentUser } = useAuth();
  const { showToast } = useToast();

  const [status, setStatus] = useState<FilterValue>('ALL');
  const [page, setPage] = useState(0);
  const [attempts, setAttempts] = useState<WebhookAttempt[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadAttempts = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const result = await fetchWebhookAttempts(currentUser, status, page, PAGE_SIZE);
      setAttempts(result.content);
      setTotalPages(result.totalPages);
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unexpected error while loading attempts.';
      setErrorMessage(message);
      setAttempts([]);
      setTotalPages(0);
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  }, [currentUser, status, page, showToast]);

  // Reset to page 0 whenever the filter or the simulated user changes, so
  // we never end up requesting a page that no longer exists.
  useEffect(() => {
    setPage(0);
  }, [status, currentUser]);

  useEffect(() => {
    loadAttempts();
  }, [loadAttempts]);

  return (
    <main className="mx-auto max-w-5xl px-6 py-10">
      <AuthSwitcher />

      <h1 className="text-2xl font-semibold text-slate-900">Webhook Delivery Log Dashboard</h1>
      <p className="mt-1 text-sm text-slate-500">Admin-only view of integration webhook delivery attempts.</p>

      <div className="mt-6 flex items-center justify-between">
        <StatusFilter value={status} onChange={setStatus} />
        <button
          type="button"
          onClick={loadAttempts}
          className="rounded-md border border-slate-200 px-3 py-1.5 text-sm font-medium text-slate-600 hover:bg-slate-100"
        >
          Refresh
        </button>
      </div>

      <div className="mt-4">
        {isLoading ? (
          <LoadingSkeleton />
        ) : errorMessage ? (
          <div
            role="alert"
            data-testid="error-state"
            className="rounded-lg border border-red-200 bg-red-50 px-4 py-6 text-center text-sm font-medium text-red-700"
          >
            {errorMessage}
          </div>
        ) : attempts.length === 0 ? (
          <EmptyState />
        ) : (
          <>
            <WebhookTable attempts={attempts} onRetried={loadAttempts} />
            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
    </main>
  );
}
