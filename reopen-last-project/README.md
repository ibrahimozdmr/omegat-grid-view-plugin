# OmegaT Reopen Last Project Plugin

Automatically reopens the most recently used OmegaT project on startup,
if no project was specified on the command line and none is already open.

*Türkçe açıklama aşağıdadır.*

## Behavior

- On startup, if no project is already loading (e.g. from a command-line
  argument) and the feature is enabled, the plugin looks at OmegaT's
  built-in "recent projects" list and reopens the most recent entry.
- If that project's folder no longer exists (moved or deleted), nothing
  happens — OmegaT's normal empty/"First Steps" startup screen is shown,
  no error dialog.
- Adds a **"Son Proje"** tab under **Preferences > Plugins** with a single
  checkbox to turn the behavior on or off. Enabled by default.

## Requirements

- OmegaT 6.0.x (developed and tested on 6.0.1)

## Installation

Copy `omegat-reopen-last-project-plugin-<version>.jar` into your OmegaT
plugins folder:

- Windows: `%APPDATA%\OmegaT\plugins`
- Linux / macOS: `~/.omegat/plugins`

Restart OmegaT.

## Building from source

```
./gradlew :reopen-last-project:jar
```

## License

GNU General Public License v3. See [COPYING](COPYING).

---

# OmegaT Son Projeyi Aç Eklentisi (Türkçe)

Uygulama açılışında, komut satırından bir proje belirtilmediyse ve henüz
açık bir proje yoksa, en son kullanılan OmegaT projesini otomatik açar.

## Davranış

- Açılışta, zaten yüklenmekte olan bir proje yoksa (örn. komut satırından
  verilmediyse) ve özellik etkinse, OmegaT'nin yerleşik "son projeler"
  listesindeki en güncel projeyi otomatik açar.
- O projenin klasörü artık mevcut değilse (taşınmış/silinmişse) hiçbir şey
  yapılmaz — OmegaT'nin normal boş/"First Steps" açılış ekranı görünür,
  hata penceresi çıkmaz.
- **Tercihler > Eklentiler** altına **"Son Proje"** sekmesi ekler; tek bir
  onay kutusuyla özelliği açıp kapatabilirsiniz. Varsayılan: açık.

## Kurulum

`omegat-reopen-last-project-plugin-<sürüm>.jar` dosyasını OmegaT eklenti
klasörüne kopyalayın:

- Windows: `%APPDATA%\OmegaT\plugins`
- Linux / macOS: `~/.omegat/plugins`

OmegaT'yi yeniden başlatın.

## Lisans

GNU Genel Kamu Lisansı v3. Bkz. [COPYING](COPYING).
