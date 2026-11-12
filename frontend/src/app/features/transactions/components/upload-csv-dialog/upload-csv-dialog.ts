import { Component, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  imports: [],
  selector: 'app-upload-csv-dialog',
  styleUrl: './upload-csv-dialog.css',
  templateUrl: './upload-csv-dialog.html',
})
export class UploadCsvDialog {
  private readonly dialogRef = inject(MatDialogRef<UploadCsvDialog>)

  close(): void {
    this.dialogRef.close()
  }
}
