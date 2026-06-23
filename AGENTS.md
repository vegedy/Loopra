# Loopra — Agent Working Notes

## Projekt
Brainfuck-Studio App für Android. Retro-modern, lokal, gamifiziert.

## Erledigt

### Phase 1 — Demo-Bereinigung + NavGraph
- [x] FirstFragment.kt gelöscht
- [x] SecondFragment.kt gelöscht
- [x] fragment_first.xml gelöscht
- [x] fragment_second.xml gelöscht
- [x] nav_graph.xml neu geschrieben (8 Ziele: Library, Editor, Debug, ChallengeList, ChallengeDetail, Profile, Settings, Splash)
- [x] strings.xml bereinigt (kein Lorem Ipsum mehr)
- [x] colors.xml erweitert (retro-modernes Palette)
- [x] themes.xml angepasst (dunkles Default-Theme)
- [x] menu_main.xml erweitert
- [x] FAB-Icon und -Listener angepasst

### Phase 2 — Ressourcen + Build
- [x] Gradle-Dependencies: Room, Lifecycle/ViewModel, Coroutines
- [x] libs.versions.toml erweitert
- [x] Package-Struktur angelegt

### Phase 3 — Brainfuck Core
- [x] BrainfuckParser (String → List<Instruction>)
- [x] BrainfuckInterpreter (sequentiell, maxSteps, Timeout)
- [x] BrainfuckDebugger (Step, Breakpoints, State)
- [x] Unit-Tests für Core

### Phase 4 — Datenhaltung
- [x] Room-Datenbank + Entities + DAOs (Script, Challenge, ChallengeProgress, UserProgress)
- [x] Repositories (Script, Challenge, Progress)

### Phase 5 — UI Screens
- [x] ScriptLibraryFragment + ViewModel + Layout
- [x] EditorFragment + ViewModel mit Run-Output + Autosave
- [x] DebugFragment + ViewModel + Tape-Ansicht (horizontal RecyclerView)
- [x] ChallengeListFragment + ViewModel + Adapter
- [x] ChallengeDetailFragment + ViewModel + Test-Auswertung
- [x] ProfileFragment + ViewModel + Gamification-Display (Level, XP, Streak, Badges)
- [x] SettingsFragment + ViewModel (MaxSteps, TapeSize, Clear Data, About)
- [x] Navigation verdrahtet

### Phase 6 — Gamification
- [x] XP + Level (200 XP / Level)
- [x] Sterne-System (≤optimal=Gold, ≤good=Silber, sonst Bronze)
- [x] 8 Badges (First Script, Script Collector, Challenge Novice/Master, Debugger, On Fire, Unstoppable, Level 5)
- [x] Streak-Tracking
- [x] Stats (Scripts, Challenges, Steps)

### Phase 7 — Tablet + Finetuning
- [x] w600dp-Layouts: Editor (Code+Output nebeneinander)
- [x] w600dp-Layouts: Debug (Code+Tape+Output+Controls nebeneinander)
- [x] w600dp-Layouts: ChallengeDetail (Description+Code nebeneinander)
- [x] Launcher-Icon: BF-Tape-Symbol mit Neon-Akzenten

## Offene TODOs (nice-to-have)
- [ ] w1240dp-Layouts für große Tablets
- [ ] Splash-Screen
- [ ] Launcher-Icon als adaptive icon (monochrome)
- [ ] Eigene Schriftart (Retro-Monospace)
- [ ] Editor Syntax-Highlighting (einfach: BF-Befehle farbig)
- [ ] Undo/Redo im Editor
- [ ] Export/Import von Scripts
- [ ] Autocomplete für Klammern im Editor

## Architektur-Entscheidungen
- Single Activity + Navigation Component
- MVVM mit ViewModel + StateFlow
- Room für Persistenz
- Brainfuck Core in `domain/bf/` (kein Android-Import → testbar)
- Coroutine-basierte Interpreter-Ausführung mit Timeout
- Editor und Debugger als getrennte Screens (Phone: nacheinander; Tablet: nebeneinander)
- FAB für "Neues Script" auf Library-Screen
- Toolbar global in MainActivity

## Bekannte Probleme
- w600dp-Layouts mit RecyclerView brauchen `xmlns:app` (gefixed)
- keine weiteren bekannten Probleme

## Nächste Schritte (optional)
1. Splash-Screen und Animation
2. Eigene Fonts (z.B. JetBrains Mono)
3. Syntax-Highlighting im Editor
4. Undo/Redo
5. Export/Import
