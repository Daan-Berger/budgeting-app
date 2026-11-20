import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { of, Subject, throwError } from 'rxjs';
import { ImportResult } from '../../models/import-result.model';
import { TransactionService } from '../../services/transaction.service';
import { UploadCsvDialog } from './upload-csv-dialog';

describe('UploadCsvDialog', () => {
  let component: UploadCsvDialog;
  let fixture: ComponentFixture<UploadCsvDialog>;
  let element: HTMLElement;
  const dialogRefStub = { close: vi.fn() };
  const serviceStub = { importCsv: vi.fn() };

  function selectFile(file: File | null): void {
    const input = element.querySelector<HTMLInputElement>('#csv-file')!;
    Object.defineProperty(input, 'files', { value: file ? [file] : [], configurable: true });
    input.dispatchEvent(new Event('change'));
  }

  function submitButton(): HTMLButtonElement {
    return element.querySelector<HTMLButtonElement>('.upload-dialog__submit')!;
  }

  beforeEach(async () => {
    dialogRefStub.close.mockReset();
    serviceStub.importCsv.mockReset();

    await TestBed.configureTestingModule({
      imports: [UploadCsvDialog],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefStub },
        { provide: TransactionService, useValue: serviceStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UploadCsvDialog);
    component = fixture.componentInstance;
    element = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  it('starts without a selected file and with the submit button disabled', () => {
    expect(component.selectedFile()).toBeNull();
    expect(submitButton().disabled).toBe(true);
    expect(element.querySelector('.upload-dialog__dropzone-title')?.textContent).toContain(
      'Choose a CSV file',
    );
  });

  it('shows the selected file name and size and enables submit', async () => {
    selectFile(new File(['x'.repeat(2048)], 'statement.csv', { type: 'text/csv' }));
    await fixture.whenStable();

    expect(component.selectedFile()?.name).toBe('statement.csv');
    expect(element.querySelector('.upload-dialog__file-name')?.textContent).toContain(
      'statement.csv',
    );
    expect(element.querySelector('.upload-dialog__dropzone-hint')?.textContent).toContain('2.0 KB');
    expect(element.querySelector('.upload-dialog__dropzone--selected')).not.toBeNull();
    expect(submitButton().disabled).toBe(false);
  });

  it('clears the selection when the file picker is emptied', async () => {
    selectFile(new File(['a'], 'a.csv'));
    selectFile(null);
    await fixture.whenStable();

    expect(component.selectedFile()).toBeNull();
    expect(submitButton().disabled).toBe(true);
  });

  async function importWithResult(result: ImportResult): Promise<void> {
    serviceStub.importCsv.mockReturnValue(of(result));
    selectFile(new File(['a'], 'a.csv'));
    await fixture.whenStable();
    submitButton().click();
    await fixture.whenStable();
  }

  function resultText(): string | undefined {
    return element.querySelector('.upload-dialog__result-title')?.textContent?.trim();
  }

  it('imports the selected file and shows the result instead of closing', async () => {
    const file = new File(['a'], 'a.csv');
    serviceStub.importCsv.mockReturnValue(of({ totalRows: 2, imported: 2, skippedDuplicates: 0 }));
    selectFile(file);
    await fixture.whenStable();

    submitButton().click();
    await fixture.whenStable();

    expect(serviceStub.importCsv).toHaveBeenCalledWith(file);
    expect(dialogRefStub.close).not.toHaveBeenCalled();
    expect(resultText()).toBe('Imported 2 new transactions.');
    expect(element.querySelector('.upload-dialog__dropzone')).toBeNull();
    expect(element.querySelector('.upload-dialog__cancel')).toBeNull();
  });

  it('closes via the Done button after a successful import', async () => {
    await importWithResult({ totalRows: 1, imported: 1, skippedDuplicates: 0 });

    expect(submitButton().textContent?.trim()).toBe('Done');
    submitButton().click();

    expect(dialogRefStub.close).toHaveBeenCalled();
  });

  it.each<[ImportResult, string]>([
    [{ totalRows: 1, imported: 1, skippedDuplicates: 0 }, 'Imported 1 new transaction.'],
    [
      { totalRows: 42, imported: 30, skippedDuplicates: 12 },
      'Imported 30 new transactions. 12 were already imported.',
    ],
    [
      { totalRows: 2, imported: 1, skippedDuplicates: 1 },
      'Imported 1 new transaction. 1 was already imported.',
    ],
    [
      { totalRows: 42, imported: 0, skippedDuplicates: 42 },
      'No new transactions; this file was already imported.',
    ],
    [
      { totalRows: 0, imported: 0, skippedDuplicates: 0 },
      'The file did not contain any transactions.',
    ],
  ])('shows the import result %o as "%s"', async (result, expected) => {
    await importWithResult(result);

    expect(resultText()).toBe(expected);
  });

  it('shows the row counts below the message', async () => {
    await importWithResult({ totalRows: 42, imported: 30, skippedDuplicates: 12 });

    const status = element.querySelector('[role="status"]');
    expect(status?.querySelector('.upload-dialog__dropzone-hint')?.textContent).toBe(
      '42 rows in file · 30 new · 12 already imported',
    );
  });

  it('uses the singular for a single row', async () => {
    await importWithResult({ totalRows: 1, imported: 1, skippedDuplicates: 0 });

    expect(element.querySelector('.upload-dialog__dropzone-hint')?.textContent).toBe(
      '1 row in file · 1 new · 0 already imported',
    );
  });

  it('hides the row counts when the file had no rows', async () => {
    await importWithResult({ totalRows: 0, imported: 0, skippedDuplicates: 0 });

    expect(
      element.querySelector('.upload-dialog__result .upload-dialog__dropzone-hint'),
    ).toBeNull();
  });

  it('marks the result as empty when nothing was imported', async () => {
    await importWithResult({ totalRows: 42, imported: 0, skippedDuplicates: 42 });

    expect(element.querySelector('.upload-dialog__result--empty')).not.toBeNull();
  });

  it('does not close while the import is still in progress', async () => {
    serviceStub.importCsv.mockReturnValue(new Subject<ImportResult>());
    selectFile(new File(['a'], 'a.csv'));
    await fixture.whenStable();

    submitButton().click();

    expect(dialogRefStub.close).not.toHaveBeenCalled();
  });

  it('shows an error and stays open when the import fails', async () => {
    serviceStub.importCsv.mockReturnValue(throwError(() => new Error('400')));
    selectFile(new File(['a'], 'a.csv'));
    await fixture.whenStable();

    submitButton().click();
    await fixture.whenStable();

    expect(dialogRefStub.close).not.toHaveBeenCalled();
    expect(element.querySelector('[role="alert"]')?.textContent).toContain(
      'The CSV file could not be imported.',
    );
  });

  it('clears the error when a new file is selected', async () => {
    serviceStub.importCsv.mockReturnValue(throwError(() => new Error('400')));
    selectFile(new File(['a'], 'a.csv'));
    component.onSubmit();
    expect(component.errorMessage()).not.toBeNull();

    selectFile(new File(['b'], 'b.csv'));
    await fixture.whenStable();

    expect(component.errorMessage()).toBeNull();
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });

  it('does nothing on submit without a file', () => {
    component.onSubmit();
    expect(serviceStub.importCsv).not.toHaveBeenCalled();
  });

  it('closes via the close and cancel buttons', () => {
    element.querySelector<HTMLButtonElement>('.upload-dialog__close')!.click();
    element.querySelector<HTMLButtonElement>('.upload-dialog__cancel')!.click();
    expect(dialogRefStub.close).toHaveBeenCalledTimes(2);
  });

  it.each([
    [0, '0 B'],
    [1023, '1023 B'],
    [1024, '1.0 KB'],
    [1536, '1.5 KB'],
    [1024 * 1024, '1.0 MB'],
    [5.25 * 1024 * 1024, '5.3 MB'],
  ])('formatFileSize(%i) returns %s', (bytes, expected) => {
    expect(component.formatFileSize(bytes)).toBe(expected);
  });
});
