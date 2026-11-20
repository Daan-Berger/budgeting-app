import { Component, inject, signal } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { finalize } from 'rxjs';
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
      next: () => this.close(),
      error: () => {
        this.errorMessage.set('The CSV file could not be imported.')
      },
    })
  }
}
