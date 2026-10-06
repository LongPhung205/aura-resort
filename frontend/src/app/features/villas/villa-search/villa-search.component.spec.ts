import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VillaSearchComponent } from './villa-search.component';

describe('VillaSearchComponent', () => {
  let component: VillaSearchComponent;
  let fixture: ComponentFixture<VillaSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VillaSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VillaSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
