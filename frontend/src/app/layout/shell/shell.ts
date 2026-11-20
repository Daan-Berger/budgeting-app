import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import {
  LucideDynamicIcon,
  LucideIcon,
  LucideLayoutDashboard,
  LucideList,
  LucideSettings2,
} from '@lucide/angular';

export interface NavItem {
  label: string;
  path: string;
  icon: LucideIcon;
}

/** Placeholder until a login/account issue exists. */
export interface AccountSummary {
  initials: string;
  name: string;
  bankAccount: string;
}

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, LucideDynamicIcon],
  selector: 'app-shell',
  styleUrl: './shell.css',
  templateUrl: './shell.html',
})
export class Shell {
  protected readonly navItems: readonly NavItem[] = [
    { label: 'Dashboard', path: '/dashboard', icon: LucideLayoutDashboard },
    { label: 'Transactions', path: '/transactions', icon: LucideList },
    { label: 'Settings', path: '/settings', icon: LucideSettings2 },
  ];

  protected readonly account: AccountSummary = {
    initials: 'DB',
    name: 'Personal',
    bankAccount: 'Belfius ···· 4417',
  };
}
