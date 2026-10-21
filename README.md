# COMMUNITY FOOD SHARING PLATFORM

A modern, AI-powered platform connecting restaurants, bakeries, community organizations, and individuals to share surplus food safely, minimize food waste, and support local community hunger relief.

---

## Key Features

1. **Brand Identity & Nature-Inspired Theme**
   - Palette: Deep Forest Green (`#173F35`), Emerald Green (`#218C65`), Soft Mint (`#E7F5EC`), Warm Ivory (`#FAF8F1`), Charcoal (`#202923`), and Muted Sage (`#A9BBA9`).
   - Custom typography pairing: **Manrope** for display headlines, **Inter** for readable body text.
   - Custom adaptive leaf and plate community brand icon.

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

## Technology Stack & Architecture

- **Mobile Client:** Android Native (Kotlin, Jetpack Compose, Material Design 3, Coroutines, Flow).
- **Local Persistence:** Room Database with initial realistic community data seed (restaurants, pantries, bakeries).
- **Backend / Cloud Schema:** Supabase PostgreSQL with Row Level Security (RLS) policies (see `supabase_schema.sql`).
- **AI Engine:** Google Gemini Vision capable models with structured JSON output and safety heuristic pipeline.

---

## Environment Variables & Configuration

Set the following in `.env` or in AI Studio Secrets:
```env
# Gemini API Key (configured securely in AI Studio Secrets panel)
GEMINI_API_KEY=YOUR_GEMINI_API_KEY

# Optional: Supabase credentials for cloud synchronization
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

---

## Database Setup (Supabase)

If deploying the cloud backend:
1. Open your Supabase project SQL Editor.
2. Run the SQL script located in `supabase_schema.sql`.
3. The script configures tables for `profiles`, `donations`, `food_requests`, `notifications`, `admin_audit_logs`, storage buckets for `food-images`, and Row Level Security policies.
