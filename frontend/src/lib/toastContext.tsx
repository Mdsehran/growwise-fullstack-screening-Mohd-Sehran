'use client';

import React, { createContext, useCallback, useContext, useState, ReactNode } from 'react';

interface ToastMessage {
  id: number;
  text: string;
  variant: 'error' | 'success' | 'info';
}

interface ToastContextValue {
  showToast: (text: string, variant?: ToastMessage['variant']) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

let nextId = 1;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const showToast = useCallback((text: string, variant: ToastMessage['variant'] = 'error') => {
    const id = nextId++;
    setToasts((prev) => [...prev, { id, text, variant }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2" data-testid="toast-container">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            role="alert"
            className={`rounded-md px-4 py-3 text-sm font-medium text-white shadow-lg ${
              toast.variant === 'error'
                ? 'bg-red-600'
                : toast.variant === 'success'
                  ? 'bg-emerald-600'
                  : 'bg-slate-700'
            }`}
          >
            {toast.text}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return ctx;
}
