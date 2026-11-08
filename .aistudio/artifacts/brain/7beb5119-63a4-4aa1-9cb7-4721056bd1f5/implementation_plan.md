# حساباتی خۆم (Hesabati Xom - Monthly Accounts & Delivery Manager)

A production-ready Android application built in Kotlin & Jetpack Compose, localized natively in Kurdish Sorani (کوردی) and Iraqi Dinar (IQD). Named **"حساباتی خۆم"**, it features automated parcel/delivery photo accounting (250 IQD default per parcel), a client & page directory (name and phone number), individual company ledger breakdowns alongside overall totals, one-click resets, and complete monthly personal finance tracking (income, expenses, budgets, debts). Fully configured for direct APK generation and clean GitHub repository publishing.

## User Review & Critical Decisions

> [!IMPORTANT]
> Specific naming and production requirements confirmed:

- **App Name (ناوی ئەپ)**: **"حساباتی خۆم"** (Hesabati Xom). Synchronized across `metadata.json`, `strings.xml` (`app_name`), and `settings.gradle.kts`.
- **Production APK & GitHub Readiness**:
  - Valid `applicationId`: `com.aistudio.hesabatixom.kurdish`.
  - Configured Gradle signing, Proguard rules, and dependencies for zero-warning `assembleDebug` and `assembleRelease` APK builds.
  - Custom Kurdish adaptive launcher icon and metadata ready for store and device installs.
- **Client & Page Directory (پەیج و کۆمپانیاکان)**:
  - Register partners with Name, Phone number, and Notes.
  - Directly associate parcels with each client.
- **Detailed Accounting (حساباتی ورد)**:
  - Count of parcels and total IQD calculated per page (e.g., ٥٠ بەرید = ١٢,٥٠٠ د.ع).
  - Grand total across all parcels/pages displayed distinctly.
- **Photo Parcel Counter (وێنە و بەرید)**:
  - Camera capture and Gallery photo picker.
  - 250 IQD auto-rate per photo with custom rate support.
  - One-click reset with confirmation dialog.
- **Complete Personal Finance**:
  - Monthly income, expenses, category budgets, and debt tracking (قەرزەکان).

---

## 1. Overview & Core Concept

### Key Capabilities
1. **Hero Delivery & Parcel Dashboard (بەرید و حساباتی ئۆتۆماتیک)**:
   - Live metrics: Total parcels, Total IQD earned (250 IQD per parcel), Active month.
   - One-Click Reset (سفڕکردنەوە) with a safety confirmation modal.
   - Quick "تۆمارکردنی بەرید" with live camera or gallery photo, notes, and company assignment.
2. **Companies & Pages Directory (پەیج و شوێنەکانی کار)**:
   - List of pages with phone numbers and parcel counts.
   - Settle/clean balance per specific company upon delivery payout.
3. **Monthly Personal Finance (خەرجی و داهات و قەرز)**:
   - General financial ledger for personal life: income, expenses by category, monthly budget limits, and debts (قەرزم لایە / قەرزارم).
4. **Offline & Private**:
   - 100% device-local storage with Jetpack Room SQLite; zero internet or login required.

---

## 2. User Experience & Visual Design

### Screen Flow & Visual Hierarchy

```
┌────────────────────────────────────────────────────────┐
│                      حساباتی خۆم                       │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 📦 کۆی گشتی بەریدەکان: ١٢٠ بەرید                 │  │
│  │ 💰 کۆی پارەی بەرید: ٣٠,٠٠٠ د.ع (بۆ هەر دانەیەک ٢٥٠)│  │
│  │ [ 🔄 سفڕکردنەوەی گشتی ]     [ ➕ پەیجی نوێ ]      │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [ 📸 وێنەی نوێی بەرید ]  ➔ هەڵبژاردنی پەیج و تێبینی    │
│                                                        │
│  حساباتی جیاوازی پەیجەکان:                             │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 🏷️ پەیجی فاشن ستۆر (0750xxxxxxx)                │  │
│  │ ٥٠ بەرید  •  ١٢,٥٠٠ د.ع      [پاکتاوکردن / وردەکاری]│  │
│  ├──────────────────────────────────────────────────┤  │
│  │ 🏷️ ئۆنلاین شۆپ کوردستان (0770xxxxxxx)            │  │
│  │ ٣٠ بەرید  •  ٧,٥٠٠ د.ع       [پاکتاوکردن / وردەکاری]│  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  خوارەوەی شاشە (Bottom Navigation):                    │
│  [ بەرید و ژمێرەر ]  [ پەیجەکان ]  [ دارایی و قەرز ]   │
└────────────────────────────────────────────────────────┘
```

### Visual Identity
- **Palette**: Modern Emerald Fintech (`#059669` / `#10B981`), Delivery Amber (`#F59E0B`), and Slate Dark/Light backgrounds.
- **RTL Native**: Natural Right-to-Left Kurdish Sorani reading flow, Arabic-Indic/Kurdish numerals and currency (`د.ع`).

---

## 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────┐
│               Jetpack Compose M3 (RTL Supported)               │
│  - MainDashboardScreen (Grand Total, Quick Add, Reset Modal)   │
│  - CompaniesScreen (Directory, Add Page, Call/WhatsApp, Settle)│
│  - AddParcelDialog (Camera capture / Gallery + Page selector)  │
│  - FinanceScreen (Monthly income, expenses, debts)             │
│  - FullImagePreviewModal                                       │
└───────────────────────────────┬────────────────────────────────┘
                                │ StateFlow<AppUiState>
                                ▼
┌────────────────────────────────────────────────────────────────┐
│                       HesabatViewModel                         │
│  - Aggregates parcel metrics, per-company stats, and finances  │
│  - Handles image persistence to app internal storage           │
└───────────────────────────────┬────────────────────────────────┘
                                │ Repository
                                ▼
┌────────────────────────────────────────────────────────────────┐
│                     HesabatRepository                          │
│  - Room Database (CompanyDao, ParcelDao, TransactionDao, DebtDao)
└────────────────────────────────────────────────────────────────┘
```

### Database Schema
- **`CompanyEntity`**: `id`, `name`, `phoneNumber`, `notes`, `customRate` (default 250.0).
- **`ParcelEntity`**: `id`, `companyId`, `imagePath`, `rateIqd`, `customerOrTracking`, `notes`, `timestamp`, `monthKey`, `isSettled`.
- **`TransactionEntity`**: `id`, `amount`, `type` (EXPENSE/INCOME), `category`, `note`, `timestamp`, `monthKey`.
- **`DebtEntity`**: `id`, `personName`, `amount`, `type` (LENT/BORROWED), `note`, `isSettled`, `timestamp`.

---

## 4. APK & GitHub Delivery Checklist

1. **Naming & Identity**:
   - `metadata.json`: Name set to `"حساباتی خۆم"`.
   - `strings.xml`: `app_name` set to `"حساباتی خۆم"`.
   - `settings.gradle.kts`: `rootProject.name = "حساباتی خۆم"`.
   - `applicationId`: `com.aistudio.hesabatixom.kurdish`.
2. **Camera & File Storage**:
   - Add `<uses-permission android:name="android.permission.CAMERA" />` to `AndroidManifest.xml`.
   - Configure `androidx.core.content.FileProvider` in `AndroidManifest.xml` with `res/xml/file_paths.xml` for zero-crash camera photo capture.
   - Enable `libs.coil.compose` in `app/build.gradle.kts`.
3. **Adaptive App Icon**: Custom Kurdish finance launcher icon configured.
4. **Compilation Verification**: Complete build verification via `compile_applet` confirming clean build ready for APK export.
