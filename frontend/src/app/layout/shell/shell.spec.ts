import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { Shell } from './shell';

@Component({ template: '<p class="stub-page">page</p>' })
class StubPage {}

describe('Shell', () => {
  let harness: RouterTestingHarness;
  let element: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: '',
            component: Shell,
            children: [
              { path: 'dashboard', component: StubPage },
              { path: 'transactions', component: StubPage },
              { path: 'transactions/:id', component: StubPage },
              { path: 'settings', component: StubPage },
            ],
          },
        ]),
      ],
    });
    harness = await RouterTestingHarness.create();
  });

  async function navigate(url: string): Promise<void> {
    await harness.navigateByUrl(url);
    element = harness.fixture.nativeElement.querySelector('app-shell');
  }

  function navLinks(): HTMLAnchorElement[] {
    return Array.from(element.querySelectorAll<HTMLAnchorElement>('nav a'));
  }

  function currentLinks(): HTMLAnchorElement[] {
    return navLinks().filter((link) => link.getAttribute('aria-current') === 'page');
  }

  it('renders the three nav items in order with their routes', async () => {
    await navigate('/dashboard');

    const labels = navLinks().map((link) =>
      link.querySelector('.shell__nav-label')?.textContent?.trim(),
    );
    expect(labels).toEqual(['Dashboard', 'Transactions', 'Settings']);
    expect(navLinks().map((link) => link.getAttribute('href'))).toEqual([
      '/dashboard',
      '/transactions',
      '/settings',
    ]);
  });

  it('renders the routed page inside the content area', async () => {
    await navigate('/settings');

    expect(element.querySelector('main .stub-page')).not.toBeNull();
  });

  it.each([
    ['/dashboard', 'Dashboard'],
    ['/transactions', 'Transactions'],
    ['/transactions/123', 'Transactions'],
    ['/settings', 'Settings'],
  ])('marks only the matching nav item as current on %s', async (url, label) => {
    await navigate(url);

    const current = currentLinks();
    expect(current).toHaveLength(1);
    expect(current[0].textContent).toContain(label);
    expect(current[0].classList).toContain('shell__nav-item--active');
  });

  it('shows the placeholder account block', async () => {
    await navigate('/dashboard');

    const account = element.querySelector('.shell__account')!;
    expect(account.textContent).toContain('Personal');
    expect(account.textContent).toContain('Belfius ···· 4417');
  });
});
