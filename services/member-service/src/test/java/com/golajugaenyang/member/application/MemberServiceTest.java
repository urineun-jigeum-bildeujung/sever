package com.golajugaenyang.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.storage.ObjectTagConfirmer;
import com.golajugaenyang.common.storage.PresignedUpload;
import com.golajugaenyang.common.storage.PresignedUploadIssuer;
import com.golajugaenyang.member.adapter.in.web.dto.request.MemberProfileUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.ProfileImageUploadResponse;
import com.golajugaenyang.member.adapter.out.client.AuthClient;
import com.golajugaenyang.member.domain.entity.Member;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import com.golajugaenyang.member.error.MemberErrorCode;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepo;
    @Mock
    private NicknameGenerator nicknameGenerator;
    @Mock
    private AuthClient authClient;
    @Mock
    private MemberSignupWriter memberSignupWriter;
    @Mock
    private PresignedUploadIssuer presignedUploadIssuer;
    @Mock
    private ObjectTagConfirmer objectTagConfirmer;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUpTransactionSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDownTransactionSynchronization() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    @DisplayName("presigned URL 발급에 성공하면 uploadUrl/fileUrl을 그대로 반환한다.")
    void issueProfileImageUploadUrl_returns_presigned_upload() {
        Long memberId = 1L;
        when(presignedUploadIssuer.issue("member-1", "jpg"))
            .thenReturn(new PresignedUpload(
                "https://upload.example.com",
                "https://image.leechs.shop/profiles/member-1/uuid.jpg"));

        ProfileImageUploadResponse response = memberService.issueProfileImageUploadUrl(memberId, "jpg");

        assertThat(response.uploadUrl()).isEqualTo("https://upload.example.com");
        assertThat(response.fileUrl()).isEqualTo("https://image.leechs.shop/profiles/member-1/uuid.jpg");
    }

    @Test
    @DisplayName("허용되지 않는 확장자면 INVALID_IMAGE_EXTENSION 예외로 변환한다.")
    void issueProfileImageUploadUrl_translates_illegal_argument_to_app_exception() {
        Long memberId = 1L;
        when(presignedUploadIssuer.issue("member-1", "exe"))
            .thenThrow(new IllegalArgumentException("허용되지 않는 확장자입니다: exe"));

        assertThatThrownBy(() -> memberService.issueProfileImageUploadUrl(memberId, "exe"))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(MemberErrorCode.INVALID_IMAGE_EXTENSION);
    }

    @Test
    @DisplayName("프로필 수정 시 image가 있으면 소유권을 검증하고, 커밋 후에 태그를 confirmed로 변경한다.")
    void updateProfile_confirms_tag_after_commit_when_image_present() {
        Long memberId = 1L;
        String fileUrl = "https://image.leechs.shop/profiles/member-1/uuid.jpg";
        Member member = new Member(memberId, "기존닉네임", null, null, null,
            null, null, null, null, null, 10L);

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(member));

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
            "새닉네임", "홍길동", LocalDate.of(1998, 1, 1), fileUrl);

        memberService.updateProfile(memberId, request);

        verify(objectTagConfirmer).validateOwnership(fileUrl, "member-" + memberId);
        verify(objectTagConfirmer, never()).confirm(any(), any());

        TransactionSynchronizationUtils.triggerAfterCommit();

        verify(objectTagConfirmer).confirm(fileUrl, "member-" + memberId);
    }

    @Test
    @DisplayName("프로필 수정 시 image가 없으면 태그 변경을 호출하지 않는다.")
    void updateProfile_does_not_confirm_when_image_absent() {
        Long memberId = 1L;
        Member member = new Member(memberId, "기존닉네임", null, null, null,
            null, null, null, null, null, 10L);

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(member));

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
            "새닉네임", "홍길동", LocalDate.of(1998, 1, 1), null);

        memberService.updateProfile(memberId, request);

        TransactionSynchronizationUtils.triggerAfterCommit();

        verify(objectTagConfirmer, never()).confirm(any(), any());
    }

    @Test
    @DisplayName("본인 소유가 아닌 이미지면 FORBIDDEN_IMAGE로 변환하고 저장하지 않는다.")
    void updateProfile_translates_ownership_mismatch_and_skips_save() {
        Long memberId = 1L;
        String fileUrl = "https://image.leechs.shop/profiles/member-99/uuid.jpg";
        Member member = new Member(memberId, "기존닉네임", null, null, null,
            null, null, null, null, null, 10L);

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(member));
        doThrow(new IllegalArgumentException("이 파일에 대한 권한이 없습니다"))
            .when(objectTagConfirmer).validateOwnership(fileUrl, "member-" + memberId);

        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest(
            "새닉네임", "홍길동", LocalDate.of(1998, 1, 1), fileUrl);

        assertThatThrownBy(() -> memberService.updateProfile(memberId, request))
            .isInstanceOf(AppException.class)
            .extracting(e -> ((AppException) e).getErrorCode())
            .isEqualTo(MemberErrorCode.FORBIDDEN_IMAGE);

        verify(memberRepo, never()).save(any());
    }
}
