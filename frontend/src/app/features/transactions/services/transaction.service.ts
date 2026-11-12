import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Transaction } from "../models/transaction.model";

@Injectable({ providedIn: 'root' })
export class TransactionService {
    private http = inject(HttpClient)
    private baseUrl = '/api/transaction'

    getTransactions(): Observable<Transaction[]> {
        return this.http.get<Transaction[]>(`${this.baseUrl}`)
    }
}