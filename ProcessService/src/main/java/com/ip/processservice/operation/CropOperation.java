package com.ip.processservice.operation;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@AllArgsConstructor
public class CropOperation implements ImageOperation {
    private int x, y, w, h;

    @Override
    public void apply(Thumbnails.Builder<?> builder) {
        builder.sourceRegion(x, y, w, h);
        builder.scale(1.0);
    }
}
