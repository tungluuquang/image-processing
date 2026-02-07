package com.ip.processservice.pattern;

import com.ip.processservice.dto.request.ProcessRequest;
import com.ip.processservice.operation.CompressOperation;
import com.ip.processservice.operation.CropOperation;
import com.ip.processservice.operation.ImageOperation;
import com.ip.processservice.operation.ResizeOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OperationFactory {

    public List<ImageOperation> getOperations(ProcessRequest req) {
        List<ImageOperation> ops = new ArrayList<>();

        switch (req.getAction().toUpperCase()) {
            case "CROP":
                ops.add(new CropOperation(req.getCropX(), req.getCropY(), req.getCropWidth(), req.getCropHeight()));
                break;

            case "RESIZE":
                ops.add(new ResizeOperation(req.getWidth(), req.getHeight(), req.getScale()));
                break;

            case "COMPRESS":
                ops.add(new CompressOperation(req.getQuality()));
                break;

            case "CONVERT":
                break;

            case "WATERMARK":
                // Logic load ảnh watermark sẽ phức tạp hơn chút, cần download logo về
                // ops.add(new WatermarkOperation(...));
                break;
        }

        // Mặc định luôn thêm Format nếu có yêu cầu
        if (req.getTargetFormat() != null) {
            ops.add(builder -> builder.outputFormat(req.getTargetFormat()));
        }

        return ops;
    }
}