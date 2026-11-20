import { Routes } from '@angular/router';
import { Shell } from './layout/shell/shell';

export const routes: Routes = [
  {
    path: '',
    component: Shell,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        title: 'Dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard-page/dashboard-page').then(
            (m) => m.DashboardPage,
          ),
      },
      {
        path: 'transactions',
        title: 'Transactions',
        loadComponent: () =>
          import('./features/transactions/pages/transactions-page/transactions-page').then(
            (m) => m.TransactionsPage,
          ),
      },
      {
        path: 'transactions/:id',
        title: 'Transaction',
        loadComponent: () =>
          import('./features/transactions/pages/transaction-detail-page/transaction-detail-page').then(
            (m) => m.TransactionDetailPage,
          ),
      },
      {
        path: 'settings',
        title: 'Settings',
        loadComponent: () =>
          import('./features/settings/pages/settings-page/settings-page').then(
            (m) => m.SettingsPage,
          ),
      },
    ],
  },
];
