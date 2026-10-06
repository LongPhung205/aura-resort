export type ResortCategory = 'all' | 'beach' | 'bay' | 'mountain';

export interface ResortItem {
  id: string;
  name: string;
  category: 'beach' | 'bay' | 'mountain';
  location: string;
  imageUrl: string;
  badges: string[];
  features: string[];
  pricePerNight: number;
  priceDisplay: string;
  rating: number;
  ratingCount: number;
}

export interface OfferPackage {
  id: string;
  badge: string;
  title: string;
  description: string;
  imageUrl: string;
  benefits?: string[];
  priceDisplay?: string;
  buttonText: string;
  featured?: boolean;
}

export interface EliteBenefit {
  id: string;
  title: string;
  description: string;
  icon: string;
  imageUrl?: string;
  tag?: string;
}

export interface CoreValueItem {
  id: string;
  title: string;
  description: string;
  icon: string;
  imageUrl?: string;
  subtitle?: string;
  number?: string;
}

export interface TestimonialItem {
  id: string;
  quote: string;
  authorName: string;
  authorTitle: string;
  avatarUrl: string;
  stars: number;
}

export interface CustomerCategoryItem {
  id: string;
  name: string;
  subtitle: string;
  badge: string;
  imageUrl: string;
  icon: string;
}

export interface CustomerCategoryBenefit {
  id: string;
  tier: string;
  categoryName: string;
  subtitle: string;
  tag: string;
  tagColor: string;
  imageUrl: string;
  icon: string;
  description: string;
  privileges: string[];
  ctaText: string;
}

export interface StatItem {
  value: string;
  description: string;
}

export interface BookingSearchParams {
  destination: string;
  checkInDate: string;
  checkOutDate: string;
  adults: number;
  children: number;
  rooms: number;
  promoCode: string;
  isBusinessTrip: boolean;
  activeTab: 'resorts' | 'villas' | 'packages';
}
