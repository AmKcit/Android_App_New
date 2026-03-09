# 📊 Admin Panel Architecture & Workflow Diagrams

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  AdminMainActivity (Dashboard)                       │  │
│  │  - Statistics Display                                │  │
│  │  - User List (RecyclerView)                          │  │
│  │  - Navigation Buttons                                │  │
│  └──────────────────────────────────────────────────────┘  │
│         ↓              ↓              ↓                     │
│  ┌──────────────┬──────────────┬──────────────┐            │
│  │ Analytics    │ Settings     │ Edit User    │            │
│  │ Activity     │ Activity     │ Activity     │            │
│  └──────────────┴──────────────┴──────────────┘            │
└─────────────────────────────────────────────────────────────┘
         ↓                    ↓                    ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UserAdapter (RecyclerView Adapter)                  │  │
│  │  - User Card Creation                                │  │
│  │  - Edit/Delete Event Handling                        │  │
│  │  - Color-coded Badges                                │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  User Model (Data Structure)                         │  │
│  │  - User Properties                                   │  │
│  │  - Serializable for Intent Passing                   │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATA ACCESS LAYER                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Firebase Firestore                                  │  │
│  │  - User Collection                                   │  │
│  │  - Real-time Sync                                    │  │
│  │  - Document Updates                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Firebase Authentication                             │  │
│  │  - User Authentication                               │  │
│  │  - Session Management                                │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 User Management Workflow

```
┌─────────────────────┐
│  Admin Dashboard    │
│   (Dashboard View)  │
└──────────┬──────────┘
           │
      ┌────┴────┬────────────┬──────────────┐
      │         │            │              │
      ↓         ↓            ↓              ↓
   ┌──────┐ ┌──────┐ ┌──────────┐ ┌─────────┐
   │ View │ │Refresh│ │Analytics│ │Settings │
   │Users │ │ List  │ │Dashboard│ │Activity │
   └──┬───┘ └──┬───┘ └────┬────┘ └────┬────┘
      │        │          │           │
      └────┬───┴──────────┴───────────┘
           │
      ┌────▼─────────────────┐
      │  User Card (Item)    │
      │  Displays:           │
      │  - Email             │
      │  - Role Badge        │
      │  - Status Badge      │
      │  - Edit/Delete BTN   │
      └────┬──────────┬──────┘
           │          │
        ┌──▼──┐    ┌──▼──┐
        │Edit │    │Delete│
        └──┬──┘    └──┬──┘
           │          │
     ┌─────▼──┐  ┌────▼────────┐
     │Edit    │  │Confirmation │
     │Activity│  │Dialog       │
     │Opens   │  │             │
     └─────┬──┘  └────┬────────┘
           │          │ (Yes)
           │          │
     ┌─────▼──────────▼────┐
     │Firebase Update/Delete│
     │User Document        │
     └─────┬────────────────┘
           │
           ↓
     ┌──────────────┐
     │List Refreshed│
     │Statistics    │
     │Updated       │
     └──────────────┘
```

## 📱 Navigation Flow

```
                    ┌─────────────────┐
                    │  Login Screen   │
                    │  (Auth Flow)    │
                    └────────┬────────┘
                             │ (Admin Login)
                             ↓
                    ┌─────────────────────────┐
                    │ AdminMainActivity       │
                    │ (Main Dashboard)        │
        ┌───────────┼─────────────────────────┼───────────┐
        │           │                         │           │
        ↓           ↓                         ↓           ↓
    ┌───────┐  ┌─────────────┐  ┌──────────────┐  ┌────────┐
    │Edit   │  │Analytics    │  │Settings      │  │Logout  │
    │User   │  │Activity     │  │Activity      │  │Button  │
    │Activity│  │             │  │              │  │        │
    └───┬───┘  └──────┬──────┘  └────────┬─────┘  └────┬───┘
        │             │                  │             │
        ↓             ↓                  ↓             ↓
    ┌────────┐   ┌───────┐   ┌──────────────┐   ┌──────────┐
    │Firebase│   │Display│   │Save Settings │   │Sign Out  │
    │Update  │   │Metrics│   │to SharedPref │   │Firebase  │
    │User    │   │Server │   │              │   │          │
    │Data    │   │Health │   │              │   │          │
    └────┬───┘   └───┬───┘   └────────┬─────┘   └────┬─────┘
         │           │                │              │
         │           │                │              ↓
         │           │                │         ┌─────────┐
         │           │                │         │Login    │
         │           │                │         │Screen   │
         └───────────┴────────────────┴─────────┘ (Loop)
                          │
                          ↓
              ┌──────────────────────┐
              │Refresh User List     │
              │Update Statistics     │
              └──────────────────────┘
```

