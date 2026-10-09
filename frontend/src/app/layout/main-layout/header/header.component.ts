import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { ZoneService } from '../../../core/services/zone.service';
import { TokenService } from '../../../core/services/token.service';
import { AuthService } from '../../../core/services/auth.service';

export interface CurrentUserSession {
  email?: string;
  fullName?: string;
  role?: string;
  avatarUrl?: string;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  isMobileMenuOpen = false;
  isVillaDropdownOpen = false;
  selectedCurrency = 'VND';
  selectedLang = 'VN';
  zones: any[] = [];

  // User session
  isLoggedIn = false;
  currentUser: CurrentUserSession | null = null;
  vipUserName = 'Thành Viên VIP';
  vipTier = 'Aura Black Elite';

  // Auth page detection
  isAuthPage = false;

  constructor(
    private router: Router,
    private zoneService: ZoneService,
    private tokenService: TokenService,
    private authService: AuthService
  ) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isAuthPage = this.router.url.includes('/login') || 
                          this.router.url.includes('/register') || 
                          this.router.url.includes('/forgot-password');
      }
    });
  }

  ngOnInit(): void {
    this.checkLoginStatus();
    this.isAuthPage = this.router.url.includes('/login') || 
                      this.router.url.includes('/register') || 
                      this.router.url.includes('/forgot-password');
    this.loadZones();
  }

  loadZones(): void {
    this.zoneService.getActiveZones().subscribe({
      next: (zones) => {
        this.zones = zones || [];
      },
      error: (err) => {
        console.warn('Could not load dynamic zones for header', err);
      }
    });
  }

  checkLoginStatus(): void {
    const raw = localStorage.getItem('user');
    if (raw) {
      try {
        this.currentUser = JSON.parse(raw);
        this.isLoggedIn = true;
        this.vipUserName = this.currentUser?.fullName || this.currentUser?.email || 'Thành Viên';
      } catch {
        this.currentUser = null;
        this.isLoggedIn = false;
      }
    } else {
      this.isLoggedIn = false;
      this.currentUser = null;
    }
  }

  logout(): void {
    const refreshToken = this.tokenService.getRefreshToken() || undefined;
    this.authService.logout(refreshToken).subscribe({
      next: () => {},
      error: () => {}
    });
    this.tokenService.clearAll();
    this.currentUser = null;
    this.isLoggedIn = false;
    window.location.href = '/login';
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  toggleVillaDropdown(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.isVillaDropdownOpen = !this.isVillaDropdownOpen;
  }

  closeVillaDropdown(): void {
    this.isVillaDropdownOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.villa-dropdown-container')) {
      this.isVillaDropdownOpen = false;
    }
  }
}
