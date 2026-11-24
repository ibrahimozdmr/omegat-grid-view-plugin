# Changelog

## 1.0.0 — 2026-07-07

First release. Developed against OmegaT 6.0.0 API, tested on OmegaT 6.0.1.

- Reopens the most recent project from OmegaT's `RecentProjects` list on
  startup, unless a project is already open (e.g. from the command line)
  or the project's folder no longer contains a valid `omegat.project` file
- New "Son Proje" tab under Preferences > Plugins with an on/off checkbox
  (`PreferencesControllers.addSupplier`), enabled by default
