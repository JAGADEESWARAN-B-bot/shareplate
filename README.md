# COMMUNITY FOOD SHARING PLATFORM

A modern, full-stack, AI-powered platform connecting restaurants, bakeries, community organizations, and individuals to share surplus food safely, minimize food waste, and support local community hunger relief.

---

## Key Features

1. **Brand Identity & Nature-Inspired Theme**
   - Palette: Deep Forest Green (`#173F35`), Emerald Green (`#218C65`), Soft Mint (`#E7F5EC`), Warm Ivory (`#FAF8F1`), Charcoal (`#202923`), and Muted Sage (`#A9BBA9`).
   - Custom typography: **Manrope** for display headlines, **Inter** for readable body text.
   - Custom leaf and plate community brand icon.

2. **Complete Food Donation Workflow**
   - Detailed specification submission: Food name, category, quantity, unit, estimated servings, preparation date/time, temperature control & storage method, packaging condition, hygiene notes, and disclosed allergens.
   - Multi-step donation progress indicator (Details -> AI Quality Screening -> Safety Review & Checklist).

3. **AI-Powered Visual Food Quality Screening**
   - Visual feature extraction and safety rule validation:
     - Detects abnormal discoloration, mold patterns, packaging damage, and unsealed exposure.
     - Validates storage temperature safety guidelines (Danger Zone: 4°C - 60°C).
   - Returns structured assessment reports:
     - `NO OBVIOUS VISUAL WARNING SIGNS`
     - `POTENTIAL QUALITY CONCERN`
     - `UNABLE TO ASSESS`
   - Mandatory food safety disclaimer:
     > *"AI screening identifies possible visible warning signs only. It cannot confirm that food is safe to eat or certify its quality. Always follow applicable food-safety guidance and verify preparation, storage, and handling conditions."*

4. **Food Safety Review Checklist**
   - Required verification check before publishing: Accurate preparation history, cold chain compliance, allergen disclosures, and acknowledgment of screening limitations.

5. **Community Discovery & Food Requests**
   - Search by keyword, food category, pricing type (Free vs. Subsidized), and proximity.
   - Direct coordination via pre-filled **WhatsApp** message containing donation code.
   - Community food request bulletin for pantries and shelters to broadcast urgent meal needs.

6. **User Dashboard & Role Management**
   - Real-time impact metrics (Active donations, requests, servings shared).
   - Multi-role support: Individual, Restaurant, NGO/Charity, Community Organization.
   - In-app notification center for status transitions.

7. **Admin Safety Portal**
   - Comprehensive audit trail.
   - Manual approval and rejection workflow for pending food donations.
   - Platform metrics computed directly from database records.

---

## Vercel Deployment Guide

### Why Vercel Showed 404
When the repository was first pushed or deployed, it lacked a root `package.json`, `index.html`, and `vercel.json` SPA routing rewrites. Vercel had no web build output to serve at the root URL `/`.

### How It Has Been Fixed
1. Created `package.json` with standard Vite React build scripts (`build: "vite build"`).
2. Created `index.html` at the repository root linking the Manrope and Inter typography and mounting `<div id="root"></div>`.
3. Created `vite.config.js` targeting the `dist` directory.
4. Created `vercel.json` with SPA catch-all rewrites (`"rewrites": [{"source": "/api/(.*)", "destination": "/api/$1"}, {"source": "/(.*)", "destination": "/index.html"}]`), ensuring all routes load without 404 errors on direct visit or page refresh.
5. Implemented `/api/analyze-food.js` as a Vercel Serverless Function for secure server-side AI processing.

### Deploying to Vercel (Step-by-Step)
1. Push your updated code to your GitHub repository:
   ```bash
   git add .
   git commit -m "Fix Vercel 404 by adding complete React frontend and Vercel routing"
   git push origin main
   ```
2. In the Vercel Dashboard:
   - Framework Preset: **Vite** (auto-detected)
   - Root Directory: `./`
   - Build Command: `npm run build`
   - Output Directory: `dist`
3. Configure Environment Variables in Vercel Project Settings (optional for cloud backend & Gemini):
   - `GEMINI_API_KEY`: Your Gemini API key for server-side AI screening
   - `VITE_SUPABASE_URL`: Your Supabase Project URL
   - `VITE_SUPABASE_ANON_KEY`: Your Supabase Anon Key
4. Click **Deploy**. The root URL `/` will now load the live Community Food Sharing Platform immediately.

---

## Technology Stack & Architecture

- **Web Frontend:** React 18, Vite 5, Lucide Icons.
- **Serverless API:** Vercel Functions (`api/analyze-food.js`) with Gemini Vision integration.
- **Mobile Client:** Android Native (Kotlin, Jetpack Compose, Material Design 3, Room, Coroutines, Flow).
- **Backend / Cloud Schema:** Supabase PostgreSQL with Row Level Security (RLS) policies (see `supabase_schema.sql`).
