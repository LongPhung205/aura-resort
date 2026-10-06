import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./layout/main-layout/main-layout.component').then((m) => m.MainLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home.component').then((m) => m.HomeComponent),
      },
      { path: 'home', redirectTo: '', pathMatch: 'full' },
      {
        path: 'villas/search',
        loadComponent: () =>
          import('./features/villas/villa-search/villa-search.component').then(
            (m) => m.VillaSearchComponent
          ),
      },
      {
        path: 'villas/zone/:slug',
        loadComponent: () =>
          import('./features/villas/zone-detail/zone-detail.component').then(
            (m) => m.ZoneDetailComponent
          ),
      },
      {
        path: 'villas/detail/:id',
        loadComponent: () =>
          import('./features/villas/villa-detail/villa-detail.component').then(
            (m) => m.VillaDetailComponent
          ),
      },
      {
        path: 'checkout',
        loadComponent: () =>
          import('./features/checkout/checkout.component').then(
            (m) => m.CheckoutComponent
          ),
        canActivate: [authGuard] // Require login for checkout
      },
      {
        path: 'booking-success',
        loadComponent: () =>
          import('./features/checkout/booking-success/booking-success.component').then(
            (m) => m.BookingSuccessComponent
          ),
        canActivate: [authGuard]
      },
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'auth/login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./features/auth/forgot-password/forgot-password.component').then(
            (m) => m.ForgotPasswordComponent
          ),
      },
      {
        path: 'account',
        loadComponent: () =>
          import('./features/account/account.component').then(
            (m) => m.AccountComponent
          ),
        canActivate: [authGuard],
      },
      {
        path: 'bookings',
        redirectTo: 'account',
        pathMatch: 'full',
      },
      {
        path: 'my-bookings',
        redirectTo: 'account',
        pathMatch: 'full',
      },
      {
        path: 'my-reviews',
        redirectTo: 'account',
        pathMatch: 'full',
      },
      {
        path: 'profile',
        redirectTo: 'account',
        pathMatch: 'full',
      },
    ],
  },
  {
    // Trang kết quả thanh toán MoMo
    path: 'payment/momo-result',
    loadComponent: () =>
      import('./features/payment/momo-result/momo-result.component').then(
        (m) => m.MomoResultComponent
      ),
  },
  {
    path: 'housekeeping',
    loadComponent: () =>
      import('./features/housekeeping/housekeeping-portal.component').then(
        (m) => m.HousekeepingPortalComponent
      ),
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./admin/layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
    canActivate: [authGuard, adminGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./admin/dashboard/dashboard.component').then((m) => m.AdminDashboardComponent),
      },
      {
        path: 'bookings',
        loadComponent: () =>
          import('./admin/booking-management/booking-management.component').then(
            (m) => m.BookingManagementComponent
          ),
      },
      {
        path: 'villas',
        loadComponent: () =>
          import('./admin/room-management/room-management.component').then(
            (m) => m.RoomManagementComponent
          ),
      },
      {
        path: 'villas/operations',
        redirectTo: 'villas',
        pathMatch: 'full',
      },
      {
        path: 'rooms',
        redirectTo: 'villas',
        pathMatch: 'full',
      },
      {
        path: 'services',
        loadComponent: () =>
          import('./admin/service-management/service-management.component').then(
            (m) => m.ServiceManagementComponent
          ),
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./admin/inventory-management/inventory-management.component').then(
            (m) => m.InventoryManagementComponent
          ),
      },
      {
        path: 'promotions',
        loadComponent: () =>
          import('./admin/promotion-management/promotion-management.component').then(
            (m) => m.PromotionManagementComponent
          ),
      },
      {
        path: 'payments',
        loadComponent: () =>
          import('./admin/payment-management/payment-management.component').then(
            (m) => m.PaymentManagementComponent
          ),
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./admin/report-management/report-management.component').then(
            (m) => m.ReportManagementComponent
          ),
      },
      {
        path: 'reviews',
        loadComponent: () =>
          import('./admin/review-management/review-management.component').then(
            (m) => m.ReviewManagementComponent
          ),
      },
      {
        path: 'staff',
        loadComponent: () =>
          import('./admin/staff-management/staff-management.component').then(
            (m) => m.StaffManagementComponent
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./admin/user-management/user-management.component').then(
            (m) => m.UserManagementComponent
          ),
      },
      {
        path: 'banners',
        loadComponent: () =>
          import('./admin/banner-management/banner-management.component').then(
            (m) => m.BannerManagementComponent
          ),
      },
      {
        path: 'collections',
        loadComponent: () =>
          import('./admin/collection-management/collection-management.component').then(
            (m) => m.CollectionManagementComponent
          ),
      },
      {
        path: 'zones',
        loadComponent: () =>
          import('./admin/zone-management/zone-management.component').then(
            (m) => m.ZoneManagementComponent
          ),
      },
      {
        path: 'housekeeping',
        loadComponent: () =>
          import('./admin/housekeeping-management/housekeeping-management.component').then(
            (m) => m.HousekeepingManagementComponent
          ),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
