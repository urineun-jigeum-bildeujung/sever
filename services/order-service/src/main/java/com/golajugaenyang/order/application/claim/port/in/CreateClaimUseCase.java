package com.golajugaenyang.order.application.claim.port.in;

import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimCommand;
import com.golajugaenyang.order.application.claim.port.in.dto.CreateClaimResult;

public interface CreateClaimUseCase {

    CreateClaimResult createClaim(CreateClaimCommand command);
}
