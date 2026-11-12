import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';

@Component({
  imports: [CurrencyPipe, DatePipe],
  selector: 'app-transaction-list',
  styleUrl: './transaction-list.css',
  templateUrl: './transaction-list.html',
})
export class TransactionList implements OnInit {
  private readonly transactionService = inject(TransactionService)

  readonly transactions = signal<Transaction[]>([])
  readonly errorMessage = signal<string | null>(null)

  ngOnInit(): void {
    this.loadTransactions()
  }

  private loadTransactions(): void {
    this.transactionService.getTransactions().subscribe({
      next: (data: Transaction[]) => {
        this.transactions.set(data)
      },
      error: (err) => {
        console.error('Failed to fetch transactions:', err);
        this.errorMessage.set(err.message)
      }
    })
  }
}
