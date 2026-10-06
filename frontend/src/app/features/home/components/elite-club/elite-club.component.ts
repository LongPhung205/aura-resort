import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EliteBenefit } from '../../models/home.models';
import { HomeMockService } from '../../services/home-mock.service';

@Component({
  selector: 'app-elite-club',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './elite-club.component.html',
  styleUrls: ['./elite-club.component.scss'],
})
export class EliteClubComponent implements OnInit {
  benefits: EliteBenefit[] = [];

  constructor(private homeMockService: HomeMockService) {}

  ngOnInit(): void {
    this.benefits = this.homeMockService.getEliteBenefits();
  }
}
