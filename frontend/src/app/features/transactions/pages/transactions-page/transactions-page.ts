import { Component } from '@angular/core';
import { TransactionList } from '../../components/transaction-list/transaction-list';

@Component({
  imports: [TransactionList],
  selector: 'app-transactions-page',
  styleUrl: './transactions-page.css',
  templateUrl: './transactions-page.html',
})
export class TransactionsPage { }
