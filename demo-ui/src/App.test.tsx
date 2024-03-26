import { render, screen } from '@testing-library/react'
import App from './App'
import '@testing-library/jest-dom'

describe('trivial test', () => {
  it('Element exists', () => {
    render(<App />);
    const linkElement = screen.getByText(/Demo App/i);
    expect(linkElement).toBeInTheDocument();
  })
})