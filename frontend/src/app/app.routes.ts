import { Routes } from '@angular/router';

export const routes: Routes = [
    // transactions
    {
        path: 'transactions',
        title: 'All Transactions',
        loadComponent: () =>
            import('./features/transactions/pages/transactions-page/transactions-page')
                .then(m => m.TransactionsPage)
    }
];
