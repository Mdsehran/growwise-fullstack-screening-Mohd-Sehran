import { render, screen } from '@testing-library/react';
import { EmptyState } from '@/components/EmptyState';

describe('EmptyState', () => {
  it('renders an empty-state message when there is no data', () => {
    render(<EmptyState />);
    expect(screen.getByTestId('empty-state')).toBeInTheDocument();
    expect(screen.getByText(/No webhook attempts found/i)).toBeInTheDocument();
  });
});
