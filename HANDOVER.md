# Photobook Studio — Handover

## What it is
A single-file photobook app (index.html). Installable on phone and laptop from GitHub Pages. Works offline after the first visit.

## Files
- index.html — the whole app (UI, page designs, PDF export)
- manifest.webmanifest, sw.js — install and offline support
- icon-192.png, icon-512.png, icon-maskable.png — app icons

## How it works
- Book types: Trip, Birthday, Anniversary, Something else.
- New book: step 1 details and photos, step 2 cover (8 styles, 10 colour sets, 13 illustrations, 6 fonts).
- Page layouts: Full photo, Two, Three, Grid of 4, Collage, Photo + words, Title page, Place page, Travel route, Words only.
- Trip with stops: auto-adds a Travel route page and a Place page before each stop's photos.
- Full-photo caption is a text box: drag it to move (stays inside the page); 8 handles like a Word text box: corners change width and height, sides change width, top/bottom change height; the text keeps its size and rewraps (letter size only via Text size). 'Fit to text' returns to auto size; behind it: soft shadow, dark band, light box or nothing.
- Text alignment (Left, Centre, Right, Justify) on the caption, Page 1 quote, Words only, Photo + caption and the back cover. Empty = layout default (caption follows its position).
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
- Phone and laptop do not share books. Move a book with Book settings → Save backup file, then "Open backup" on the other device.

## Rules for updates
- Keep it one index.html with the fonts and PDF tools built in, and no outside requests.
- When changing sw.js or the app, raise VERSION in sw.js (pbs-v1 → pbs-v2) so installed copies update.

## Design rule
- Keep the side panel clean: one control per job, no duplicates of what can be done on the page, short notes only.
