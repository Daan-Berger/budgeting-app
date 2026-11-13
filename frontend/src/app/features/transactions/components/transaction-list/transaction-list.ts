import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { TransactionService } from '../../services/transaction.service';

@Component({
  imports: [CurrencyPipe, DatePipe],
  selector: 'app-transaction-list',
  styleUrl: './transaction-list.css',
  templateUrl: './transaction-list.html',
})
export class TransactionList implements OnInit {
  private readonly transactionService = inject(TransactionService);

  readonly transactions = this.transactionService.transactions;
  readonly errorMessage = this.transactionService.error;

  ngOnInit(): void {
    this.transactionService.refresh();
  }
}
