package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.out.persistence.mapper.MemberMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.MemberJpaRepository;
import com.golajugaenyang.member.domain.entity.Member;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import com.golajugaenyang.member.error.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdapter implements MemberRepository {

    private static final String AUTH_ID_UNIQUE_CONSTRAINT = "uk_member_auth_id";
    private static final String NICKNAME_UNIQUE_CONSTRAINT = "uk_member_nickname";

    private final MemberJpaRepository memberJpaRepo;

    @Override
    public boolean existsByNickname(String nickname){
        return memberJpaRepo.existsByNickname(nickname);
    }

    @Override
    public Member save(Member member) {
        try {
            return MemberMapper.toDomain(memberJpaRepo.save(MemberMapper.toJpaEntity(member)));
        } catch (DataIntegrityViolationException e) {
            throw toAppException(e);
        }
    }

    @Override
    public boolean existsByAuthId(Long authId){
        return memberJpaRepo.existsByAuthId(authId);
    }

    private AppException toAppException(DataIntegrityViolationException e) {
        String constraintName = e.getCause() instanceof ConstraintViolationException cve
            ? cve.getConstraintName()
            : null;

        if (AUTH_ID_UNIQUE_CONSTRAINT.equals(constraintName)) {
            return new AppException(MemberErrorCode.ALREADY_SIGNED_UP);
        }
        if (NICKNAME_UNIQUE_CONSTRAINT.equals(constraintName)) {
            return new AppException(MemberErrorCode.ALREADY_HAVE_NICKNAME);
        }
        throw e;
    }

}