## 🎨 UI Component Hierarchy

```
activity_admin_main.xml
├── LinearLayout (Vertical) - Main Container
│   ├── LinearLayout - Header with Gradient
│   │   ├── TextView - "Admin Dashboard" (28sp, Bold)
│   │   └── TextView - "Manage users..." (14sp)
│   │
│   ├── LinearLayout - Statistics Section (Horizontal)
│   │   ├── CardView - Total Users Card
│   │   │   ├── TextView - Count (24sp, Bold)
│   │   │   └── TextView - Label (12sp)
│   │   ├── CardView - Active Users Card
│   │   │   ├── TextView - Count (24sp, Bold)
│   │   │   └── TextView - Label (12sp)
│   │   └── CardView - Admin Count Card
│   │       ├── TextView - Count (24sp, Bold)
│   │       └── TextView - Label (12sp)
│   │
│   ├── LinearLayout - List Header
│   │   ├── TextView - "Registered Users" (18sp, Bold)
│   │   └── Button - "Refresh"
│   │
│   ├── RecyclerView - User List
│   │   └── item_user_admin.xml (Repeated)
│   │       ├── LinearLayout - User Card
│   │       ├── LinearLayout - Avatar
│   │       ├── LinearLayout - User Info
│   │       │   ├── TextView - Email (14sp, Bold)
│   │       │   └── LinearLayout - Badges
│   │       │       ├── TextView - Role Badge (12sp)
│   │       │       └── TextView - Status Badge (11sp)
│   │       └── LinearLayout - Action Buttons
│   │           ├── Button - Edit
│   │           └── Button - Delete
│   │
│   └── LinearLayout - Action Buttons (Horizontal)
│       ├── Button - "Analytics"
│       ├── Button - "Settings"
│       └── Button - "Logout"
```

## 🔗 Class Dependencies

```
AdminMainActivity
├── depends on:
│   ├── UserAdapter
│   ├── User
│   ├── AdminSettingsActivity
│   ├── AdminAnalyticsActivity
│   ├── EditUserActivity
│   └── Firebase Firestore
└── uses:
    ├── RecyclerView
    ├── CardView
    ├── LinearLayout
    └── Button

UserAdapter
├── depends on:
│   ├── User
│   ├── OnUserActionListener (interface)
│   └── EditUserActivity
└── uses:
    ├── ViewHolder
    ├── CardView
    └── Button

EditUserActivity
├── depends on:
│   ├── User
│   ├── Firebase Firestore
│   └── SimpleDateFormat
└── uses:
    ├── Spinner
    ├── Switch
    ├── CardView
    └── Button

AdminAnalyticsActivity
├── depends on:
    └── Firebase Firestore
└── uses:
    ├── TextView
    ├── LinearLayout
    └── ScrollView

AdminSettingsActivity
├── depends on:
    └── SharedPreferences (ready)
└── uses:
    ├── Switch
    ├── CardView
    └── Button
```

## 💾 Data Flow

```
┌──────────────────────────┐
│  Firebase Firestore      │
│  Users Collection        │
│  ├── uid                 │
│  ├── email               │
│  ├── role (user/admin)   │
│  ├── active (bool)       │
│  ├── lastLogin (ms)      │
│  └── createdAt (ms)      │
└────────────┬─────────────┘
             │ (Query)
             ↓
┌──────────────────────────┐
│  AdminMainActivity       │
│  loadUsers()             │
│  ├── Query Firestore     │
│  ├── Parse Documents     │
│  ├── Create User Objects │
│  └── Update Statistics   │
└────────────┬─────────────┘
             │
        ┌────┴────┐
        │          │
        ↓          ↓
    ┌────────┐ ┌──────────┐
    │Update  │ │Update    │
    │Adapter │ │TextViews │
    │(notify)│ │(stats)   │
    └────────┘ └──────────┘
        │          │
        ↓          ↓
    ┌────────────────────┐
    │UI Refreshed        │
    │- New User List     │
    │- Updated Stats     │
    └────────────────────┘
```

