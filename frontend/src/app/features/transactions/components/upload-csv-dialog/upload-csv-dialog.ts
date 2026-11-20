import { Component, computed, inject, signal } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { finalize } from 'rxjs';
import { ImportResult } from '../../models/import-result.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  imports: [],
  selector: 'app-upload-csv-dialog',
  styleUrl: './upload-csv-dialog.css',
  templateUrl: './upload-csv-dialog.html',
})
export class UploadCsvDialog {
  private readonly dialogRef = inject(MatDialogRef<UploadCsvDialog>)
  private readonly transactionService = inject(TransactionService)

  readonly selectedFile = signal<File | null>(null)
  readonly errorMessage = signal<string | null>(null)
  readonly result = signal<ImportResult | null>(null)
  readonly resultMessage = computed(() => {
    const result = this.result()
    return result ? describeImportResult(result) : null
  })
  readonly resultDetails = computed(() => {
    const result = this.result()
    if (!result || result.totalRows === 0) {
      return null
    }
    return `${result.totalRows} ${pluralize(result.totalRows, 'row')} in file · ${result.imported} new · ${result.skippedDuplicates} already imported`
  })
  private readonly pending = signal(false)

  close(): void {
    this.dialogRef.close()
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement
    this.selectedFile.set(input.files?.[0] ?? null)
    this.errorMessage.set(null)
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) {
      return `${bytes} B`
    }
    if (bytes < 1024 * 1024) {
      return `${(bytes / 1024).toFixed(1)} KB`
    }
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  }

  onSubmit(): void {
    const file = this.selectedFile()
    if (!file || this.pending()) {
      return
    }

    this.pending.set(true)
    this.transactionService.importCsv(file).pipe(
      finalize(() => this.pending.set(false)),
    ).subscribe({
      next: (result) => this.result.set(result),
      error: () => {
        this.errorMessage.set('The CSV file could not be imported.')
      },
    })
  }
}

function describeImportResult({ totalRows, imported, skippedDuplicates }: ImportResult): string {
  if (totalRows === 0) {
    return 'The file did not contain any transactions.'
  }
  if (imported === 0) {
    return 'No new transactions: this file was already imported.'
  }
  const importedText = `Imported ${imported} new ${pluralize(imported, 'transaction')}.`
  if (skippedDuplicates === 0) {
    return importedText
  }
  return `${importedText} ${skippedDuplicates} ${pluralize(skippedDuplicates, 'was', 'were')} already imported.`
}

function pluralize(count: number, singular: string, plural = `${singular}s`): string {
  return count === 1 ? singular : plural
}
