# AgriCycle — Smart Agricultural Waste Marketplace
> Android mobile application connecting agricultural waste suppliers with buyers | Built with Android Studio & Firebase

## Overview
AgriCycle is a full-stack Android mobile application that creates a marketplace for agricultural waste — connecting suppliers who have waste by-products with buyers who want to reuse, recycle, or repurpose them. The app features role-based dashboards, real-time listings, offer negotiation, direct purchasing, and a supplier rating system.

Built as a university project at the University of Sharjah using Android Studio and Firebase (Authentication & Realtime Database). Includes a complete test specification covering unit, integration, validation, and system testing.

---

## Features
- **Role-based dashboards** — separate interfaces for Suppliers and Buyers
- **Waste listings marketplace** — suppliers post items with name, type, and price
- **Offer negotiation** — buyers submit offers, suppliers accept or reject
- **Direct purchase** — buyers can buy listings instantly at the listed price
- **Supplier rating system** — buyers can rate suppliers after completed deals
- **Firebase real-time sync** — all data updates instantly across devices
- **Full test specification** — unit, integration, validation, and system tests

---

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Java |
| IDE | Android Studio |
| Authentication | Firebase Authentication (email/password) |
| Database | Firebase Realtime Database |
| UI | Android XML Layouts |
| Architecture | Activity-based with RecyclerView Adapter pattern |

---

## Project Structure
```
AgriCycle/
├── java/
│   ├── SignActivity.java          # Login screen
│   ├── SignUpActivity.java        # Registration with role selection
│   ├── MainActivity.java          # Role-based dashboard
│   ├── AddListingActivity.java    # Supplier: create waste listing
│   ├── ViewWasteListActivity.java # Browse listings, offers, deals
│   ├── ListingAdapter.java        # RecyclerView adapter (buyer + supplier modes)
│   ├── ProfileActivity.java       # User profile and rating system
│   ├── Listing.java               # Listing data model
│   └── User.java                  # User data model
├── layout/
│   ├── activity_sign.xml
│   ├── activity_sign_up.xml
│   ├── activity_main.xml
│   ├── activity_add_listing.xml
│   ├── activity_view_waste_list.xml
│   ├── activity_profile.xml
│   └── list_item_listing.xml
└── docs/
    ├── AgriCycle_User_Manual.pdf
    └── AgriCycle_Test_Specification.pdf
```

---

## Getting Started

### Prerequisites
- Android Studio installed
- Firebase project set up (Authentication + Realtime Database enabled)
- Android device or emulator (Android 10+)

### Setup
1. Clone the repository
2. Open the project in Android Studio
3. Connect your Firebase project by adding your `google-services.json` to the `app/` folder
4. Enable **Email/Password** authentication in Firebase Console
5. Set up Firebase Realtime Database with the following rules:
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```
6. Run the app on your device or emulator

---

## Use Cases
**UC1 — Supplier adds a waste listing**
Supplier logs in → taps "Add Waste Listing" → enters name, type, price → listing saved to Firebase with status "available"

**UC2 — Buyer browses and makes offer / buys directly**
Buyer logs in → views all listings → submits an offer or buys directly → listing status updates to "pending" or "sold"

**UC3 — Supplier manages offers and accepted deals**
Supplier views pending offers → accepts or rejects → accepted deals appear in "Accepted Offers" screen

---

## Documentation
Full documentation is available in the `/docs` folder:
- **User Manual** — installation, sign-in, and full feature walkthrough with screenshots
- **Test Specification** — complete testing strategy including unit, integration, validation, and system test cases

---

## Testing Summary
| Test Type | Components Tested | Result |
|---|---|---|
| Unit Testing | SignActivity, SignUpActivity, AddListingActivity, ListingAdapter, ProfileActivity | ✅ Pass |
| Integration Testing | Supplier–Buyer offer flow, Direct buy flow | ✅ Pass |
| Validation Testing | UC1, UC2, UC3 complete flows | ✅ Pass |
| System Testing | Recovery, security, stress, performance | ✅ Pass |

---

## Contributors
**Mohammed Nassar Alshimmari** — Unit Testing Lead (Adapters & Firebase Logic)
University of Sharjah, Computer Engineering

[LinkedIn](https://linkedin.com/in/mohammedalshimmari) · [GitHub](https://github.com/mohammedalshimmari7)

---

*University of Sharjah — Computer Engineering Department — 2025*
