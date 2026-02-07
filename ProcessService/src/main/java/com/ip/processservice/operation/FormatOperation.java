package com.ip.processservice.operation;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@AllArgsConstructor
public class FormatOperation implements ImageOperation {
    private String format;
    @Override
    public void apply(Thumbnails.Builder<?> builder) {
        if (format != null) {
            builder.outputFormat(format);
        }
    }
}
