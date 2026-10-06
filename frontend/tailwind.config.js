/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'brand-primary': '#0f172a',
        'brand-accent': '#f97316',
        'brand-gold': '#C5A880',
        'brand-gold-dark': '#8B6E4E',
        'brand-gold-light': '#F2EBE1',
        'brand-gold-accent': '#D4AF37',
        'brand-dark': '#0B1320',
        'brand-darker': '#060B11',
        'brand-card': '#111B2B',
        'brand-surface': '#F9F9F8',
        'brand-emerald': '#059669',
        'brand-emerald-light': '#D1FAE5',
        'brand-ocean': '#0284C7',
        'brand-ocean-light': '#E0F2FE',
        'brand-sunset': '#EA580C',
        'brand-sunset-light': '#FFEDD5',
        'brand-royal': '#4F46E5',
        'brand-champagne': '#FAF6EE',
      },
      fontFamily: {
        'sans': ['"Roboto"', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'sans-serif'],
        'roboto': ['"Roboto"', 'sans-serif'],
        'serif': ['"Roboto"', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
