import type { WebhookAttempt } from '@/lib/types';
import { StatusBadge } from './StatusBadge';
import { RetryButton } from './RetryButton';

export function WebhookTable({
  attempts,
  onRetried,
}: {
  attempts: WebhookAttempt[];
  onRetried: () => void;
}) {
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">ID</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Subscription</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Event Type</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Status</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Response Code</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Created At</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Last Error</th>
            <th className="px-4 py-2 text-left font-semibold text-slate-600">Action</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {attempts.map((attempt) => (
            <tr key={attempt.id}>
              <td className="px-4 py-2 font-mono text-xs text-slate-500">{attempt.id}</td>
              <td className="px-4 py-2 text-slate-700">{attempt.subscriptionId}</td>
              <td className="px-4 py-2 text-slate-700">{attempt.eventType}</td>
              <td className="px-4 py-2">
                <StatusBadge status={attempt.status} />
              </td>
              <td className="px-4 py-2 text-slate-700">{attempt.responseCode ?? '—'}</td>
              <td className="px-4 py-2 text-slate-500">{new Date(attempt.createdAt).toLocaleString()}</td>
              <td className="px-4 py-2 text-slate-500">{attempt.lastError ?? '—'}</td>
              <td className="px-4 py-2">
                {attempt.status === 'FAILED' && <RetryButton attemptId={attempt.id} onRetried={onRetried} />}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
