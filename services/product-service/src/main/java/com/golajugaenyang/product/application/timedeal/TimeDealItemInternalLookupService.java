package com.golajugaenyang.product.application.timedeal;


import com.golajugaenyang.product.application.timedeal.port.in.TimeDealItemInternalLookupUseCase;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalItem;
import com.golajugaenyang.product.application.timedeal.port.in.dto.TimeDealItemInternalLookupResult;
import com.golajugaenyang.product.application.timedeal.port.out.TimeDealItemBulkQueryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeDealItemInternalLookupService implements TimeDealItemInternalLookupUseCase {

    private final TimeDealItemBulkQueryRepository timeDealItemBulkQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public TimeDealItemInternalLookupResult getTimeDealItems(List<Long> timeDealItemIds) {
        if (timeDealItemIds == null || timeDealItemIds.isEmpty()) {
            return new TimeDealItemInternalLookupResult(List.of(), List.of());
        }

        List<TimeDealItemInternalItem> items = timeDealItemBulkQueryRepository
            .findAllByIds(timeDealItemIds)
            .stream()
            .map(TimeDealItemInternalItem::from)
            .toList();

        Set<Long> foundIds = items.stream()
            .map(TimeDealItemInternalItem::timeDealItemId)
            .collect(Collectors.toSet());
        List<Long> missingIds = timeDealItemIds.stream()
            .distinct()
            .filter(id -> !foundIds.contains(id))
            .toList();

        return new TimeDealItemInternalLookupResult(items, missingIds);
    }
}
