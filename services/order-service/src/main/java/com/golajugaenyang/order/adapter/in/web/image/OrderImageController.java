package com.golajugaenyang.order.adapter.in.web.image;


import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.order.adapter.in.web.image.dto.OrderImageUploadRequest;
import com.golajugaenyang.order.adapter.in.web.image.dto.OrderImageUploadResponse;
import com.golajugaenyang.order.application.image.port.in.IssueOrderImageUploadUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders/images")
@RequiredArgsConstructor
public class OrderImageController implements OrderImageControllerDocs {

    private final IssueOrderImageUploadUseCase issueOrderImageUploadUseCase;

    @Override
    @PostMapping("/presigned-url")
    public ResponseEntity<OrderImageUploadResponse> issueImageUploadUrl(
        @MemberId Long memberId,
        @Valid @RequestBody OrderImageUploadRequest request
    ) {
        var result = issueOrderImageUploadUseCase.issueImageUploadUrl(
            memberId, request.extension());
        return ResponseEntity.ok(
            new OrderImageUploadResponse(result.uploadUrl(), result.fileUrl()));
    }
}
