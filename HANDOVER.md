# Photobook Studio — Handover

## What it is
A single-file photobook app (index.html). Installable on phone and laptop from GitHub Pages. Works fully offline, even on the first visit.

## Files
- index.html — the whole app (UI, page designs, PDF export)
- manifest.webmanifest, sw.js — install and offline support
- icon-192.png, icon-512.png, icon-maskable.png — app icons
- android/ — the Android app (a window that shows index.html from inside the app)
- .github/workflows/android.yml — builds the Android app on GitHub after every upload

## How it works
- Book types: Trip, Birthday, Anniversary, Something else.
- New book: step 1 details and photos, step 2 cover (8 styles, 10 colour sets, 13 illustrations, 6 fonts).
- Page layouts (▦ window): Photo + caption, Full page, Across 2 pages, 2 stacked, 2 side by side, 1 big + 2, 4 grid, Collage, 6 grid, Centered photo, 2 scattered, 3 scattered, Title page, Place page, Travel route, Words only.
- Trip with stops: auto-adds a Travel route page and a Place page before each stop's photos.
- Full-photo caption is a text box: drag it to move (stays inside the page); 8 handles like a Word text box: corners change width and height, sides change width, top/bottom change height; the text keeps its size and rewraps (letter size only via Text size). 'Fit to text' returns to auto size; behind it: soft shadow, dark band, light box or nothing.
- Text alignment (Left, Centre, Right, Justify) on the caption, Page 1 quote, Words only, Photo + caption and the back cover. Empty = layout default (caption left; quote, Words only and back cover centred; Photo + caption left).
- Every page: text, font, text size, text colour, background, stickers (drag to move).
- Outside of the book: the cover screen shows Back | Spine | Front; tap a part (or the tabs) to edit it.
- Spine words: Name, Title, Place, Date — each ticked on/off, each copies the cover (title, country/name, year) until typed over; separator • · | or space. Plus font, size, colours, ✈ on/off, width in mm (empty = from page count).
- Front cover panel order: Cover photo → Words (title, region, year, title font, text size) → Colours (+ background) → Picture → Stickers.
- Cover style window: with a photo cover, 'Best with a photo' (Photo cover, Bold title, Poster, Title behind) shows first; styles short of photos say 'Needs N photos'.
- Back cover: colour, optional photo, words, small cover picture.
- PDF cover: full cover sheet (back + spine + front, one wide page) or front cover only.
- Sizes: Portrait 8×10 in, Landscape 10×7 in, Square 8×8 in.
- Save PDF: 300 dpi for printing, or a smaller file for sharing.
- Across 2 pages: one photo over a left + right page (layout 'spread' on the left page, 'spreadR' on the right). Pick it on either page of a spread (not page 1); the PDF prints each half on its own page. Drag the photo on either half to move the view.
- Page 1 is always the caption or quote page, on the right side (left = inside of front cover), like a real book. It cannot be deleted or moved; spreads after it are 2–3, 4–5…; the last spread can show the inside of back cover.
- Page strip: drag a page to move it (phone: hold, then drag); × deletes it.
- Undo / redo: ↶ ↷ in the top bar, or Ctrl+Z / Ctrl+Y. Up to 60 steps per open book.
- One photo, one place: a photo can only be on one spot; picking a used photo moves it.
- Layouts: ▦ button on each page corner. Photos: tap to change, drag to move the view.
- Book view: the editor shows pages as an open book (cover as a closed book / flat Back | Spine | Front). Preview opens a 3D book you can turn, and pages turn like a real book.
- Book name: rename on the shelf (pencil next to the name) or in Book settings. Only the shelf name changes; the cover title stays.

## Privacy and security
- No outside connections: fonts (Anton, Archivo Black, Caveat, Cormorant Garamond, Figtree, Montserrat, Playfair Display — latin) and the PDF tools (html2canvas 1.4.1, jsPDF 2.5.1, MIT) are built into index.html. Works offline from the first visit.
- No account, no tracking, no uploads. Books and photos stay in the browser on the device.
- Backups can be locked with a password: AES-GCM 256, key from PBKDF2-SHA256 (310,000 rounds), random salt and IV. File starts with "PBSLOCK1". A forgotten password cannot be recovered.
- "Saved ✓ / Saving… / Not saved" shows next to the book title. The app saves by itself after every change.
- Deleting a book can be undone for 10 seconds (Undo in the message).

## Storage
- Books and photos stay on the device (browser IndexedDB). Nothing is uploaded.
- Each photo is saved 3 times: small (lists), medium (screen), full (PDF, up to 3000 px).
- Phone and laptop do not share books. Move books with Backup on the shelf (or Book settings → Save backup file for one book), then Backup → "Open backup file" on the other device.

## Backup and restore
- Shelf → Backup: "Back up all books" (one file, password optional) and "Open backup file".
- Book settings → Save backup file: one book only.
- All-books file: starts with "PBSMULTI1", then for each book an 8-byte size and that book's normal backup file (locked or not). File name: Photobook-backup-YYYY-MM-DD.pbook.
- Restoring an all-books file skips books already on the shelf (same book, not newer). A locked file asks the password once.
- A restored book that is already on the shelf comes in as a copy with new photo ids, so the two books never share photos.

## Android app
- Built by GitHub Actions on every upload to main. Newest APK: Releases page → Photobook-Studio.apk (link: /releases/latest/download/Photobook-Studio.apk).
- The build copies index.html, the manifest and the icons into the app, so the app always matches the website.
- Add photos opens the phone's photo picker directly. Backups and PDFs open the phone's Save window (JS bridge window.AndroidFiles: begin, append, end; result in window.__afDone: 1 saved, 0 cancelled, -1 failed).
- Back button: closes the open window, then goes back to the shelf, then leaves the app (window.__appBack).
- No internet permission. Books stay on the phone; deleting the app deletes its books, so back up first.
- Signed with android/app/photobook.keystore so each new APK installs over the old one and keeps the books. Do not change or lose this file.


## Rules for updates
- Keep it one index.html with the fonts and PDF tools built in, and no outside requests.
- index.html must work both on the website and inside the Android app (check window.AndroidFiles where they differ).
- When changing sw.js or how files are cached, raise VERSION in sw.js (now pbs-v2 → next pbs-v3) so installed copies update.

## Design rule
- Keep the side panel clean: one control per job, no duplicates of what can be done on the page, short notes only.
