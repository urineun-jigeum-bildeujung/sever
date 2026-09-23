package com.golajugaenyang.order.application.image;


import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.storage.PresignedUpload;
import com.golajugaenyang.common.storage.PresignedUploadIssuer;
import com.golajugaenyang.order.application.image.port.in.IssueOrderImageUploadUseCase;
import com.golajugaenyang.order.application.image.port.in.OrderImageUploadResult;
import com.golajugaenyang.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class IssueOrderImageUploadService implements IssueOrderImageUploadUseCase {

    private final PresignedUploadIssuer presignedUploadIssuer;

    @Override
    public OrderImageUploadResult issueImageUploadUrl(Long memberId, String extension) {
        try {
            PresignedUpload upload = presignedUploadIssuer.issue("member-" + memberId, extension);
            return new OrderImageUploadResult(upload.uploadUrl(), upload.fileUrl());
        } catch (IllegalArgumentException e) {
            throw new AppException(OrderErrorCode.INVALID_IMAGE_EXTENSION);
        }
    }
}
