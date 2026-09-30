---
name: frontend-workflow
description: Use when creating or modifying frontend components, layouts, or styles in the Booking Hotel Angular application
---

# Frontend Workflow

## Overview
This skill outlines the mandatory design guidelines, aesthetic standards, and technical workflow for developing the frontend of the Booking Hotel application.

## When to Use
- When creating new Angular components.
- When modifying existing layouts (Login, Register, Dashboard).
- When styling elements or fixing UI bugs.
- When integrating the frontend with backend APIs.

## Core Design Aesthetics (Luxury & Modern)
1. **Single-Screen Forms**: Auth forms (login, register) MUST fit entirely within a single screen without vertical scrolling. Use `flex-1`, `min-h-0`, and `overflow-hidden` appropriately on containers.
2. **Typography**: Always use the modern font stack defined in `tailwind.config.js`: `Avenir Next, BlinkMacSystemFont, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif`.
3. **Native HTML + Tailwind**: Prefer using native HTML `<input>`, `<button>` tags styled with Tailwind CSS instead of complex legacy third-party UI components (e.g., Taiga UI legacy forms). This prevents rendering conflicts and ensures predictable styling.
4. **Spacing & Alignment**: Ensure elements are not cramped. Use generous paddings (`p-6`, `p-8`) and margins (`mb-6`, `mt-4`) to create a "breathing room" typical of premium, luxury interfaces.

## Quick Reference / Standard Commands
- **Create Component**: `npx ng generate component <path>`
- **Create Service**: `npx ng generate service <path>`
- **Format Code**: `npx prettier --write <filepath>` (Always run this after editing `.html` or `.ts` files to maintain clean code structure).

## Technical Implementation Workflow
1. **Understand Requirements**: Verify if the layout requires a 2-column split (e.g., Image on left, Form on right) or a centered modal.
2. **Apply Tailwind Classes**: Construct the UI using Tailwind. Avoid writing custom CSS in `.scss` files unless absolutely necessary for complex animations.
3. **Verify HTML Syntax**: Angular strictly enforces HTML syntax. Ensure all `<div>`, `<form>`, and `<label>` tags are properly closed to avoid compiler error `NG5002`.
4. **Compile & Check**: After editing, always verify that the `ng serve` background task is compiling successfully and is not throwing errors.
