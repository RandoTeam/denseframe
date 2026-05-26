# Manual ARCore Capture Checklist: OnePlus 13

Use this checklist before treating ARCore raw-depth capture as field-ready.

1. Build debug APK: `.\gradlew.bat assembleDebug --no-daemon`.
2. Install on OnePlus 13.
3. Confirm Google Play Services for AR is installed and up to date.
4. Launch DenseFrame.
5. Verify gallery opens without creating an ARCore session.
6. Open Capture screen.
7. Grant camera permission.
8. Verify ARCore availability status.
9. Verify selected depth mode.
10. Start capture.
11. Move slowly around a textured object.
12. Confirm tracking state becomes `TRACKING`.
13. Confirm accepted frame count increases.
14. Confirm depth freshness reports `NEW_DEPTH` at least sometimes.
15. Confirm confidence/depth payloads are written for accepted depth frames.
16. Cover camera or point at a blank wall and confirm tracking/depth degradation is surfaced.
17. Stop capture.
18. Confirm project finalization.
19. Confirm DFR manifest is readable.
20. Confirm frame folders contain `frame.json`, `depth_u16.bin` when depth was present, `confidence_u8.bin` when confidence was present, and `checksum.sha256`.
21. Confirm project validator passes.
22. Restart app and confirm the project remains accessible or the missing gallery persistence is documented.
23. Kill app during capture and inspect whether incomplete frames are reported safely.
24. Record thermal and battery notes for 3-minute and 10-minute capture sessions.

Record device build, Android version, lighting, surface materials, capture duration, accepted/rejected/dropped frame counts, and any ARCore install/update prompts.
