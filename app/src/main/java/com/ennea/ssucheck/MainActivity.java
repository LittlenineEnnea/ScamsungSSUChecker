package com.ennea.ssucheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "settings";
    private static final String PREF_LANG = "language";

    // Carrier IDs that are always globally unlocked
    private static final Set<String> GLOBAL_CARRIERS = new HashSet<>(Arrays.asList(
            "XAA", "CHM", "CHN", "CHC"
    ));

    // Chips that do not support SSU prop detection
    private static final Set<String> UNSUPPORTED_CHIPS = new HashSet<>(Arrays.asList(
            "SM8350", "SM8450", "SM8550"
    ));

    // ATT model prefixes that predate S26 (no SSU mechanism)
    private static final Set<String> ATT_SSU_MODELS = new HashSet<>(Arrays.asList(
            "S931", "S936", "S937", "S938", "F766", "F966"
    ));

    private TextView tvModel, tvSoc, tvHardware, tvCarrier;
    private TextView tvSsuBig, tvSsuStatus, tvSsuSupport, tvSsuCount, tvSsuError, tvSsuWarning;
    private TextView tvKnoxStatus, tvKgState, tvKeystore;
    private TextView tvSectionDevice, tvSectionSsu, tvSectionKnox;
    private TextView tvFooter, tvLangLabel;
    private CheckBox cbRaw;
    private LinearLayout rawLayout, resultLayout;
    private TextView tvRaw, tvRawTitle;
    private String rawDump = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvModel        = findViewById(R.id.tv_model);
        tvSoc          = findViewById(R.id.tv_soc);
        tvHardware     = findViewById(R.id.tv_hardware);
        tvCarrier      = findViewById(R.id.tv_carrier);
        tvSsuBig       = findViewById(R.id.tv_ssu_big);
        tvSsuStatus    = findViewById(R.id.tv_ssu_status);
        tvSsuSupport   = findViewById(R.id.tv_ssu_support);
        tvSsuCount     = findViewById(R.id.tv_ssu_count);
        tvSsuError     = findViewById(R.id.tv_ssu_error);
        tvSsuWarning   = findViewById(R.id.tv_ssu_warning);
        tvKnoxStatus   = findViewById(R.id.tv_knox_status);
        tvKgState      = findViewById(R.id.tv_kg_state);
        tvKeystore     = findViewById(R.id.tv_keystore);
        tvSectionDevice= findViewById(R.id.tv_section_device);
        tvSectionSsu   = findViewById(R.id.tv_section_ssu);
        tvSectionKnox  = findViewById(R.id.tv_section_knox);
        tvFooter       = findViewById(R.id.tv_footer);
        tvLangLabel    = findViewById(R.id.tv_lang_label);
        cbRaw          = findViewById(R.id.cb_raw);
        rawLayout      = findViewById(R.id.raw_layout);
        tvRaw          = findViewById(R.id.tv_raw);
        tvRawTitle     = findViewById(R.id.tv_raw_title);
        resultLayout   = findViewById(R.id.result_layout);

        tvLangLabel.setText(getCurrentLang().equals("zh") ? "中文" : "EN");
        findViewById(R.id.btn_lang).setOnClickListener(v -> showLangPicker());
        findViewById(R.id.btn_check).setOnClickListener(v -> runChecks());
        findViewById(R.id.btn_guide).setOnClickListener(v -> showGuide());
        findViewById(R.id.btn_github).setOnClickListener(v ->
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/LittlenineEnnea/ScamsungSSUChecker"))));

        cbRaw.setOnCheckedChangeListener((btn, checked) -> {
            rawLayout.setVisibility(checked ? View.VISIBLE : View.GONE);
            if (checked) tvRaw.setText(rawDump.isEmpty() ? "(no props found)" : rawDump);
        });

        updateStringViews();
    }

    private void runChecks() {
        String model    = Build.MODEL + " (" + Build.DEVICE + ")";
        String soc      = getProp("ro.soc.model");
        String hardware = getProp("ro.hardware");
        String carrier  = getProp("ro.boot.carrierid");

        String ssuStatus  = getProp("ssu.status");
        String ssuSupport = getProp("ssu.support");
        String ssuCount   = getProp("ssu.count");
        String ssuError   = getProp("ssu.error");

        String knox    = getProp("ro.boot.em.status");
        String kg      = getProp("knox.kg.state");
        String keytype = getProp("ro.security.keystore.keytype");

        // Determine SSU display state
        setSsuIndicator(model, soc, hardware, carrier, ssuSupport);

        // Knox
        String knoxResult;
        if (knox.isEmpty()) {
            knoxResult = "N/A";
        } else if (knox.equals("0x0") || knox.equals("0")) {
            knoxResult = "✅ " + knox;
        } else {
            knoxResult = "⚠️ " + knox;
        }

        String kgResult = kg.isEmpty() ? "N/A" : kg;

        // Raw dump
        StringBuilder sb = new StringBuilder();
        appendRaw(sb, "ro.boot.carrierid",            carrier);
        appendRaw(sb, "ro.soc.model",                 soc);
        appendRaw(sb, "ro.hardware",                  hardware);
        appendRaw(sb, "ssu.status",                   ssuStatus);
        appendRaw(sb, "ssu.support",                  ssuSupport);
        appendRaw(sb, "ssu.count",                    ssuCount);
        appendRaw(sb, "ssu.error",                    ssuError);
        appendRaw(sb, "ro.boot.em.status",            knox);
        appendRaw(sb, "knox.kg.state",                kg);
        appendRaw(sb, "ro.security.keystore.keytype", keytype);
        rawDump = sb.toString().trim();

        tvModel.setText     ("Model:       " + model);
        tvSoc.setText       ("SoC:         " + orNA(soc));
        tvHardware.setText  ("Hardware:    " + orNA(hardware));
        tvCarrier.setText   ("Carrier ID:  " + orNA(carrier));
        tvSsuStatus.setText ("ssu.status:  " + orNA(ssuStatus));
        tvSsuSupport.setText("ssu.support: " + orNA(ssuSupport));
        tvSsuCount.setText  ("ssu.count:   " + orNA(ssuCount));
        tvSsuError.setText  ("ssu.error:   " + orNA(ssuError));
        tvKnoxStatus.setText("Knox Status: " + knoxResult);
        tvKgState.setText   ("KG State:    " + kgResult);
        tvKeystore.setText  ("Key Type:    " + orNA(keytype));

        cbRaw.setChecked(false);
        rawLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Determine what to show in the big SSU indicator area.
     * Priority order:
     * 1. Unsupported chip (SM8350/8450/8550) → 🤔 not supported
     * 2. ATT carrier + pre-S26 model + no ssu.support → 🤔 ATT not recognized
     * 3. Global carrier (XAA/CHM/CHN/CHC) → ✅ Global Unlocked
     * 4. ssu.support=1 → 🔒 SSU Locked
     * 5. else → ✅ Global Unlocked
     * + Exynos/MTK hardware warning shown separately
     */
    private void setSsuIndicator(String model, String soc, String hardware,
                                  String carrier, String ssuSupport) {
        // Check chip
        String socUpper = soc.toUpperCase();
        boolean isUnsupportedChip = UNSUPPORTED_CHIPS.contains(socUpper);

        // Check ATT pre-S26
        boolean isAtt = carrier.equalsIgnoreCase("ATT");
        boolean isPreS26AttModel = false;
        if (isAtt) {
            String modelUpper = model.toUpperCase().replace("-", "").replace(" ", "");
            for (String prefix : ATT_SSU_MODELS) {
                if (modelUpper.contains(prefix)) {
                    isPreS26AttModel = true;
                    break;
                }
            }
        }

        // Check non-Qcom hardware
        boolean isNonQcom = !hardware.equalsIgnoreCase("qcom") && !hardware.isEmpty();

        // Determine big indicator
        if (isUnsupportedChip) {
            tvSsuBig.setText(getString(R.string.ssu_not_supported));
            tvSsuBig.setTextColor(0xFFFFB300);
            tvSsuBig.setBackgroundColor(0x22FFB300);
        } else if (isAtt && isPreS26AttModel && ssuSupport.isEmpty()) {
            tvSsuBig.setText(getString(R.string.ssu_att_not_recognized));
            tvSsuBig.setTextColor(0xFFFFB300);
            tvSsuBig.setBackgroundColor(0x22FFB300);
        } else if (GLOBAL_CARRIERS.contains(carrier.toUpperCase())) {
            tvSsuBig.setText(getString(R.string.ssu_unlocked));
            tvSsuBig.setTextColor(0xFF3FB950);
            tvSsuBig.setBackgroundColor(0x00000000);
        } else if (ssuSupport.equals("1")) {
            tvSsuBig.setText(getString(R.string.ssu_locked));
            tvSsuBig.setTextColor(0xFFFF6B6B);
            tvSsuBig.setBackgroundColor(0x22FF0000);
        } else {
            tvSsuBig.setText(getString(R.string.ssu_unlocked));
            tvSsuBig.setTextColor(0xFF3FB950);
            tvSsuBig.setBackgroundColor(0x00000000);
        }

        // Show Exynos/MTK warning if applicable
        if (isNonQcom) {
            tvSsuWarning.setText(getString(R.string.inaccurate_warning));
            tvSsuWarning.setVisibility(View.VISIBLE);
        } else {
            tvSsuWarning.setVisibility(View.GONE);
        }
    }

    private void showLangPicker() {
        String current = getCurrentLang();
        new AlertDialog.Builder(this)
            .setTitle(current.equals("zh") ? "语言 / Language" : "Language / 语言")
            .setItems(new String[]{"English", "简体中文"}, (dialog, which) -> {
                String chosen = (which == 0) ? "en" : "zh";
                if (!chosen.equals(current)) {
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                        .edit().putString(PREF_LANG, chosen).apply();
                    recreate();
                }
            })
            .show();
    }

    private void applyLanguage() {
        Locale locale = getCurrentLang().equals("zh")
                ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getCurrentLang() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (prefs.contains(PREF_LANG)) {
            return prefs.getString(PREF_LANG, "en");
        }
        String sys = Locale.getDefault().getLanguage();
        return sys.equals("zh") ? "zh" : "en";
    }

    private void updateStringViews() {
        tvSectionDevice.setText(getString(R.string.section_device));
        tvSectionSsu.setText(getString(R.string.section_ssu));
        tvSectionKnox.setText(getString(R.string.section_knox));
        tvFooter.setText(getString(R.string.footer));
        cbRaw.setText(getString(R.string.show_raw));
        tvRawTitle.setText(getString(R.string.raw_title));
    }

    private void showGuide() {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.guide_title))
            .setMessage(getString(R.string.guide_body))
            .setPositiveButton(getString(R.string.guide_close), null)
            .show();
    }

    private void appendRaw(StringBuilder sb, String key, String val) {
        sb.append("[").append(key).append("]: [").append(val).append("]\n");
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            p.waitFor();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String orNA(String s) {
        return (s == null || s.isEmpty()) ? "N/A" : s;
    }
}
