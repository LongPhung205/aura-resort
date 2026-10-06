import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
  isAuthPage = false;

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isAuthPage = this.router.url.includes('/login') || 
                          this.router.url.includes('/register') || 
                          this.router.url.includes('/forgot-password');
      }
    });
    this.isAuthPage = this.router.url.includes('/login') || 
                      this.router.url.includes('/register') || 
                      this.router.url.includes('/forgot-password');
  }
}
