package com.ip.processservice.operation;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@AllArgsConstructor
public class ResizeOperation implements ImageOperation {
    private Integer width;
    private Integer height;
    private Double scale;

    @Override
    public void apply(Thumbnails.Builder<?> builder) {
        if (scale != null) {
            builder.scale(scale);
        } else if (width != null && height != null) {
            builder.size(width, height);
            builder.keepAspectRatio(true);
        }
    }
}
