# 🏫 Shale Namma Pride (ಶಾಲಾ ನಮ್ಮ ಹೆಮ್ಮೆ)

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)

**Shale Namma Pride** is a mission-driven Android application designed to bridge the gap between rural schools and the community. By providing a transparent window into school nutrition, facilities, and student achievements, the app empowers parents and stakeholders to support quality education in rural areas.

---

## ✨ Key Features

### 🌍 Bilingual Experience
*   **Localized Interface**: Full support for **English** and **Kannada**.
*   **Runtime Toggle**: Seamlessly switch languages within the app without losing your state.

### 🍱 Nutrition & Wellness
*   **Today's Menu**: Real-time updates on daily mid-day meals.
*   **Nutrition Reports**: Detailed reports on the nutritional value provided to students.
*   **Weekly Timetable**: A transparent view of the school's planned meal schedule.

### 🛠️ School Transparency
*   **Facility Tour**: Virtual tour of school resources like Science Labs, Libraries, and Computer rooms.
*   **Student Stars**: Recognizing and celebrating student excellence in sports, arts, and academics.
*   **Feedback System**: Anonymously submit thoughts or suggestions to improve school quality.

### 👨‍💼 Admin Dashboard
*   **Dynamic Updates**: Admins can update meal plans, facility descriptions, and student stars directly from the app.
*   **Image Management**: Integrated image uploading for facilities and student achievements.
*   **Role-Based Access**: Secure Google Authentication determines user permissions.

---

## 🚀 Tech Stack

*   **Language**: Kotlin (100%)
*   **UI Framework**: Android XML (ConstraintLayout, ViewPager2, RecyclerView)
*   **Backend**: 
    *   **Firebase Auth**: Google & Guest Sign-in.
    *   **Cloud Firestore**: Real-time data synchronization.
    *   **Firebase Storage**: Image hosting for facilities and achievements.
*   **Libraries**:
    *   **Coil**: Modern image loading with Kotlin Coroutines.
    *   **ViewBinding**: Safe and efficient UI interaction.
    *   **Material Components**: Premium, state-of-the-art UI design.

---

## 🛠️ Installation & Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/AasishBVijayan/Shale-Namma-Pride.git
    ```
2.  **Open in Android Studio**:
    *   File > Open > Select the `ShaleNamaPride` folder.
3.  **Firebase Configuration**:
    *   Create a project on the [Firebase Console](https://console.firebase.google.com/).
    *   Add your Android app (Package Name: `com.shale.nammapride`).
    *   Download the `google-services.json` and place it in the `app/` directory.
    *   Enable **Google Authentication**, **Firestore**, and **Storage**.
4.  **Build and Run**:
    *   Connect your device and click **Run**.

---

## 📸 Screenshots

| Login Screen | Dashboard (English) | Dashboard (Kannada) |
| :---: | :---: | :---: |
| ![Login](https://via.placeholder.com/200x400?text=Login) | ![Dashboard EN](https://via.placeholder.com/200x400?text=Dashboard+EN) | ![Dashboard KN](https://via.placeholder.com/200x400?text=Dashboard+KN) |

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

**Shale Namma Pride** — *Empowering Rural Education through Transparency.*