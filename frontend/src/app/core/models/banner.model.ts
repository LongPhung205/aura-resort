export interface HomeBanner {
  id: number;
  title: string;
  subtitle?: string;
  description?: string;
  imageUrl: string;
  mobileImageUrl?: string;
  ctaText?: string;
  ctaLink?: string;
  placement?: string;
  displayOrder?: number;
  isActive?: boolean;
  badgesJson?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface HomeBannerPayload {
  title: string;
  subtitle?: string;
  description?: string;
  imageUrl: string;
  mobileImageUrl?: string;
  ctaText?: string;
  ctaLink?: string;
  placement?: string;
  displayOrder?: number;
  isActive?: boolean;
  badgesJson?: string;
}
