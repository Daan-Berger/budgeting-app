import { Component, inject } from '@angular/core';
import { TransactionList } from '../../components/transaction-list/transaction-list';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { UploadCsvDialog } from '../../components/upload-csv-dialog/upload-csv-dialog';

@Component({
  imports: [TransactionList, MatDialogModule, MatButtonModule],
  selector: 'app-transactions-page',
  styleUrl: './transactions-page.css',
  templateUrl: './transactions-page.html',
})
export class TransactionsPage {
  readonly dialog = inject(MatDialog)

  openUploadDialog(): void {
    this.dialog.open(UploadCsvDialog, {
      width: 'min(32rem, calc(100vw - 2rem))',
      maxWidth: 'none',
      panelClass: 'upload-dialog-panel',
      backdropClass: 'upload-dialog-backdrop',
    })
  }
}
