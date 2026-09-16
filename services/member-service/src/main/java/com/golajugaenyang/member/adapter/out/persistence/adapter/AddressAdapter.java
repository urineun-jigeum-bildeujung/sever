package com.golajugaenyang.member.adapter.out.persistence.adapter;

import com.golajugaenyang.member.adapter.out.persistence.entity.AddressJpaEntity;
import com.golajugaenyang.member.adapter.out.persistence.mapper.AddressMapper;
import com.golajugaenyang.member.adapter.out.persistence.repository.AddressJpaRepository;
import com.golajugaenyang.member.domain.entity.Address;
import com.golajugaenyang.member.domain.repository.AddressRepository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressAdapter implements AddressRepository {

    private final AddressJpaRepository addressJpaRepo;

    @Override
    public Address save(Address address){
        return AddressMapper.toDomain(addressJpaRepo.save(AddressMapper.toJpaEntity(address)));
    }

    @Override
    public boolean existsByMemberId(Long memberId){
        return addressJpaRepo.existsByMemberId(memberId);
    }

    @Override
    public Optional<Address> findDefaultByMemberId(Long memberId){
        return addressJpaRepo.findByMemberIdAndIsDefaultTrue(memberId).map(AddressMapper::toDomain);
    }

    @Override
    public List<Address> findAllByMemberId(Long memberId){
        return addressJpaRepo.findAllByMemberId(memberId).stream()
                .map(AddressMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Address> findById(Long addressId) {
        return addressJpaRepo.findById(addressId)
                .map(AddressMapper::toDomain);
    }

}
