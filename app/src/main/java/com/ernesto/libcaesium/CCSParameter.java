package com.ernesto.libcaesium;

public class CCSParameter {
    public boolean keep_metadata;
    public int jpeg_qu;
    public int png_qu;
    public boolean png_force_zopfli;
    public int gif_qu;
    public int webp_qu;
    public boolean optm;
    public int width;
    public int height;

    public CCSParameter(boolean keep_metadata, int jpeg_qu, int png_qu, boolean png_force_zopfli, int gif_qu,
                        int webp_qu, boolean optm, int width, int height) {
        super();
        this.keep_metadata = keep_metadata;
        this.jpeg_qu = jpeg_qu;
        this.png_qu = png_qu;
        this.png_force_zopfli = png_force_zopfli;
        this.gif_qu = gif_qu;
        this.webp_qu = webp_qu;
        this.optm = optm;
        this.width = width;
        this.height = height;
    }

}
