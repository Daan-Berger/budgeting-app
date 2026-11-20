import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ImportResult } from '../models/import-result.model';
import { Transaction } from '../models/transaction.model';
import { TransactionService } from './transaction.service';

const transactions: Transaction[] = [
  {
    id: '1',
    date: '2026-01-15',
    amount: -12.5,
    counterparty: 'Supermarket',
    counterpartyIban: 'BE00 0000 0000 0000',
    description: 'Groceries',
    categoryId: 'c1',
    categoryName: 'Groceries',
  },
];

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('starts with no transactions and no error', () => {
    expect(service.transactions()).toEqual([]);
    expect(service.error()).toBeNull();
  });

  it('getTransactions requests GET /api/transaction', () => {
    let result: Transaction[] | undefined;
    service.getTransactions().subscribe((data) => (result = data));

    const req = httpMock.expectOne('/api/transaction');
    expect(req.request.method).toBe('GET');
    req.flush(transactions);

    expect(result).toEqual(transactions);
  });

  it('refresh stores the fetched transactions', () => {
    service.refresh();
    httpMock.expectOne('/api/transaction').flush(transactions);

    expect(service.transactions()).toEqual(transactions);
    expect(service.error()).toBeNull();
  });

  it('refresh sets the error message when the request fails', () => {
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});

    service.refresh();
    httpMock
      .expectOne('/api/transaction')
      .flush('boom', { status: 500, statusText: 'Server Error' });

    expect(service.error()).toContain('500');
    expect(service.transactions()).toEqual([]);
    expect(consoleError).toHaveBeenCalled();
  });

  it('refresh clears a previous error', () => {
    vi.spyOn(console, 'error').mockImplementation(() => {});
    service.refresh();
    httpMock.expectOne('/api/transaction').flush(null, { status: 500, statusText: 'Error' });
    expect(service.error()).not.toBeNull();

    service.refresh();
    expect(service.error()).toBeNull();
    httpMock.expectOne('/api/transaction').flush(transactions);
  });

  it('refresh cancels a pending refresh so the latest one wins', () => {
    service.refresh();
    const first = httpMock.expectOne('/api/transaction');
    service.refresh();
    const second = httpMock.expectOne('/api/transaction');

    expect(first.cancelled).toBe(true);
    second.flush(transactions);
    expect(service.transactions()).toEqual(transactions);
  });

  it('importCsv posts the file as multipart form data and refreshes afterwards', () => {
    const file = new File(['a;b'], 'statement.csv', { type: 'text/csv' });
    const importResult: ImportResult = { totalRows: 3, imported: 2, skippedDuplicates: 1 };
    let result: ImportResult | undefined;

    service.importCsv(file).subscribe((data) => (result = data));

    const req = httpMock.expectOne('/api/transaction/import');
    expect(req.request.method).toBe('POST');
    const body = req.request.body as FormData;
    const sent = body.get('file') as File;
    expect(sent.name).toBe('statement.csv');
    req.flush(importResult);

    expect(result).toEqual(importResult);
    httpMock.expectOne('/api/transaction').flush(transactions);
    expect(service.transactions()).toEqual(transactions);
  });

  it('importCsv does not refresh when the import fails', () => {
    const file = new File(['bad'], 'bad.csv', { type: 'text/csv' });
    let failed = false;

    service.importCsv(file).subscribe({ error: () => (failed = true) });
    httpMock
      .expectOne('/api/transaction/import')
      .flush(null, { status: 400, statusText: 'Bad Request' });

    expect(failed).toBe(true);
    httpMock.expectNone('/api/transaction');
  });
});
