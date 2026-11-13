import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Transaction } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';
import { UploadCsvDialog } from '../../components/upload-csv-dialog/upload-csv-dialog';
import { TransactionsPage } from './transactions-page';

describe('TransactionsPage', () => {
  let component: TransactionsPage;
  let fixture: ComponentFixture<TransactionsPage>;
  let element: HTMLElement;
  const serviceStub = {
    transactions: signal<Transaction[]>([]).asReadonly(),
    error: signal<string | null>(null).asReadonly(),
    refresh: vi.fn(),
    importCsv: vi.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionsPage],
      providers: [{ provide: TransactionService, useValue: serviceStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionsPage);
    component = fixture.componentInstance;
    element = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  afterEach(() => {
    component.dialog.closeAll();
  });

  it('renders the page header and the transaction list', () => {
    expect(element.querySelector('h1')?.textContent).toContain('Transactions');
    expect(element.querySelector('app-transaction-list')).not.toBeNull();
  });

  it('opens the upload dialog when the upload button is clicked', () => {
    const open = vi.spyOn(component.dialog, 'open');

    element.querySelector<HTMLButtonElement>('.transactions-page__upload-button')!.click();

    expect(open).toHaveBeenCalledWith(
      UploadCsvDialog,
      expect.objectContaining({
        panelClass: 'upload-dialog-panel',
        backdropClass: 'upload-dialog-backdrop',
      }),
    );
  });

  it('renders the upload dialog in an overlay', async () => {
    component.openUploadDialog();
    await fixture.whenStable();

    expect(document.querySelector('app-upload-csv-dialog')).not.toBeNull();
    expect(document.querySelector('.upload-dialog-panel')).not.toBeNull();
  });
});
