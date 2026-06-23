# Loopra — Brainfuck Studio for Android

A retro-modern, local-first, gamified Brainfuck development environment for Android phones and tablets.

## Features

- **Script Library** — create, edit, delete, and organize your Brainfuck scripts with autosave
- **Code Editor** — full-screen code input with monospace font and instant execution
- **Run & Output** — execute scripts with configurable step limits and timeout protection
- **Interactive Debugger** — step through code instruction by instruction, set breakpoints, inspect tape cells with data pointer highlighting
- **Tape/Memory View** — horizontal scrolling grid showing cell index, decimal value, and ASCII representation
- **Challenges** — 5 built-in challenges (Easy to Hard) with automatic test evaluation and star ratings (Bronze/Silver/Gold)
- **Gamification** — XP and level system, 8 achievements/badges, daily streak tracking, and detailed statistics
- **Profile** — level badge, XP progress bar, streak counter, stats overview, and badge collection grid
- **Settings** — configurable interpreter limits (max steps, tape size), data reset
- **Tablet Support** — adaptive dual-pane layouts for Editor, Debugger, and Challenge screens on ≥600dp screens
- **Retro-Modern Design** — dark theme with neon cyan/magenta/green/amber accents, terminal-inspired output area

## Screens

| Screen | Description |
|---|---|
| **Library** | List of saved scripts with FAB to create new |
| **Editor** | Code input with run button and live output |
| **Debugger** | Step debugger with tape/memory view and controls |
| **Challenges** | Curated tasks sorted by difficulty |
| **Challenge Detail** | Task description, code editor, and test runner |
| **Profile** | Level, XP, streak, stats, and badges |
| **Settings** | Interpreter configuration and data management |

## Tech Stack

- **Language:** Kotlin
- **UI:** XML/Views, Material 3, ConstraintLayout
- **Architecture:** Single Activity + MVVM + Navigation Component
- **Persistence:** Room (SQLite) with Flow-based reactive queries
- **Async:** Coroutines + StateFlow
- **Build:** Gradle 8.6, AGP 8.4.0
- **Min SDK:** 24 · **Target SDK:** 34

## Architecture

```
app/
├── ui/              # Fragment + ViewModel per screen
│   ├── library/
│   ├── editor/
│   ├── debug/
│   ├── challenges/
│   ├── profile/
│   └── settings/
├── domain/bf/       # Pure Kotlin, no Android deps
│   ├── BfParser.kt      # Char stream → Instruction list
│   ├── BfInterpreter.kt # Sequential execution with timeout
│   └── BfDebugger.kt    # Step mode + breakpoints
├── data/
│   ├── model/       # Room entities
│   ├── db/          # DAOs + AppDatabase
│   └── repository/  # Data access layer
└── MainActivity.kt  # Toolbar, FAB, global nav
```

## Brainfuck Language

Loopra implements strict Brainfuck with 8 commands:

| Command | Meaning |
|---|---|
| `>` | Move data pointer right |
| `<` | Move data pointer left |
| `+` | Increment current cell |
| `-` | Decrement current cell |
| `.` | Output current cell as ASCII |
| `,` | Input from buffer |
| `[` | Jump past `]` if cell is 0 |
| `]` | Jump back to `[` if cell is non-zero |

All other characters are treated as comments and ignored. The interpreter enforces:
- 30,000-cell tape (configurable)
- 100,000 step limit (configurable)
- 5-second execution timeout
- Byte-sized cells (0–255, wrapping)

## Building

```bash
git clone https://github.com/your-username/loopra.git
cd loopra
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Testing

```bash
./gradlew testDebugUnitTest
```

21 unit tests cover the Brainfuck parser, interpreter, and debugger.

## License

MIT
