import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { routes } from './app.routes';

describe('app routes', () => {
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes), provideHttpClient(), provideHttpClientTesting()],
    });
    harness = await RouterTestingHarness.create();
  });

  it('redirects the root url to the dashboard', async () => {
    await harness.navigateByUrl('/');

    expect(TestBed.inject(Router).url).toBe('/dashboard');
  });

  it.each([
    ['/dashboard', 'app-dashboard-page'],
    ['/transactions', 'app-transactions-page'],
    ['/transactions/123', 'app-transaction-detail-page'],
    ['/settings', 'app-settings-page'],
  ])('renders %s inside the shell', async (url, selector) => {
    await harness.navigateByUrl(url);

    const shell = harness.fixture.nativeElement.querySelector('app-shell') as HTMLElement;
    expect(shell.querySelector(`main ${selector}`)).not.toBeNull();
  });
});
