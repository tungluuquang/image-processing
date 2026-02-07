package com.ip.processservice.operation;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@AllArgsConstructor
public class RotateOperation implements ImageOperation {
    private Double angle;

    @Override
    public void apply(Thumbnails.Builder<?> builder) {
        if (angle != null) {
            builder.rotate(angle);
        }
    }
}
