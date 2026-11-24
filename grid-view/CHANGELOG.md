# Changelog

## 1.0.0 — 2026-07-07

First release. Developed against OmegaT 6.0.0 API, tested on OmegaT 6.0.1.

- Dockable Grid View panel with a lazy, read-through table model
  (segment number | source | target | status)
- Shows the document open in the editor (switches automatically with the
  editor via `onNewFile`); a gear-menu toggle switches to an all-files view
- Editable target column: commits through OmegaT's editor API
  (`gotoEntry` → `replaceEditText` → `commitAndLeave`)
- Two-way synchronization between the grid and the OmegaT editor
  (row click → `gotoEntry`; `onEntryActivated` → row highlight + autoscroll)
- Search box and All / Untranslated / Translated filter (`TableRowSorter`,
  `setSortsOnUpdates` for live filtering while translating)
- Pane gear menu: word wrap toggle, font size increase/decrease/reset;
  follows OmegaT's application font and font-change events
- Word wrap with per-row height growth for source/target columns;
  centered bold segment-number column
- First install: panel starts as a collapsed auto-hide tab at the bottom
  border (works around OmegaT's `ensureDockablesVisible`, which would
  otherwise force-dock the panel into an invisible zero-width split);
  saved layouts are respected on later starts
- Project lifecycle handling: populate on load, clear on close,
  live repaint on save/modification
