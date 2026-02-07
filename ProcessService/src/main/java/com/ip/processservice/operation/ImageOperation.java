package com.ip.processservice.operation;

import net.coobird.thumbnailator.Thumbnails;

public interface ImageOperation {
    void apply(Thumbnails.Builder<?> builder);
}