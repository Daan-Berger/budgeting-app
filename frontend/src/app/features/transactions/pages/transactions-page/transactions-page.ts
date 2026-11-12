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
    let dialogRef = this.dialog.open(UploadCsvDialog)
  }
}
