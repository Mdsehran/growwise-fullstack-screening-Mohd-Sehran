export function LoadingSkeleton() {
  return (
    <div className="animate-pulse space-y-2" data-testid="loading-skeleton">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="h-10 rounded-md bg-slate-100" />
      ))}
    </div>
  );
}
