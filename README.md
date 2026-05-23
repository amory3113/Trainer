# Intelligent Personal Trainer & Nutrition Assistant (Android Native)
An advanced, fully native Android application developed as an engineering thesis project. The app functions as an intelligent personal trainer and dietary assistant, focusing on data privacy and user safety. It automatically automates workout generation and calorie tracking without relying on external cloud servers.

## 🚀 Key Features & Core Logic
* **Medical Risk Assessment Algorithm: The core innovation of the app. Based on an initial medical questionnaire, the algorithm dynamically filters the local exercise database to completely exclude movements that pose a risk to users with cardiovascular (tagged as CARDIO) or orthopedic (tagged as JOINTS) conditions.

* Smart Workout Plan Generator: Automatically builds tailored training programs based on user goals, preferred location (HOME or GYM), and weekly frequency.

  * 1–3 days: Generates a Full Body Workout (FBW) split with alternating A/B routines.

  * 4 days: Generates an Upper/Lower split using shuffled().take(n) for routine diversity.

  * 5+ days: Generates a classic bodybuilding Split routine targeting single muscle groups per day.

* Dietary Module & Self-Adaptation: Computes Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE) using the Mifflin-St Jeor equation. It dynamically allocates macronutrients according to the user's specific fitness goal. Recording a new weight entry triggers an automatic background re-calibration of all caloric and macronutrient targets.

* 100% Offline Capability & Privacy: All sensitive anthropometric and medical data is stored strictly in the device's internal memory via a local database, satisfying strict user privacy standards.

* Local Notification System: Utilizes system alarms to handle daily dietary and workout reminders entirely offline, eliminating the need for cloud-based push notifications.

# 🛠 Tech Stack (Modern Android Development)
Language: Kotlin (100% Native)

* UI Framework: Jetpack Compose (Declarative UI with Material Design 3 guidelines)

* Architecture: MVVM (Model-View-ViewModel) with a Feature-Based Package Structure for high cohesion and scalability. Implements Unidirectional Data Flow (UDF).

* Asynchronous Programming: Kotlin Coroutines & Reative Streams (Flow, StateFlow, SharedFlow).

* Local Persistence: Room Database (built on top of SQLite) with automated pre-seeding from a local JSON asset file (ExerciseLoader).

* Navigation: Jetpack Navigation Compose (utilizing nested navigation graphs and shared ViewModels).

* Build System: Gradle (Kotlin DSL).

# 📐 Database Architecture
The local database scheme consists of 7 normalized tables managed via Room:

1  user_profile — stores anthropometric data, medical flags, and nutritional goals.

2  exercises — dictionary table containing exercise definitions, muscle groups, and health risk tags.

3  workout_templates — stores workout routine headers.

4  workout_exercises — junction table realizing a Many-to-Many relationship between templates and exercises, featuring parameters like sets, reps, order, and OnDelete = ForeignKey.CASCADE restrictions.

5  schedule — maps workout template IDs to specific days of the week.

6  nutrition_history — tracks daily calorie and macronutrient intake logs over time.

7  weight_history — archives body mass measurements indexed by timestamp.

# 💡 Technical Highlights for Recruiters
Advanced Async BroadcastReceiver: To query the Room database safely within a system broadcast handler (where the onReceive lifecycle is extremely short), the notification system implements the goAsync() API. This shifts database processing to Dispatchers.IO and explicitly completes the pending result upon delivery, avoiding ANRs and process death.

Shared ViewModel Pattern: Multi-step onboarding and medical survey screens are grouped into a nested navigation graph (ONBOARDING_GRAPH). They securely share a single instance of OnboardingViewModel scoped to the graph's lifecycle using navController.getBackStackEntry(), preventing data loss across state transitions.

In-Memory to Relational ID Mapping: The workout generator creates entire routine schemas in-memory using temporary, decoupled IDs. During the final database write phase, the repository leverages an explicit mapping cache (tempToRealIdMap) to correctly correlate auto-generated SQLite primary keys with their children entries and schedule records within a thread-safe coroutine scope.

Database Integrity: Complex multi-table operations (such as resetting or overwriting an entire training plan) are wrapped inside custom Room @Transaction blocks to guarantee strict database atomicity and ACID compliance.
