# Frontend UI Design System (Booking Hotel)

## Overview
This rule strictly enforces the UI aesthetics and technology choices for the Booking Hotel frontend application. Any UI changes, new components, or styling MUST adhere to this design system.

## 1. Core Technology Stack
- **Framework**: Angular 18 (Standalone components preferred)
- **UI Component Library**: [Taiga UI](https://taiga-ui.dev/)
- **Utility CSS**: [Tailwind CSS](https://tailwindcss.com/)
- **Pre-processor**: SCSS

## 2. Design Aesthetic: "Modern & Vibrant"
The app must convey a sense of modern technology, high trust, and vibrant energy to encourage bookings.

### Color Palette
- **Primary (Trust & Brand)**: Deep Blue / Navy (`#0f172a` or `#1e3a8a`). Used for navbar, headings, and primary layout structures.
- **Accent (Call to Action)**: Vibrant Coral / Orange (`#f97316` or `#ff7a59`). Strictly reserved for primary actions (Book Now, Search buttons, highlighted badges).
- **Backgrounds**: Pure White (`#ffffff`) for cards, and Off-White/Light Gray (`#f8fafc` or `#f1f5f9`) for app backgrounds to create depth.
- **Text**: Dark Slate (`#334155`) for body text, not pure black, to reduce eye strain.

### Typography
- **Primary Font**: `Poppins` (Google Fonts). Must be used globally.
- Headings should be bold (`font-bold` or `font-semibold`) and clearly separated from body text via sizing and margin.

## 3. Technology Responsibilities

### When to use Taiga UI
Use Taiga UI for all interactive or complex components to ensure accessibility and robust UX:
- Inputs (Text, Password, Number)
- Date and DateRange Pickers (Crucial for hotel booking)
- Selects and Dropdowns
- Dialogs (Modals)
- Alerts/Notifications
- Tabs & Accordions

### When to use Tailwind CSS
Use Tailwind for layout, spacing, and micro-interactions:
- Flexbox / Grid layouts
- Margins and Paddings
- Responsive design (e.g., `md:flex-row`, `lg:grid-cols-3`)
- Custom colors that are not supported out-of-the-box by Taiga UI.
- Micro-animations (see below).

## 4. Micro-animations & Interaction Design
The interface must feel alive and premium.

- **Cards (Hotel/Room items)**:
  - Default state: `rounded-2xl bg-white shadow-sm transition-all duration-300`
  - Hover state: `hover:-translate-y-1 hover:shadow-md`
- **Buttons**:
  - Primary buttons must have a clear hover state (e.g., slightly lighter Coral) and use Taiga's ripple effect.
- **Transitions**: Any state change (color, background, opacity, transform) MUST be accompanied by a Tailwind transition (`transition-colors duration-200`, etc.).

## 5. Coding Standards
- Do NOT use inline styles (`style="..."`). Always use Tailwind classes or SCSS.
- Keep Tailwind class lists organized.
- Override Taiga UI variables in `styles.scss` (or a dedicated `theme.scss`) using CSS variables to match the Color Palette above, ensuring Taiga components look like they belong to our Custom Design System.
