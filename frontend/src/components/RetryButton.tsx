'use client';

import { useState } from 'react';
import { useAuth } from '@/lib/authContext';
import { useToast } from '@/lib/toastContext';
import { retryWebhookAttempt, ApiError } from '@/lib/api';

export function RetryButton({ attemptId, onRetried }: { attemptId: string; onRetried: () => void }) {
  const { currentUser } = useAuth();
  const { showToast } = useToast();
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleRetry() {
    if (isSubmitting) return;
    setIsSubmitting(true);
    try {
      await retryWebhookAttempt(currentUser, attemptId);
      showToast('Retry queued — a new PENDING attempt was created.', 'success');
      onRetried();
    } catch (err) {
      const message = err instanceof ApiError ? err.message : 'Unexpected error while retrying.';
      showToast(message, 'error');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <button
      type="button"
      onClick={handleRetry}
      disabled={isSubmitting}
      data-testid={`retry-button-${attemptId}`}
      className="rounded-md bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-50"
    >
      {isSubmitting ? 'Retrying…' : 'Retry'}
    </button>
  );
}
