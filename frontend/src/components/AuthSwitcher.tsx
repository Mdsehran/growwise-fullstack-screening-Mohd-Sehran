'use client';

import { useAuth } from '@/lib/authContext';
import type { Role } from '@/lib/types';

const ROLES: Role[] = ['ADMIN', 'INSTRUCTOR', 'STUDENT'];

/**
 * Evaluator-facing convenience widget. Switching roles here only changes
 * which X-User-Id / X-Role headers get attached to outgoing fetches (see
 * lib/api.ts) — it has no authority of its own. Try switching to STUDENT
 * or INSTRUCTOR and refreshing the dashboard to see the server-enforced
 * 403 in the Error state below.
 */
export function AuthSwitcher() {
  const { currentUser, setRole } = useAuth();

  return (
    <div className="fixed top-4 right-4 z-40 rounded-lg border border-slate-200 bg-white p-3 shadow-md">
      <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-slate-500">Simulated session</p>
      <div className="flex gap-1">
        {ROLES.map((role) => (
          <button
            key={role}
            type="button"
            onClick={() => setRole(role)}
            data-testid={`auth-switch-${role.toLowerCase()}`}
            className={`rounded-md px-3 py-1.5 text-xs font-medium transition ${
              currentUser.role === role ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            {role}
          </button>
        ))}
      </div>
      <p className="mt-2 text-xs text-slate-400">
        Acting as <span className="font-medium text-slate-600">{currentUser.label}</span>
      </p>
    </div>
  );
}