## 🎯 Activity Launch Sequence

```
Step 1: User clicks "Edit" button
        │
        ↓
Step 2: UserAdapter.onBindViewHolder()
        │ detects click
        ↓
Step 3: Create Intent
        │ intent.putExtra("user", userObject)
        ↓
Step 4: Start EditUserActivity
        │ context.startActivity(intent)
        ↓
Step 5: EditUserActivity.onCreate()
        │ retrieves user from intent
        ↓
Step 6: loadUserData()
        │ populates UI fields
        ↓
Step 7: User makes changes
        │ (changes dropdown, toggle switch)
        ↓
Step 8: Click "Save"
        │
        ↓
Step 9: saveUserChanges()
        │ creates update map
        ↓
Step 10: Firebase update
         │ db.collection("Users")
         │   .document(userId)
         │   .update(changes)
         ↓
Step 11: Success callback
         │ Toast notification
         ↓
Step 12: finish()
         │ return to AdminMainActivity
         ↓
Step 13: User manually clicks Refresh
         │ or onCreate loads data again
         ↓
Step 14: Updated list displayed
```

## 🎨 Color Theme Application

```
Blue (#1976D2) - Primary
├── Header background
├── Primary buttons (Refresh, Analytics)
├── User avatar background
├── Total Users stat color
└── Primary text highlights

Green (#388E3C) - Success
├── Active status badge
├── Active Users stat color
└── Success state indicators

Orange (#FF9800) - Warning
├── Settings button
├── Role badge background
├── Warning indicators
└── Secondary actions

Red (#E74C3C) - Danger
├── Delete button
├── Logout button
├── Admin count stat color
├── Error states
└── Danger indicators

Purple (#673AB7) - Analytics
├── Analytics button
└── Special feature highlight

Gray Palette:
├── #F8F9FA - Background
├── #2C3E50 - Dark text
├── #757575 - Secondary text
└── #FFFFFF - Cards/backgrounds
```

## 📊 State Management

```
AdminMainActivity States:
┌─────────────────┐
│ Loading         │ ← Fetching data from Firebase
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ Loaded          │ ← Data ready, UI displayed
└────────┬────────┘
         │ ┌──────────────────────┐
         ├─→ Edit User Click     │ → EditUserActivity
         │ └──────────────────────┘
         │ ┌──────────────────────┐
         ├─→ Delete User Click   │ → Delete Dialog
         │ └──────────────────────┘
         │ ┌──────────────────────┐
         ├─→ Analytics Click     │ → Analytics Activity
         │ └──────────────────────┘
         │ ┌──────────────────────┐
         ├─→ Settings Click      │ → Settings Activity
         │ └──────────────────────┘
         │ ┌──────────────────────┐
         └─→ Refresh Click       │ → Reload from Firebase
           └──────────────────────┘

EditUserActivity States:
┌──────────────┐
│ Loading User │ ← From Intent
└──────┬───────┘
       │
       ↓
┌──────────────┐
│ User Loaded  │ ← Data displayed in form
└──────┬───────┘
       │ ┌──────────────┐
       ├─→ Save Click  │ → Update Firebase → finish()
       │ └──────────────┘
       │ ┌──────────────┐
       └─→ Cancel Click│ → finish() without changes
         └──────────────┘
```

---

## 📝 Notes

- Each diagram shows a different aspect of the system
- Color codes match the Material Design palette
- Flow diagrams show both happy paths and alternatives
- Component hierarchies show nesting and relationships
- Dependencies help understand integration points

---

**Visual Documentation Complete** ✨
**Version**: 1.0.0
**Date**: March 2026

