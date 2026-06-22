'use client';

export type FilterValue = 'ALL' | 'SUCCESS' | 'FAILED' | 'PENDING';

const OPTIONS: FilterValue[] = ['ALL', 'SUCCESS', 'FAILED', 'PENDING'];

export function StatusFilter({
  value,
  onChange,
}: {
  value: FilterValue;
  onChange: (value: FilterValue) => void;
}) {
  return (
    <div className="inline-flex rounded-lg border border-slate-200 bg-white p-1">
      {OPTIONS.map((option) => (
        <button
          key={option}
          type="button"
          onClick={() => onChange(option)}
          className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
            value === option ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          {option}
        </button>
      ))}
    </div>
  );
}
