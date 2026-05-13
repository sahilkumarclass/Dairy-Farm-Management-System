---
name: Dairy Management Design System
colors:
  surface: '#f8f9fb'
  surface-dim: '#d9dadc'
  surface-bright: '#f8f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f6'
  surface-container: '#edeef0'
  surface-container-high: '#e7e8ea'
  surface-container-highest: '#e1e2e4'
  on-surface: '#191c1e'
  on-surface-variant: '#444651'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f3'
  outline: '#757682'
  outline-variant: '#c5c5d3'
  surface-tint: '#4059aa'
  primary: '#00236f'
  on-primary: '#ffffff'
  primary-container: '#1e3a8a'
  on-primary-container: '#90a8ff'
  inverse-primary: '#b6c4ff'
  secondary: '#006e2f'
  on-secondary: '#ffffff'
  secondary-container: '#6bff8f'
  on-secondary-container: '#007432'
  tertiary: '#4b1c00'
  on-tertiary: '#ffffff'
  tertiary-container: '#6e2c00'
  on-tertiary-container: '#f39461'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b6c4ff'
  on-primary-fixed: '#00164e'
  on-primary-fixed-variant: '#264191'
  secondary-fixed: '#6bff8f'
  secondary-fixed-dim: '#4ae176'
  on-secondary-fixed: '#002109'
  on-secondary-fixed-variant: '#005321'
  tertiary-fixed: '#ffdbcb'
  tertiary-fixed-dim: '#ffb691'
  on-tertiary-fixed: '#341100'
  on-tertiary-fixed-variant: '#773205'
  background: '#f8f9fb'
  on-background: '#191c1e'
  surface-variant: '#e1e2e4'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Noto Sans Devanagari
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 20px
  label-sm:
    fontFamily: Noto Sans Devanagari
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  data-metric:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 48px
---

## Brand & Style

This design system is built for the rigorous environment of dairy farming—balancing high-utility professional tools with an aesthetic that evokes freshness and reliability. The visual language follows a **Corporate/Modern** style, emphasizing clarity and structural integrity. 

The personality is dependable and efficient, prioritizing high legibility for outdoor use and "glanceable" data metrics for quick decision-making. The UI avoids unnecessary decorative elements, opting instead for a functional, card-based architecture that feels organized and trustworthy. The emotional response is one of calm control over complex agricultural data.

## Colors

The "Fresh Dairy" palette uses **Deep Blue (#1E3A8A)** to establish professional authority and trust, while **Grassy Green (#22C55E)** provides a vibrant connection to the farm environment and growth. 

- **Primary:** Reserved for critical actions, navigation, and brand identification.
- **Secondary:** Used for success states, health indicators, and "active" growth metrics.
- **Backgrounds:** Light mode uses a combination of pure White (#FFFFFF) for cards and Soft Gray (#F3F4F6) for page canvases to create distinct layering.
- **Dark Mode:** Transitions to a High-Contrast Dark Blue/Black (#0F172A) to reduce glare during early morning or late-night milking shifts, using vibrant saturated versions of the primary and secondary colors to maintain accessibility.

## Typography

The system utilizes **Inter** for all English technical data and interface elements due to its exceptional legibility and neutral tone. To support the diverse workforce in dairy management, **Noto Sans Devanagari** is integrated seamlessly for Hindi support, maintaining a consistent weight and x-height with the Latin characters.

Special emphasis is placed on the **Data Metric** style—a bold, condensed font treatment used for critical numbers like milk yield and herd count, ensuring they are readable from a distance. For mobile devices, headlines scale down by 15% to preserve screen real estate while maintaining touch-target spacing.

## Layout & Spacing

This design system employs a **Fluid Grid** model based on a 4px baseline shift. 

- **Mobile:** A 4-column layout with 16px margins. Primary interaction points are placed within the "thumb zone" at the bottom two-thirds of the screen.
- **Desktop/Tablet:** A 12-column grid with 24px gutters. Dashboard cards reflow into multi-column layouts to maximize data density without clutter.
- **Rhythm:** Generous 16px (md) and 24px (lg) padding is used within cards to ensure that even complex data sets feel airy and "fresh." Large 48px touch targets are mandated for all interactive elements to accommodate use in active farm environments.

## Elevation & Depth

To maintain a lightweight and professional feel, this design system uses **Low-Contrast Outlines** and **Tonal Layers** rather than heavy shadows. 

- **Level 0 (Background):** Soft Gray (#F3F4F6) or Dark Slate (#0F172A).
- **Level 1 (Cards/Sheets):** Pure White or Surface Dark (#1E293B). These use a 1px border (#E2E8F0 in light mode) to define boundaries.
- **Level 2 (Interactive):** Elements that require focus (like active modals or dropdowns) utilize a subtle, 8% opacity shadow with a 4px blur, tinted with the Primary Deep Blue to maintain color harmony.

This "Flat-Plus" approach ensures the UI remains fast and works well even on lower-end mobile hardware common in agricultural settings.

## Shapes

The shape language is **Rounded**, utilizing a 0.5rem (8px) base radius. This softens the "industrial" nature of farm data, making the app feel more approachable and modern.

- **Standard Elements:** 8px (Buttons, Input Fields, Small Cards).
- **Large Containers:** 16px (Dashboard Overview Cards, Bottom Sheets).
- **Status Tags:** Pill-shaped (Full round) to distinguish them instantly from actionable buttons.

## Components

- **Buttons:** Large (min-height 48px), high-contrast blocks. Primary buttons use Deep Blue with white text; secondary buttons use a 2px Deep Blue border.
- **Data Cards:** The core of the dashboard. Each card must feature a clear Title (Headline-sm) and a primary metric (Data-metric).
- **List Items:** High-density rows with 16px vertical padding, featuring a "chevron" or "action" icon to indicate drill-down capability.
- **Inputs:** Underlined or lightly boxed with 16px padding. Active states are indicated by a 2px Grassy Green border to signal "Fresh/Ready" status.
- **Dairy Specifics:**
    - **Health Chips:** Small pill-shaped badges (e.g., "In Heat," "Sick," "Dry") using high-contrast semantic colors.
    - **Yield Charts:** Simplified line graphs using Grassy Green for "current" and Deep Blue for "historical" data.
    - **Quick-Action FAB:** A Floating Action Button for "Add Log" or "Quick Scan" (RFID/QR) to facilitate rapid data entry on the move.