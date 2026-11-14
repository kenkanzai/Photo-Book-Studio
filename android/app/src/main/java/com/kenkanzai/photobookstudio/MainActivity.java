package com.kenkanzai.photobookstudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.webkit.WebViewAssetLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Photobook Studio for Android.
 * Shows the same index.html as the website, from inside the app (works offline).
 * "Add photos" opens the phone's photo picker directly (no "Choose an action" menu).
 * Backups and PDFs open the phone's own Save window, so you choose where the file goes.
 */
public class MainActivity extends ComponentActivity {

    private static final String HOST = "appassets.androidplatform.net";
    private static final String START = "https://" + HOST + "/assets/www/index.html";

    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private final Handler ui = new Handler(Looper.getMainLooper());

    // File being saved: the page sends it in pieces, then the Save window opens.
    private File saveTemp;
    private FileOutputStream saveOut;
    private String saveName;

    // Photos: the phone's photo picker, straight to the photos.
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMany =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(),
                    uris -> deliver(uris == null || uris.isEmpty() ? null : uris.toArray(new Uri[0])));
    private final ActivityResultLauncher<PickVisualMediaRequest> pickOne =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                    uri -> deliver(uri == null ? null : new Uri[]{uri}));
    // Backup files: the phone's file browser.
    private final ActivityResultLauncher<String[]> openDoc =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                    uri -> deliver(uri == null ? null : new Uri[]{uri}));
    // Saving: the phone's Save window.
    private final ActivityResultLauncher<String> createPdf =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/pdf"), this::writeSaved);
    private final ActivityResultLauncher<String> createBin =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/octet-stream"), this::writeSaved);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if (HOST.equals(u.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                boolean images = false;
                String[] types = params.getAcceptTypes();
                if (types != null) for (String t : types) if (t != null && t.startsWith("image")) images = true;
                try {
                    if (images) {
                        PickVisualMediaRequest req = new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build();
                        if (params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) pickMany.launch(req);
                        else pickOne.launch(req);
                    } else {
                        openDoc.launch(new String[]{"*/*"});
                    }
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });

        web.addJavascriptInterface(new AndroidFiles(), "AndroidFiles");

        // Back button: close the open window in the app, else back to the shelf, else leave the app.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                web.evaluateJavascript("(window.__appBack&&window.__appBack())?1:0", r -> {
                    if (!"1".equals(r)) {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true);
                    }
                });
            }
        });

        if (savedInstanceState == null) web.loadUrl(START);
        else web.restoreState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    private void deliver(Uri[] result) {
        if (fileCallback != null) {
            fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }

    private void done(int code) {
        ui.post(() -> web.evaluateJavascript("window.__afDone&&window.__afDone(" + code + ")", null));
    }

    private void writeSaved(Uri uri) {
        if (uri == null) { done(0); if (saveTemp != null) saveTemp.delete(); return; }
        final File src = saveTemp;
        new Thread(() -> {
            try (InputStream in = new FileInputStream(src);
                 OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out == null) throw new Exception("no output");
                byte[] buf = new byte[65536];
                int n;
                while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
                done(1);
            } catch (Exception e) {
                done(-1);
            } finally {
                src.delete();
            }
        }).start();
    }

    /** Called from the page: begin(name, type), append(piece) as many times as needed, end(). */
    private class AndroidFiles {
        @JavascriptInterface
        public void begin(String name, String mime) {
            try {
                if (saveOut != null) saveOut.close();
            } catch (Exception ignored) { }
            try {
                saveTemp = new File(getCacheDir(), "save.tmp");
                saveOut = new FileOutputStream(saveTemp);
                saveName = name;
            } catch (Exception e) {
                saveOut = null;
            }
        }

        @JavascriptInterface
        public boolean append(String base64) {
            if (saveOut == null) return false;
            try {
                saveOut.write(Base64.decode(base64, Base64.DEFAULT));
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @JavascriptInterface
        public void end() {
            try {
                if (saveOut == null) throw new Exception("not started");
                saveOut.close();
                saveOut = null;
            } catch (Exception e) {
                done(-1);
                return;
            }
            final String name = saveName == null ? "photobook" : saveName;
            ui.post(() -> {
                try {
                    if (name.toLowerCase().endsWith(".pdf")) createPdf.launch(name);
                    else createBin.launch(name);
                } catch (Exception e) {
                    done(-1);
                }
            });
        }
    }
}
