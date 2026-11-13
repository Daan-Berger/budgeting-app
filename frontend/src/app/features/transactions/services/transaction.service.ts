import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Transaction } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = '/api/transaction';

    private readonly _transactions = signal<Transaction[]>([]);
    private readonly _error = signal<string | null>(null);

    readonly transactions = this._transactions.asReadonly();
    readonly error = this._error.asReadonly();

    getTransactions(): Observable<Transaction[]> {
        return this.http.get<Transaction[]>(this.baseUrl);
    }

    refresh(): void {
        this._error.set(null);
        this.getTransactions().subscribe({
            next: (data) => this._transactions.set(data),
            error: (err) => {
                console.error('Failed to fetch transactions:', err);
                this._error.set(err.message);
            },
        });
    }

    importCsv(csv: File): Observable<void> {
        const formData = new FormData();
        formData.append('file', csv, csv.name);

        return this.http
            .post<void>(`${this.baseUrl}/import`, formData)
            .pipe(tap(() => this.refresh()));
    }
}
