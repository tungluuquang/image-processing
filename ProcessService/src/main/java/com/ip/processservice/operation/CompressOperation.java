package com.ip.processservice.operation;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@AllArgsConstructor
public class CompressOperation implements ImageOperation {
    private Double quality;

    @Override
    public void apply(Thumbnails.Builder<?> builder) {
        if (quality != null) {
            builder.outputQuality(quality);
        }
    }
}