import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UploadCsvDialog } from './upload-csv-dialog';

describe('UploadCsvDialog', () => {
  let component: UploadCsvDialog;
  let fixture: ComponentFixture<UploadCsvDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadCsvDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(UploadCsvDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
