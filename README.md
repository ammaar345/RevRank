# TripRank — Full Redesign & Product Overhaul
## Handover Package for Claude Code

---

## What This Package Contains

| File | Purpose |
|------|---------|
| `docs/01-product-strategy.md` | Vision, positioning, monetization, startup path |
| `docs/02-tech-stack.md` | Full tech stack decisions with rationale |
| `docs/03-feature-roadmap.md` | All features, phased by branch/sprint |
| `docs/04-monetization.md` | Free vs Pro tier logic, pricing, growth |
| `design-system/01-visual-identity.md` | Matrix/retro aesthetic, tokens, typography |
| `design-system/02-component-library.md` | Every UI component spec |
| `wireframes/01-screens.md` | Every screen wireframed in ASCII |
| `wireframes/02-user-flows.md` | All user journeys mapped |
| `features/` | One file per feature with full implementation spec |
| `claude-code-prompts/` | Ready-to-paste prompts for Claude Code, one per branch |

---

## How to Use This Package

1. Start with `docs/01-product-strategy.md` for context
2. Read `design-system/01-visual-identity.md` before touching any UI
3. Execute branches in order using files in `claude-code-prompts/`
4. Each prompt in `claude-code-prompts/` is self-contained — paste it directly into Claude Code

---

## Branch Execution Order

```
branch/00-foundation        ← project setup, auth, navigation shell
branch/01-onboarding        ← new user flow, free tier clarity
branch/02-trip-tracking     ← auto-detect, live HUD redesign
branch/03-trip-summary      ← cinematic end screen, score system
branch/04-gamification      ← ranks, badges, streaks, XP
branch/05-social             ← shareable cards, friend challenges
branch/06-pro-features      ← route replay, G-force, analytics
branch/07-monetization      ← paywall UX, subscription flow
branch/08-performance       ← GPS accuracy, battery, background
branch/09-polish            ← animations, haptics, final QA
```
