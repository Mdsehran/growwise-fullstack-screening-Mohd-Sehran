import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { RetryButton } from '@/components/RetryButton';
import { AuthProvider } from '@/lib/authContext';
import { ToastProvider } from '@/lib/toastContext';

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <AuthProvider>
      <ToastProvider>{ui}</ToastProvider>
    </AuthProvider>
  );
}

describe('RetryButton', () => {
  beforeEach(() => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ id: 'new-id', status: 'PENDING' }),
    }) as jest.Mock;
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('calls the retry API and toggles the loading/disabled state', async () => {
    const onRetried = jest.fn();
    renderWithProviders(<RetryButton attemptId="att-0004" onRetried={onRetried} />);

    const button = screen.getByTestId('retry-button-att-0004');
    expect(button).not.toBeDisabled();

    fireEvent.click(button);

    expect(button).toBeDisabled();
    expect(button).toHaveTextContent(/retrying/i);

    await waitFor(() => expect(onRetried).toHaveBeenCalledTimes(1));

    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/admin/webhook-attempts/att-0004/retry'),
      expect.objectContaining({ method: 'POST' })
    );

    await waitFor(() => expect(button).not.toBeDisabled());
  });
});
