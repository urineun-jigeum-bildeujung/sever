package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {

    Address save(Address address);

    boolean existsByMemberId(Long memberId);

    Optional<Address> findDefaultByMemberId(Long memberId);

    List<Address> findAllByMemberId(Long memberId);
}
