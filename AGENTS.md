# Loopra — Agent Working Notes

## Projekt
Brainfuck-Studio App für Android. Retro-modern, lokal, gamifiziert.

## Erledigt

### Phase 1 — Demo-Bereinigung + NavGraph (TODO abgeschlossen)
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
- [x] Room-Datenbank + Entities + DAOs
- [x] Repositories

### Phase 5 — UI Screens
- [x] ScriptLibraryFragment + ViewModel + Layout
- [x] EditorFragment + ViewModel + Layout
- [x] Navigation verdrahtet

## Offene TODOs

### Phase 2 (nächster Agentenlauf)
- [ ] DebugFragment + ViewModel + Tape-Ansicht
- [ ] ChallengeListFragment + ViewModel
- [ ] ChallengeDetailFragment + ViewModel + Test-Auswertung
- [ ] ProfileFragment + ViewModel + Gamification-Display
- [ ] SettingsFragment + ViewModel
- [ ] Gamification-Engine (XP, Badges, Streaks)
- [ ] Retro-modernes Finetuning (Drawables, Themes, Fonts)
- [ ] Tablet-Adaptive-Layouts (w600dp, w1240dp)
- [ ] Launcher-Icon ersetzen

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
- keine

## Nächste Schritte (Priorität)
1. DebugFragment mit Tape-Ansicht
2. Challenges in DB seeden + ChallengeList/Detail
3. Gamification-Layer
4. Profile/Settings
5. Tablet-Layouts
6. Visuelles Finetuning
