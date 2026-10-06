import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoreValueItem } from '../../models/home.models';
import { HomeMockService } from '../../services/home-mock.service';

@Component({
  selector: 'app-core-values',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './core-values.component.html',
  styleUrls: ['./core-values.component.scss'],
})
export class CoreValuesComponent implements OnInit {
  coreValues: CoreValueItem[] = [];

  constructor(private homeMockService: HomeMockService) {}

  ngOnInit(): void {
    this.coreValues = this.homeMockService.getCoreValues();
  }
}
