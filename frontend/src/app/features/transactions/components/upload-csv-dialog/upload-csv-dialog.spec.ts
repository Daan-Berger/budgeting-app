import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { of, Subject, throwError } from 'rxjs';
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

  it('imports the selected file and closes on success', async () => {
    const file = new File(['a'], 'a.csv');
    serviceStub.importCsv.mockReturnValue(of(undefined));
    selectFile(file);
    await fixture.whenStable();

    submitButton().click();

    expect(serviceStub.importCsv).toHaveBeenCalledWith(file);
    expect(dialogRefStub.close).toHaveBeenCalled();
  });

  it('does not close while the import is still in progress', async () => {
    serviceStub.importCsv.mockReturnValue(new Subject<void>());
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
