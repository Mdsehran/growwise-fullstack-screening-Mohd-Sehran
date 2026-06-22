export function EmptyState({ message }: { message?: string }) {
  return (
    <div
      className="flex flex-col items-center justify-center rounded-lg border border-dashed border-slate-300 py-16 text-center"
      data-testid="empty-state"
    >
      <p className="text-lg font-medium text-slate-700">No webhook attempts found</p>
      <p className="mt-1 text-sm text-slate-500">
        {message || 'Try a different status filter, or check back after the next delivery cycle.'}
      </p>
    </div>
  );
}
