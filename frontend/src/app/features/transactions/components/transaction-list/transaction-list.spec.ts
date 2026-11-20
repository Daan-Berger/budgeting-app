import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Transaction } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';
import { TransactionList } from './transaction-list';

function makeTransaction(overrides: Partial<Transaction> = {}): Transaction {
  return {
    id: '1',
    date: '2026-01-15',
    amount: -12.5,
    counterparty: 'Supermarket',
    counterpartyIban: null,
    description: null,
    categoryId: null,
    categoryName: null,
    ...overrides,
  };
}

describe('TransactionList', () => {
  let fixture: ComponentFixture<TransactionList>;
  let element: HTMLElement;
  const transactions = signal<Transaction[]>([]);
  const error = signal<string | null>(null);
  const serviceStub = {
    transactions: transactions.asReadonly(),
    error: error.asReadonly(),
    refresh: vi.fn(),
  };

  beforeEach(async () => {
    transactions.set([]);
    error.set(null);
    serviceStub.refresh.mockReset();

    await TestBed.configureTestingModule({
      imports: [TransactionList],
      providers: [{ provide: TransactionService, useValue: serviceStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionList);
    element = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  it('refreshes transactions on init', () => {
    expect(serviceStub.refresh).toHaveBeenCalledTimes(1);
  });

  it('shows an empty state when there are no transactions', () => {
    expect(element.querySelector('.transaction-list__message')?.textContent).toContain(
      'No transactions found.',
    );
    expect(element.querySelectorAll('.transaction-card').length).toBe(0);
  });

  it('shows the error message instead of the list when loading failed', async () => {
    transactions.set([makeTransaction()]);
    error.set('Http failure response');
    await fixture.whenStable();

    const message = element.querySelector('.transaction-list__message--error');
    expect(message?.textContent).toContain('Http failure response');
    expect(element.querySelectorAll('.transaction-card').length).toBe(0);
  });

  it('renders a card per transaction with formatted amount, date and category', async () => {
    transactions.set([
      makeTransaction({ id: '1', categoryName: 'Groceries' }),
      makeTransaction({ id: '2', amount: 2500, counterparty: 'Employer', date: '2026-02-01' }),
    ]);
    await fixture.whenStable();

    const cards = element.querySelectorAll('.transaction-card');
    expect(cards.length).toBe(2);

    const [expense, income] = Array.from(cards);
    expect(expense.querySelector('.transaction-card__amount')?.textContent).toContain('-€12.50');
    expect(expense.querySelector('.transaction-card__name')?.textContent).toContain('Supermarket');
    expect(expense.querySelector('.transaction-card__category')?.textContent).toContain(
      'Groceries',
    );
    expect(expense.querySelector('.transaction-card__date')?.textContent).toContain('15-01-2026');

    expect(income.querySelector('.transaction-card__amount')?.textContent).toContain('€2,500.00');
    expect(income.querySelector('.transaction-card__category')?.textContent).toContain(
      'Uncategorized',
    );
  });

  it('marks negative amounts and non-negative amounts with different classes', async () => {
    transactions.set([
      makeTransaction({ id: '1', amount: -1 }),
      makeTransaction({ id: '2', amount: 0 }),
    ]);
    await fixture.whenStable();

    const amounts = element.querySelectorAll('.transaction-card__amount');
    expect(amounts[0].classList).toContain('transaction-card__amount--negative');
    expect(amounts[0].classList).not.toContain('transaction-card__amount--positive');
    expect(amounts[1].classList).toContain('transaction-card__amount--positive');
    expect(amounts[1].classList).not.toContain('transaction-card__amount--negative');
  });
});
