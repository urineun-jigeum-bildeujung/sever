package com.golajugaenyang.product.application.inventory.port.in;

import com.golajugaenyang.product.application.inventory.port.in.dto.ReserveItemCommand;
import com.golajugaenyang.product.domain.inventory.StockSubjectType;
import java.util.List;

public interface InventoryCommandUseCase {

    void reserveBulk(List<ReserveItemCommand> items);

    void confirm(StockSubjectType type, Long subjectId, Long orderItemId, int quantity);

    void release(StockSubjectType type, Long subjectId, Long orderItemId, int quantity);

    void restore(StockSubjectType type, Long subjectId, Long orderItemId, int quantity);
}
