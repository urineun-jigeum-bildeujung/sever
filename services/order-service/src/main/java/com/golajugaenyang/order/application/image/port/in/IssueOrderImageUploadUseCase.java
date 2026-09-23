package com.golajugaenyang.order.application.image.port.in;

public interface IssueOrderImageUploadUseCase {

    OrderImageUploadResult issueImageUploadUrl(Long memberId, String extension);
}
