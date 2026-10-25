package com.noxira.store;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(9, 10, 18);
    private final int panel = Color.rgb(20, 22, 37);
    private final int purple = Color.rgb(139, 92, 246);
    private final int cyan = Color.rgb(56, 189, 248);
    private EditText search;
    private LinearLayout products;

    private static class Product {
        String name, category, description, price;
        Product(String n, String c, String d, String p) { name=n; category=c; description=d; price=p; }
    }
    private final Product[] catalog = {
        new Product("Roblox Gift Card", "Voucher", "Digital voucher • admin confirmation", "Rp 50.000"),
        new Product("Game Top Up", "Gaming", "Top up game favorit kamu", "Mulai Rp 15.000"),
        new Product("Premium App", "Digital", "Aplikasi dan layanan digital legal", "Mulai Rp 10.000"),
        new Product("Custom Digital Service", "Jasa", "Konsultasikan kebutuhan digital kamu", "Hubungi admin")
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildUi();
    }

    private GradientDrawable shape(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(18));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }
    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w < 0 ? w : dp(w), h < 0 ? h : dp(h)); }
    private void buildUi() {
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(bg);
        LinearLayout root = column(); root.setPadding(dp(20), dp(22), dp(20), dp(28)); scroll.addView(root);
        LinearLayout brand = new LinearLayout(this); brand.setGravity(Gravity.CENTER_VERTICAL);
        TextView mark = text("N", 24, Color.WHITE, true); mark.setGravity(Gravity.CENTER);
        mark.setBackground(shape(purple, 0)); brand.addView(mark, lp(48,48));
        LinearLayout names = column(); names.setPadding(dp(12),0,0,0);
        names.addView(text("NOXIRA", 19, Color.WHITE, true)); names.addView(text("STORE  /  DIGITAL GOODS", 10, cyan, true));
        brand.addView(names, new LinearLayout.LayoutParams(0, -2, 1));
        TextView status = text("● ONLINE", 11, Color.rgb(74,222,128), true); brand.addView(status); root.addView(brand);
        TextView hero = text("Upgrade your\ndigital world.", 32, Color.WHITE, true); hero.setPadding(0,dp(28),0,dp(8)); root.addView(hero);
        TextView sub = text("Produk digital pilihan. Aman, simpel, dibantu admin.", 14, Color.rgb(167,174,196), false); root.addView(sub);
        LinearLayout banner = column(); banner.setPadding(dp(18),dp(18),dp(18),dp(18)); banner.setBackground(shape(Color.rgb(31,25,58), purple));
        LinearLayout.LayoutParams bp=lp(-1,-2); bp.topMargin=dp(22); root.addView(banner,bp);
        banner.addView(text("NOXIRA SELECT", 11, cyan, true));
        TextView btitle=text("Belanja digital tanpa ribet",20,Color.WHITE,true); btitle.setPadding(0,dp(8),0,dp(5)); banner.addView(btitle);
        banner.addView(text("Pilih produk → kirim pesanan → tunggu konfirmasi admin.",13,Color.rgb(210,208,230),false));
        search = new EditText(this); search.setSingleLine(true); search.setTextColor(Color.WHITE); search.setHintTextColor(Color.rgb(130,137,160)); search.setHint("Cari produk digital...");
        search.setPadding(dp(15),0,dp(15),0); search.setBackground(shape(panel,Color.rgb(48,52,75)));
        LinearLayout.LayoutParams sp=lp(-1,52); sp.topMargin=dp(24); root.addView(search,sp);
        TextView section=text("Katalog pilihan",22,Color.WHITE,true); section.setPadding(0,dp(24),0,dp(12)); root.addView(section);
        products=column(); root.addView(products); renderProducts("");
        search.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            public void onTextChanged(CharSequence s,int start,int before,int count) { renderProducts(s.toString()); }
            public void afterTextChanged(android.text.Editable e) {}
        });
        TextView how=text("CARA ORDER",12,cyan,true); how.setPadding(0,dp(24),0,dp(8)); root.addView(how);
        root.addView(text("01  Pilih produk yang kamu inginkan\n02  Kirim detail pesanan ke admin\n03  Lakukan pembayaran setelah instruksi resmi\n04  Admin memverifikasi pembayaran dan pesanan",14,Color.rgb(183,189,209),false));
        TextView foot=text("NOXIRA STORE • Digital made simpler",11,Color.rgb(110,118,145),false); foot.setGravity(Gravity.CENTER); foot.setPadding(0,dp(26),0,0); root.addView(foot);
        setContentView(scroll);
    }
    private void renderProducts(String query) {
        products.removeAllViews();
        int shown=0;
        for (Product p: catalog) {
            String hay=(p.name+" "+p.category+" "+p.description).toLowerCase();
            if (!hay.contains(query.toLowerCase())) continue;
            shown++;
            LinearLayout card=column(); card.setPadding(dp(16),dp(16),dp(16),dp(16)); card.setBackground(shape(panel,Color.rgb(40,43,64)));
            LinearLayout.LayoutParams cp=lp(-1,-2); cp.bottomMargin=dp(12); products.addView(card,cp);
            TextView cat=text(p.category.toUpperCase(),10,cyan,true); card.addView(cat);
            TextView title=text(p.name,18,Color.WHITE,true); title.setPadding(0,dp(7),0,dp(5)); card.addView(title);
            card.addView(text(p.description,12,Color.rgb(160,168,191),false));
            LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(0,dp(14),0,0);
            row.addView(text(p.price,16,Color.rgb(196,181,253),true),new LinearLayout.LayoutParams(0,-2,1));
            Button order=new Button(this); order.setText("Pesan"); order.setTextColor(Color.WHITE); order.setAllCaps(false); order.setBackground(shape(purple,0));
            row.addView(order,lp(100,44)); order.setOnClickListener(v -> sendOrder(p));
            card.addView(row);
        }
        if (shown==0) products.addView(text("Produk tidak ditemukan. Coba kata kunci lain.",14,Color.LTGRAY,false));
    }
    private void sendOrder(Product p) {
        String message="Halo Admin NOXIRA STORE, saya ingin memesan:\nProduk: "+p.name+"\nHarga tertera: "+p.price+"\n\nMohon info stok, total pembayaran, dan cara konfirmasi. Saya akan membayar setelah mendapat instruksi resmi.";
        Intent share=new Intent(Intent.ACTION_SEND); share.setType("text/plain"); share.putExtra(Intent.EXTRA_SUBJECT,"Pesanan NOXIRA STORE"); share.putExtra(Intent.EXTRA_TEXT,message);
        try { startActivity(Intent.createChooser(share,"Kirim pesanan ke admin")); }
        catch(Exception e) { Toast.makeText(this,"Tidak ada aplikasi untuk mengirim pesanan.",Toast.LENGTH_LONG).show(); }
    }
}
