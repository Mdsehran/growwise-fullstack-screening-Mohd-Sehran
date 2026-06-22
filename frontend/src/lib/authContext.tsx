'use client';

import React, { createContext, useContext, useState, ReactNode } from 'react';
import type { Role, SimulatedUser } from './types';

/**
 * These IDs match the deterministic seed IDs in the backend's DataSeeder
 * (DataSeeder.ADMIN_ID / INSTRUCTOR_ID / STUDENT_ID), so switching profiles
 * here actually corresponds to a real seeded user on the backend.
 */
export const SIMULATED_USERS: Record<Role, SimulatedUser> = {
  ADMIN: { id: '11111111-1111-1111-1111-111111111111', role: 'ADMIN', label: 'Ava Admin' },
  INSTRUCTOR: { id: '22222222-2222-2222-2222-222222222222', role: 'INSTRUCTOR', label: 'Ian Instructor' },
  STUDENT: { id: '33333333-3333-3333-3333-333333333333', role: 'STUDENT', label: 'Sam Student' },
};

interface AuthContextValue {
  currentUser: SimulatedUser;
  setRole: (role: Role) => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<SimulatedUser>(SIMULATED_USERS.ADMIN);

  const setRole = (role: Role) => {
    setCurrentUser(SIMULATED_USERS[role]);
  };

  return <AuthContext.Provider value={{ currentUser, setRole }}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}
