-- =========================================================================
-- COMMUNITY FOOD SHARING PLATFORM
-- Supabase PostgreSQL Database Schema & Row Level Security (RLS) Policies
-- =========================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. PROFILES TABLE (Users & Organizations)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    account_type TEXT NOT NULL CHECK (account_type IN ('Individual', 'Restaurant', 'NGO/Charity', 'Community Organization')),
    location TEXT NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS for profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public profiles are viewable by authenticated users"
ON public.profiles FOR SELECT
TO authenticated
USING (true);

CREATE POLICY "Users can update their own profile"
ON public.profiles FOR UPDATE
TO authenticated
USING (auth.uid() = id);

-- 2. DONATIONS TABLE
CREATE TABLE IF NOT EXISTS public.donations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    donation_code TEXT UNIQUE NOT NULL, -- e.g. DON-2026-X8Y1
    donor_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    food_name TEXT NOT NULL,
    category TEXT NOT NULL CHECK (category IN ('Prepared Meals', 'Bakery', 'Fresh Produce', 'Dairy', 'Pantry & Canned', 'Beverages', 'Other')),
    description TEXT NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    unit TEXT NOT NULL,
    servings INTEGER NOT NULL DEFAULT 1,
    prep_datetime TIMESTAMPTZ,
    storage_method TEXT NOT NULL CHECK (storage_method IN ('Refrigerated', 'Frozen', 'Room Temperature', 'Heated Container')),
    location TEXT NOT NULL,
    deadline TIMESTAMPTZ NOT NULL,
    contact_method TEXT NOT NULL DEFAULT 'WhatsApp',
    contact_phone TEXT NOT NULL,
    packaging_condition TEXT NOT NULL,
    hygiene_notes TEXT,
    allergen_info TEXT DEFAULT 'None',
    pricing_type TEXT NOT NULL CHECK (pricing_type IN ('Free Donation', 'Fixed-Price / Subsidized')),
    price_amount NUMERIC(10,2) DEFAULT 0.00,
    status TEXT NOT NULL DEFAULT 'Pending Review' CHECK (status IN ('Pending Review', 'Available', 'Reserved', 'Completed', 'Rejected')),
    image_url TEXT,
    ai_screening_status TEXT DEFAULT 'PENDING' CHECK (ai_screening_status IN ('NO OBVIOUS VISUAL WARNING SIGNS', 'POTENTIAL QUALITY CONCERN', 'UNABLE TO ASSESS', 'PENDING')),
    ai_summary TEXT,
    ai_warning_signs TEXT,
    ai_limitations TEXT,
    ai_next_steps TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.donations ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Active donations are viewable by anyone"
ON public.donations FOR SELECT
TO authenticated, anon
USING (status IN ('Available', 'Reserved', 'Completed'));

CREATE POLICY "Donors can view their own submissions regardless of status"
ON public.donations FOR SELECT
TO authenticated
USING (auth.uid() = donor_id);

CREATE POLICY "Donors can insert donations"
ON public.donations FOR INSERT
TO authenticated
WITH CHECK (auth.uid() = donor_id);

CREATE POLICY "Donors can update their own donations"
ON public.donations FOR UPDATE
TO authenticated
USING (auth.uid() = donor_id);

CREATE POLICY "Admins have full access to donations"
ON public.donations FOR ALL
TO authenticated
USING (
    EXISTS (SELECT 1 FROM public.profiles WHERE profiles.id = auth.uid() AND profiles.is_admin = TRUE)
);

-- 3. FOOD REQUESTS TABLE
CREATE TABLE IF NOT EXISTS public.food_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    request_code TEXT UNIQUE NOT NULL,
    recipient_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    category TEXT NOT NULL,
    quantity TEXT NOT NULL,
    servings_needed INTEGER NOT NULL DEFAULT 1,
    needed_datetime TIMESTAMPTZ NOT NULL,
    location TEXT NOT NULL,
    dietary_requirements TEXT,
    allergies TEXT,
    additional_notes TEXT,
    contact_phone TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'Open' CHECK (status IN ('Open', 'Matched', 'Fulfilled', 'Cancelled')),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.food_requests ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Requests are viewable by all authenticated users"
ON public.food_requests FOR SELECT
TO authenticated
USING (true);

CREATE POLICY "Recipients can insert food requests"
ON public.food_requests FOR INSERT
TO authenticated
WITH CHECK (auth.uid() = recipient_id);

CREATE POLICY "Recipients can update their food requests"
ON public.food_requests FOR UPDATE
TO authenticated
USING (auth.uid() = recipient_id);

-- 4. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view and update their own notifications"
ON public.notifications FOR ALL
TO authenticated
USING (auth.uid() = user_id);

-- 5. ADMIN AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS public.admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admin_id UUID NOT NULL REFERENCES public.profiles(id),
    action TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    details TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.admin_audit_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admins can view and write audit logs"
ON public.admin_audit_logs FOR ALL
TO authenticated
USING (
    EXISTS (SELECT 1 FROM public.profiles WHERE profiles.id = auth.uid() AND profiles.is_admin = TRUE)
);

-- Storage bucket creation for food images
INSERT INTO storage.buckets (id, name, public)
VALUES ('food-images', 'food-images', true)
ON CONFLICT (id) DO NOTHING;

CREATE POLICY "Public read food images"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'food-images');

CREATE POLICY "Authenticated users can upload food images"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'food-images');
