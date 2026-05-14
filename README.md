!!! Grama Khata !!!

Grama Khata is a simple Android-based digital ledger application designed for rural shopkeepers to manage customer credit transactions digitally instead of maintaining traditional handwritten notebooks (“Vahi”).

The app focuses on simplicity, trust, and ease of use while helping small retailers track dues, manage transactions, and send payment reminders efficiently.


🚀 Features

* 👤 Customer Management

  * Add customer details
  * Store customer records securely

* ➕➖ Credit & Debit Tracking

  * Record transactions easily
  * Instant due calculation

* 📊 Due Dashboard

  * View customers sorted by highest due amount

* 📄 Daily Collection Report

  * Generate text-based daily reports

* 📱 WhatsApp / SMS Reminder

  * Send pre-filled payment reminders

* 🔐 Secure Login

  * PIN authentication
  * Biometric fingerprint login

* 🌙 Dark Mode

  * User-friendly theme support

* 📶 Offline First

  * Works without internet using Room Database

---

🛠️ Technologies Used

* Kotlin
* Jetpack Compose
* MVVM Architecture
* Room Database
* Android Navigation Component
* Biometric Authentication
* Material Design 3

---

🏗️ Architecture

The project follows the MVVM (Model-View-ViewModel) architecture:

text
UI (Compose Screens)
        ↓
ViewModel
        ↓
Room Database (DAO)


---

📂 Project Structure

text
com.example.gramakhata
│
├── data
│   ├── Customer.kt
│   ├── Transaction.kt
│   ├── DAO
│   └── Database
│
├── ui
│   ├── LoginScreen
│   ├── DashboardScreen
│   ├── CustomerDetailScreen
│   ├── SettingsScreen
│   └── ProfileScreen
│
├── viewmodel
│   └── KhataViewModel
│
└── theme
    └── UI Theme Files


---

📱 App Workflow

1. User logs in using PIN or Fingerprint : Fingerprint is visible when your phone act as an emulator.
2. Add customer details
3. Record credit/debit transactions
4. App updates net balance instantly
5. Dashboard displays pending dues
6. Send WhatsApp/SMS reminders
7. Generate daily collection report

---

🎯 Objectives

* Digitize rural credit management
* Reduce manual bookkeeping errors
* Improve financial transparency
* Enable quick customer communication
* Provide a simple and accessible solution for small retailers

---

🔮 Future Enhancements

* Multi-language support (Kannada/Hindi)
* Cloud backup using Firebase
* PDF report generation
* Voice-based transaction entry
* QR payment integration

---

👩‍💻 Developed By

Suchita
Android App Development using GenAI Internship Project

---

📌 Conclusion

Grama Khata bridges traditional trust-based credit systems with modern technology by providing a simple, reliable, and offline capable digital ledger solution for rural shopkeepers.
