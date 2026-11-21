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
- Page layouts (▦ window): Photo + caption, Full page, Across 2 pages, 2 stacked, 2 side by side, 1 big + 2, 4 grid, 4 collage, Collage, 6 grid, 6 collage, Centered photo, 2 scattered, 3 scattered, Title page, Place page, Travel route, Words only.
- Trip with stops: auto-adds a Travel route page and a Place page before each stop's photos.
- Text boxes belong to the page, not to a photo (page.boxes = [{id,title,sub,x,y,ax,ay,w,h,style,align,ts}]). Every page except the cover and Page 1 can have several. T (next to the page's ▦ layout button, towards the page middle) adds a new box and opens it for typing. A new box goes in the first empty white spot of the page, clear of the photos, other boxes and the ▦/T corners (newBox()); behind = Nothing, text dark (Auto), smaller text. On a Full page it goes bottom left (then other corners), behind = Soft shadow, white text. Boxes are drawn above the photos, stickers stay on top. Old books: the full-photo caption becomes the first box when the book opens (migrateBoxes()).
- Each box: drag it to move (stays inside the page); 8 handles like a Word text box: corners change width and height, sides change width, top/bottom change height; the text keeps its size and rewraps (letter size only via Text height). 'Fit to text' returns to auto size; behind it: soft shadow, dark band, light box or nothing.
- Caption bar: next to the selected text box (only the selected box shows its handles and bar) (under it, or above it when there is no room) there is a bar with two rows: Text alignment (Left, Centre, Right, Justify), Text height in mm (applies on Enter or leaving the box), Fit to text and ✕ Delete (Undo in the message; Delete or Backspace also deletes the selected box); then Behind: Soft shadow, Dark band, Light box, Nothing. None of these is in the side panel. Heading font and Text colour (side panel, Style) apply to all boxes of the page. Pressing its buttons while typing keeps the cursor in the box. On phones (≤520 px) its buttons are smaller so it stays in two rows. Text alignment for Page 1 quote, Words only, Photo + caption and the back cover. Empty = layout default (caption left; quote, Words only and back cover centred; Photo + caption left).
- Every page: text, font, text height, text colour, background, stickers (drag to move).
- Text height (cover title, pages, back cover, spine): a number box in mm = height of a capital letter of the main text (cover title, page heading, or the body when there is no heading). Other text on the page keeps its proportion. Stored as the old size factor (page.ts, spine.size); mm = factor × size at 100% × capital height of the font (measured, capR()) ÷ 3.937 px per mm (pages are 100 px per inch).
- Text never breaks a word or leaves its area: txLimit() finds the largest size that fits (whole words, block height inside the area) and the page is always drawn at no more than that. The box says the range that fits; typing more stops at the largest and says so. Smallest: 3 mm (spine 1.5 mm).
- Outside of the book: the cover screen shows Back | Spine | Front; tap a part (or the tabs) to edit it.
- Spine words: Name, Title, Place, Date — each ticked on/off, each copies the cover (title, country/name, year) until typed over; separator • · | or space. Plus font, size, colours, ✈ on/off, width in mm (empty = from page count).
- Front cover panel order: Cover photo → Words (title, region, year, title font, text size) → Colours (+ background) → Picture → Stickers.
- Cover style window: with a photo cover, 'Best with a photo' (Photo cover, Bold title, Poster, Title behind) shows first; styles short of photos say 'Needs N photos'.
- Back cover: colour, optional photo, words, small cover picture.
- PDF cover: full cover sheet (back + spine + front, one wide page) or front cover only.
- Sizes: Portrait 8×10 in, Landscape 10×7 in, Square 8×8 in.
- Save PDF: 300 dpi for printing, or a smaller file for sharing (150 dpi, no bleed, 1 file).
- For printing (made for photo printers): Bleed in mm (default 3): each PDF page is the trim size + bleed on every side; photos and edge bands that touch a cut edge reach into the bleed, the page background fills the rest (bleedBox(), bleedPart(), bleedFinish()). On the cover sheet only the outer edges get bleed (not between back, spine and front). Sheet width = 2 × page + spine + 2 × bleed.
- Files: 2 files (Title-cover.pdf and Title-inside.pdf, what photo printers usually ask for) or 1 file.
- Empty photo spots (emptyList()): the Save PDF window lists every page with empty photo spots (they print as grey), with the count per page, e.g. 'Page 15 (2)', up to 12 pages then 'And N more pages'. Tap a page to close the window and go there (Back cover opens the back).
- Sharpness check (lowResList()): before saving, lists photos under 200 dpi at their printed size, with the page, worst first (under 150 = blurry). dpi = saved full photo pixels (max 3000 px) ÷ printed inches, zoom included.
- Across 2 pages: one photo over a left + right page (layout 'spread' on the left page, 'spreadR' on the right). Pick it on either page of a spread (not page 1); the PDF prints each half on its own page. Drag the photo on either half to move the view.
- Page 1 is always the caption or quote page, on the right side (left = inside of front cover), like a real book. It cannot be deleted or moved; spreads after it are 2–3, 4–5…; the last spread can show the inside of back cover.
- Page strip: drag a page to move it (phone: hold, then drag); × deletes it.
- Odd page count (oddNote()): when adding a page, deleting a page or adding photos leaves an odd number of inside pages, the message adds 'Book now has N pages; printers need an even number.' After + Page it has an 'Add one more' button (adds another empty page right after; addEmptyPage()); after a delete the button stays Undo.
- Add an empty page: '+ Page after N' button next to the page label under the open book ('Pages 12–13 of 34'), always in view, so no scrolling the strip. N = the selected page (tap a page in the strip or on the book to change it); on the cover it adds after Page 1 (Page 1 never moves). The new page is a Full page. If locked pages are in the way it takes the next free spot (keepLocked()) and the message says which page it became. The old '+ Page' at the end of the strip is removed. Phone: label and button wrap to two lines when the label is long (cover).
- Undo / redo: ↶ ↷ in the top bar, or Ctrl+Z / Ctrl+Y. Up to 60 steps per open book.
- One photo, one place: a photo can only be on one spot.
- Collages: 4 collage, Collage (5) and 6 collage share one look: white frames, tilted and overlapping, on the page colour mixed with white (COLLAGES list). 6 collage is 2 columns × 3 rows on portrait pages, 3 × 2 on landscape and square.
- No duplicate photos: adding photos (new book or + Photo → Add photos) skips any photo already in the book, and the same photo picked twice (importFiles(), sigOf(), sigSame()). Each photo record keeps sig = its shape + a 16×16 colour print of its small copy, made from the original (so an enhanced or turned photo still matches). Same photo = same shape and nearly the same colours everywhere, so a re-saved or smaller copy is caught but a different shot is not. Older photos get their sig the first time it is needed (sigFor()). A message says how many were not added again. A photo removed with × in Choose a photo can be added again.
- Choose a photo window (openPicker()): a count line (N photos · N not used), then 3 groups: This box (the photo now in the box, green outline, tap keeps it), Not used yet (with Add photos; 'All photos are placed.' when none), Already used (faded, sorted by page, tag '⇄ Page N'). Picking a used photo swaps it with this box's photo (both keep their framing); if this box is empty it moves here and its old box is empty. Message with Undo after a swap or move.
- Swap on the page: hold a photo about half a second (mouse or finger, still), then drag it onto another box on the pages that show (swStart(), swOver(), swEnd()). The photo follows the pointer, the target box gets a white and green ring. The two photos trade places and keep their framing; an empty box just takes the photo. Drop outside a box or press Esc to cancel. Undo in the message. A quick drag still moves the view inside the frame; a tap still opens Choose a photo.
- Adding several photos from one box (+ Photo, then Add photos): the first goes in that box, then the page's other empty boxes; the rest get new pages right after this page (mixed layouts, placeNew()). A message says how many went where. If a 2-page photo comes later in the book, the new pages are an even number (one page split in two), or go at the end of the book so the pair stays together.
- Remove a photo: × on a photo in the Choose a photo window. It asks first, empties the box it was on, and can be undone (Undo in the message, or ↶). The photo stays on the device until the book is closed (book.trash), then it is deleted for good (purgeTrash() on leaving the book, opening it, or deleting the book). Deleting a page (× in the strip) deletes the page only; its photos stay in Choose a photo.
- Photo zoom: a zoom bar along the bottom of each photo on the page (− slider + and the value, Fit to 3×, − and + step 0.25, stopping at 1× on the way). 1× fills the frame (crops what does not fit). Below 1× down to Fit shows more of the photo; at Fit the whole photo shows. The photo box shrinks to the photo's shape, centred in its frame, and the page background shows around it; on scattered and collage pages the white frame shrinks with it (shrinkTo(), zEff()). Fit = the frame's own limit, so a photo never gets smaller than its frame in both directions. Zoom below 1× is saved as slot.zo with slot.z = 1 (setZ(), zOf()), so an older copy of the app shows those photos at 1×. Across 2 pages stays 1× to 3×. Mouse: it shows on the photo under the pointer and hides when the pointer leaves. Touch: it shows while touching or dragging the photo and hides 4 seconds later (a tap still opens Choose a photo). It turns with tilted photos and moves above the caption box and its bar when they would overlap. Narrow photos hide the value (<150 px) and the − + (<120 px); very small photos (<90×60 px) get no bar. Pinch a photo with two fingers to zoom it inside its frame, the same value as the zoom bar. One finger drags to choose the part that shows. On a laptop, trackpad pinch or Ctrl + mouse wheel on a photo does the same. Inside the page area the browser's own zoom is blocked (stage touch-action: pan-y, Ctrl+wheel prevented), so only the photo zooms.
- Layouts: ▦ button on each page corner. Photos: tap to change (an empty box: tap to add), drag to move the view, hold then drag to swap, pinch to zoom.
- On each photo: ✦ Edit photo on the top-right and ✕ Take off the page on the top-left, apart so ✦ is not hit by mistake (the photo stays in Choose a photo; Undo in the message). When the page's corner buttons (▦ T lock) would touch them, ✦ and ✕ move down below them; on a short photo ✕ goes left of ✦.
- Side panel, photos: no photo controls (zoom is on each photo), only Photo size for the photo cover. No Change, + Photo, focus picture or Duplicate page: the page does those.
- Text box lines: Caption and 'Place or date' (the small line under the caption, spaced capitals, about a third of the caption height), typed straight in the text box on the page, like Word. T button adds a new box; or click the box once to select it, then click again to type (a press anywhere else unselects it). While typing, empty lines show hints ('Type a caption', 'Place or date'); Enter goes from the caption to the second line, Enter again, Esc or clicking outside finishes; a box with both lines empty is removed. No Words group in the side panel on full-photo pages. (S.capSelId = selected box id, CAPEDIT = box being typed; pageHTML draws the hints only on the editor page view, never in thumbnails or the PDF.)
- Phone: upright shows one page at a time (big enough to edit); turned sideways shows two pages like the open book (onePage(): narrow screen and portrait orientation). Sideways, the page view fills the screen height so the whole spread shows.
- Book view: the editor shows pages as an open book (cover as a closed book / flat Back | Spine | Front). Preview opens a 3D book you can turn, and pages turn like a real book.
- Page turn in the editor: ‹ › turn the page like a book (stageFlip()). Open book: › lifts the right page and turns it over to the left (its back is the next left page); ‹ turns the left page back to the right. Phone upright: the page turns away (›) or comes back (‹). Cover ↔ Page 1 just changes the view (no turn). Pressing again during a turn finishes it at once. Off when the device asks for reduced motion. Arrow keys and the page strip still jump without the turn.
- Book name: rename on the shelf (pencil next to the name) or in Book settings. Only the shelf name changes; the cover title stays.

## Lock
- Page lock: an open padlock next to T on every page (and next to ▦ on the front cover and Page 1). Tap to lock; a locked page shows only a green padlock in the corner (tap it to unlock). Stored as page.locked. The cover lock (pages[0].locked) covers back, spine and front. A photo across 2 pages is locked when either page is (lockOwner()).
- Book lock: Book settings → Lock book (book.locked). Every page is locked; + Page, undo/redo, book size, book type and Enhance all are blocked until it is unlocked (Book settings, side panel Unlock, or Unlock in the message).
- On a locked page: no ▦, T, ✦, ✕ or zoom bar; no photo drag, hold-to-swap, pinch, Ctrl+wheel zoom, Choose a photo, text box move/resize/delete, sticker move/delete or Delete key. Another photo cannot be dropped on it. A tap says "Page N is locked" with Unlock (lockToast()).
- Side panel: lock note with Unlock at the top; the controls below are faded and cannot be used (div.lk-body inert, setInsp()).
- Strip: small padlock next to the page number; a locked page cannot be dragged; × asks first.
- A locked page keeps its page number and side (keepLocked()). Adding, deleting or moving other pages flows them around it: a new page added just before it goes after it, and the page after it moves up to fill a deleted page's place. Both halves of a locked 2-page photo stay put. If a step would move a locked page (e.g. it is the last page and a page before it is deleted) or split a 2-page photo, it is not done and a message says why; + Page and Add photos then put the new pages at the end instead. Undo/redo never moves a locked page either.
- Choose a photo: photos on locked pages show a padlock tag and cannot be swapped out or removed (onLocked()). Enhance all skips them.
- Undo/redo never changes or moves a locked page or book (lockBlock()). Locking/unlocking is not an undo step, and undo keeps the current locks (restoreSnap()).
- Preview, PDF and backups are not affected (locks travel in backups).

## Photo enhance
- ✦ button on the top-right of each photo on the page (moves below the page's corner buttons when they would touch; filled when the photo is enhanced, turned or flipped) opens the Edit photo window (called Enhance before; the section and code names stay 'enhance'): Before / After, looks Off, Auto, Vivid, Warm, Cool, B&W (on the device, offline, with Strength), and AI ✦ (online). Brightness and Contrast (−50 to +50) work with any look, Off and AI.
- Turn and flip (top row of the Edit photo window): ↺ Turn left, ↻ Turn right, ⇆ Flip (left to right), ⇅ Flip (upside down). Works with any look, Off and AI, offline. Saved into the photo like Enhance (orientC()): stored as enh.r (quarter turns to the right, 0–3) and enh.fh (mirrored; the mirror is applied first, then the turn). The original stays untouched; Off with no turn brings it back. A turned photo gets its new width and height, so frames, zoom, the sharpness check and the PDF follow it. On a photo that is not enhanced, the first turn or flip sets the look to Off (unless a look was already picked), so turning alone does not also enhance it.
- Framing after a turn (reframeTurn(), oriPt()): each frame showing the photo moves its view (slot.fx/fy) so the same part of the photo stays in view; the zoom stays the same.
- Undo after Keep: the message (Photo edited / Photo turned / Original photo back) has Undo for 8 seconds. It puts back the photo as it was before Keep (full, medium, small sizes and its settings) and the frames' view. ↶ in the top bar does not undo photo edits (it only covers the pages).
- Turn or flip alone does not count as enhanced (isEnh()): Enhance all still improves those photos and keeps the turn; Put all originals back keeps the turn too. ✦ title says 'Turned or flipped'.
- AI ✦: Google Gemini image editing (default model gemini-2.5-flash-image, changeable in AI settings). Optional extra request, e.g. "brighter sky". Try again gives a new version. The prompt asks for a realistic professional edit that keeps people, faces and details.
- AI settings (in the Edit photo window): the user's own Gemini key (from aistudio.google.com/apikey) and model name, kept on the device (localStorage 'pbs-ai'). The app never contains a key.
- If the AI result is under 2000 px on its long side, the window says a full-page print may look a little soft.
- Book settings → Enhance all photos: Auto (offline), AI (online, one by one, with Stop), or put all originals back. Already enhanced photos are skipped.
- Saving: the enhanced picture replaces the full, medium and small sizes (so the page view and PDF use it); the original stays in the 'full' record as orig. Off brings it back. Photo record has enh: {look,k,br,ct} or {ai:true,model,br,ct}; look 'off' with br/ct = brightness/contrast only. An AI result is kept before brightness/contrast in full.ai so they can be changed later (not in backups; after a restore, the adjusted photo becomes the new start). Backups include the originals.

## Privacy and security
- No outside connections: fonts (Anton, Archivo Black, Caveat, Cormorant Garamond, Figtree, Montserrat, Playfair Display — latin) and the PDF tools (html2canvas 1.4.1, jsPDF 2.5.1, MIT) are built into index.html. Works offline from the first visit.
- No account, no tracking. Books and photos stay on the device. The only upload is AI enhance: when the user taps it, that photo is sent to Google Gemini with the user's own key.
- Backups can be locked with a password: AES-GCM 256, key from PBKDF2-SHA256 (310,000 rounds), random salt and IV. File starts with "PBSLOCK1". A forgotten password cannot be recovered.
- "Saved ✓ / Saving… / Not saved" shows next to the book title. The app saves by itself after every change.
- Deleting a book can be undone for 10 seconds (Undo in the message).

## Theme
- Shelf → ◐ (next to Backup) opens Theme: Auto, Light, Dark. Auto follows the phone or computer setting. Light or Dark stays the same whatever the device is set to.
- Kept on each device (localStorage 'pbs-theme'; no value = Auto). Not in backups. Set before the page shows (small script in <head>), so there is no flash.
- Android app: the page calls AndroidFiles.setTheme(dark) so the phone's top and bottom bars match. Older APKs without it are skipped safely.

## Storage
- Books and photos stay on the device (browser IndexedDB). Nothing is uploaded.
- Each photo is saved 3 times: small (lists), medium (screen), full (PDF, up to 3000 px).
- Phone and laptop do not share books. Move books with Backup on the shelf (or Book settings → Save backup file for one book), then Backup → "Open backup file" on the other device.

## Backup and restore
- Shelf → Backup: "Back up all books" (one file, password optional) and "Open backup file".
- Book settings → Save backup file: one book only.
- All-books file: starts with "PBSMULTI1", then for each book an 8-byte size and that book's normal backup file (locked or not). File name: Photobook-backup-YYYY-MM-DD.pbook.
- Last backup reminder on the shelf, under the title (only when there are books): "Not backed up yet", or "Last backup N days ago. Some books changed since." with a Back up now button (shown in the warning colour); or a quiet "Last backup today. Nothing changed since." Time of the last successful "Back up all books" is kept on the device (localStorage 'pbs-lastbackup'); a book counts as changed when its updated time is newer. One-book backups and restores do not reset it.
- Restoring an all-books file skips books already on the shelf (same book, not newer). A locked file asks the password once.
- A restored book that is already on the shelf comes in as a copy with new photo ids, so the two books never share photos.

## Android app
- Built by GitHub Actions on every upload to main. Newest APK: Releases page → Photobook-Studio.apk (link: /releases/latest/download/Photobook-Studio.apk).
- The build copies index.html, the manifest and the icons into the app, so the app always matches the website.
- Add photos opens the phone's photo picker directly. Backups and PDFs open the phone's Save window (JS bridge window.AndroidFiles: begin, append, end; result in window.__afDone: 1 saved, 0 cancelled, -1 failed).
- Back button: closes the open window, then goes back to the shelf, then leaves the app (window.__appBack).
- Internet permission is only for AI enhance. Books stay on the phone; deleting the app deletes its books, so back up first.
- Signed with android/app/photobook.keystore so each new APK installs over the old one and keeps the books. Do not change or lose this file.


## Rules for updates
- Keep it one index.html with the fonts and PDF tools built in, and no outside requests except AI enhance (generativelanguage.googleapis.com, only when the user taps it).
- index.html must work both on the website and inside the Android app (check window.AndroidFiles where they differ).
- When changing sw.js or how files are cached, raise VERSION in sw.js (now pbs-v2 → next pbs-v3) so installed copies update.

## Design rule
- Keep the side panel clean: one control per job, no duplicates of what can be done on the page, short notes only.
