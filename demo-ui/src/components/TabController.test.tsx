import { render, screen } from '@testing-library/react'
import TabController from './TabController'
import '@testing-library/jest-dom'
import { act } from 'react-dom/test-utils';

describe('Tab Links Correctly Display Pane', () => {
  it('Initial Active State (Transfer BTC)', () => {
    render(<TabController />);
    expect(screen.getByRole("tabpanel", { name: /transfer/i})).toHaveClass('show')
    expect(screen.getByRole("tabpanel", { name: /view/i})).not.toHaveClass('show')
  })
  it('View Wallet clicked', () => {
    render(<TabController />);
    act(() => screen.getByRole("tab", { name: "View Wallet"}).click());
    expect(screen.getByRole("tabpanel", { name: /transfer/i})).not.toHaveClass('show')
    expect(screen.getByRole("tabpanel", { name: /view/i})).toHaveClass('show')
  })
  it('Transfer BTC unclicked, then clicked', () => {
    render(<TabController />);
    act(() => screen.getByRole("tab", { name: /view/i}).click())
    act(() => screen.getByRole("tab", { name: /transfer/i}).click())
    expect(screen.getByRole("tabpanel", { name: /transfer/i})).toHaveClass('show')
    expect(screen.getByRole("tabpanel", { name: /view/i})).not.toHaveClass('show')
  })
})