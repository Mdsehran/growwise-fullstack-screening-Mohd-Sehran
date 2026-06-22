import type { AttemptStatus } from '@/lib/types';

const STYLES: Record<AttemptStatus, string> = {
  SUCCESS: 'bg-emerald-100 text-emerald-700 border-emerald-300',
  FAILED: 'bg-red-100 text-red-700 border-red-300',
  PENDING: 'bg-amber-100 text-amber-700 border-amber-300',
};

export function StatusBadge({ status }: { status: AttemptStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${STYLES[status]}`}>
      {status}
    </span>
  );
}
