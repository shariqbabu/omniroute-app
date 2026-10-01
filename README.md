# OmniRoute Mobile (Android Native)

Modern, high-performance Android Client & Management Console for **OmniRoute v3.8.51 AI Gateway** built with **Kotlin, Jetpack Compose, Material 3, OkHttp SSE Streaming, and DataStore**.

---

## 🚀 Key Features

* **Dashboard & Telemetry**: Live status of OmniRoute Gateway, tokens saved counter (RTK + Caveman 15-95% compression), active providers (359 providers, 150+ free), and live latency.
* **AI Playground**: Real-time SSE streaming chat with any model (Claude 3.5 Sonnet, GPT-4o, Gemini 1.5 Pro, DeepSeek V3/R1, Moonshot/Kimi, Qwen, etc.).
* **Provider Management Hub**: Categorized directory (Tier 1, Free Tiers, Cloud LPUs, Chinese Models, Local Inference). Toggle providers on/off and configure custom keys/base URLs.
* **Smart Routing & Fallback Chains**: Inspect and toggle automatic failovers on rate-limits (429) or server errors (5xx).
* **Live Audit Logs**: Request logs with latency, token usage, and fallback chain inspection.
* **VPS Connection Settings**: Configure your VPS OmniRoute host URL (`http://<IP>:3000` or `https://domain`), master secret key, and temperature controls.

---

## 🛠️ Automated Build via GitHub Actions

This repository includes a GitHub Action workflow `.github/workflows/build-apk.yml`.
When you push to GitHub:
1. It automatically builds the **Debug APK** and **Release APK**.
2. Uploads the ready-to-install `.apk` in GitHub Actions Artifacts.

---

## 📱 Local Build Instructions

```bash
cd omniroute-android
chmod +x gradlew
./gradlew assembleDebug
```
Output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
