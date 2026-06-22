import type { Metadata } from 'next';
import './globals.css';
import { AuthProvider } from '@/lib/authContext';
import { ToastProvider } from '@/lib/toastContext';

export const metadata: Metadata = {
  title: 'Webhook Delivery Log Dashboard',
  description: 'GrowWise integrations dashboard for webhook delivery attempts',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="bg-slate-50 text-slate-900">
        <AuthProvider>
          <ToastProvider>{children}</ToastProvider>
        </AuthProvider>
      </body>
    </html>
  );
}
