# OmegaT Grid View Plugin

A dockable two-column (source | target) grid view for [OmegaT](https://omegat.org),
inspired by the grid editors of memoQ/Trados. The grid stays in sync with the
OmegaT editor in both directions, and the target column is directly editable —
all edits are committed through OmegaT's own editor API, so translation memory,
statistics and autosave behave exactly as if you had typed in the editor pane.

This is an independent, unofficial plugin. It is not made or endorsed by
the OmegaT project.

*Türkçe açıklama aşağıdadır.*

## Features

- Dockable **Grid View** panel: segment number | source | target | status
  (translated / untranslated / active)
- Shows the **document open in the editor** and follows file switches;
  an all-files view is available from the gear menu
- **Editable target column** — double-click a cell, type, press Enter;
  the change is committed through `IEditor` (gotoEntry → replaceEditText → commit)
- **Two-way synchronization**: click a row to jump the editor to that segment;
  the grid follows the editor's active segment and scrolls it into view
- **Search box and filter** (All / Untranslated / Translated) backed by
  `TableRowSorter`, with live row updates as you translate
- **Pane settings menu** (gear icon): word wrap on/off, font size adjustment;
  the grid follows OmegaT's application font by default
- Lazy table model over `IProject.getAllEntries()` — no per-segment copies,
  scales to projects with tens of thousands of segments
- On first install the panel appears as a collapsed auto-hide tab at the
  bottom border; drag it wherever you like, the position is remembered

## Requirements

- OmegaT 6.0 or 6.1 (tested on 6.0.1 and 6.1.1).
  Earlier versions have not been tested.

## Installation

Download `omegat-gridview-plugin-<version>.jar` from the
[latest release](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest)
and copy it into your OmegaT plugins folder:

- Windows: `%APPDATA%\OmegaT\plugins`
- Linux / macOS: `~/.omegat/plugins`

Restart OmegaT. The **Grid View** tab appears at the bottom border of the
main window.

## Building from source

```
./gradlew jar
```

The JAR is produced in `build/libs/`. JDK 17+ is required to run the build
(the plugin bytecode targets Java 11, the minimum for OmegaT 6.x).

## Releasing

1. Set the new `version` in `build.gradle.kts`.
2. Add a `## <version>` section to `CHANGELOG.md`.
3. Commit, then push a tag named `v<version>`, e.g. `git tag v1.0.2` and
   `git push origin v1.0.2`.

The Release workflow builds the JAR and publishes a GitHub release with it,
using that CHANGELOG section as the release notes. It stops if the tag does
not match the version in `build.gradle.kts`.

## Known limitations

- The in-grid cell editor is plain text: OmegaT tags in tag-heavy segments
  must be maintained by hand (same rules as the editor pane, but without
  tag colouring). Prefer the editor pane for heavily tagged segments.
- When a row drops out of a filtered view, a neighbouring row may temporarily
  inherit its wrapped height; heights normalize on the next filter, font or
  wrap change.
- The panel-specific font size adjustment is per-session (not persisted).

## License

GNU General Public License v3. See [COPYING](COPYING).

---

# OmegaT Izgara Görünümü Eklentisi (Türkçe)

OmegaT için, memoQ/Trados'taki ızgara editörlerinden esinlenen, yerleştirilebilir
iki sütunlu (kaynak | hedef) bir panel. Izgara, OmegaT editörüyle iki yönlü
senkronize çalışır; hedef sütunu doğrudan düzenlenebilir — tüm değişiklikler
OmegaT'nin kendi editör API'si üzerinden kaydedilir (çeviri belleği,
istatistikler ve otomatik kayıt normal şekilde işler).

Bu bağımsız, resmi olmayan bir eklentidir. OmegaT projesi tarafından
yapılmamış veya onaylanmamıştır.

## Özellikler

- Yerleştirilebilir **Grid View** paneli: segment no | kaynak | hedef | durum
  (Çevrildi / Çevrilmedi / Aktif)
- **Editörde açık belgeyi** gösterir, dosya geçişlerini otomatik izler;
  dişli menüsünden tüm dosyaları birleşik gösterme seçeneği vardır
- **Düzenlenebilir hedef sütunu** — hücreye çift tıkla, yaz, Enter'a bas
- **Çift yönlü senkronizasyon**: satıra tıklayınca editör o segmente atlar;
  editörde segment değişince ızgara aktif satırı seçip görünür alana kaydırır
- **Arama kutusu ve filtre** (Tümü / Çevrilmemiş / Çevrilmiş); çeviri
  yaptıkça satırlar filtreye göre canlı güncellenir
- **Dişli menüsü**: satır kaydırma aç/kapa, yazı boyutu büyüt/küçült;
  panel varsayılan olarak OmegaT'nin uygulama yazı tipini kullanır
- Tembel (lazy) tablo modeli — on binlerce segmentli projelerde bile
  segment kopyalamaz
- İlk kurulumda panel alt kenarda küçültülmüş sekme olarak belirir;
  istediğiniz yere sürükleyin, konum hatırlanır

## Gereksinimler

- OmegaT 6.0 veya 6.1 (6.0.1 ve 6.1.1 üzerinde test edildi).
  Daha eski sürümler test edilmedi.

## Kurulum

`omegat-gridview-plugin-<sürüm>.jar` dosyasını
[son sürüm](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest)
sayfasından indirip OmegaT eklenti klasörüne kopyalayın:

- Windows: `%APPDATA%\OmegaT\plugins`
- Linux / macOS: `~/.omegat/plugins`

OmegaT'yi yeniden başlatın; **Grid View** sekmesi ana pencerenin alt
kenarında görünür.

## Bilinen sınırlar

- Hücre editörü düz metindir: etiket (tag) ağırlıklı segmentlerde etiketleri
  elle korumanız gerekir; bu tür segmentler için editör paneli daha güvenlidir.
- Filtreli görünümde satır düştüğünde komşu satır geçici olarak onun satır
  yüksekliğini devralabilir; ilk filtre/yazı tipi değişikliğinde düzelir.
- Panele özel yazı boyutu ayarı oturumluktur (kalıcı değildir).

## Lisans

GNU Genel Kamu Lisansı v3. Bkz. [COPYING](COPYING).
